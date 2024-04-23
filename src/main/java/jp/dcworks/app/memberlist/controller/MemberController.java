package jp.dcworks.app.memberlist.controller;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.dcworks.app.memberlist.AppNotFoundException;
import jp.dcworks.app.memberlist.dto.RequestMember;
import jp.dcworks.app.memberlist.entity.Members;
import jp.dcworks.app.memberlist.service.MembersService;
import jp.dcworks.app.memberlist.util.StringUtil;
import lombok.extern.log4j.Log4j2;

/**
 * メンバー登録画面コントローラー。
 *
 * @author tomo-sato
 */
@Log4j2
@Controller
@RequestMapping("/member")
public class MemberController {

	/** メンバー関連サービスクラス。 */
	@Autowired
	private MembersService membersService;

	/**
	 * [GET]メンバー入力フォームのアクション。
	 *
	 * @param model 入力フォームのオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping(path = { "", "/" })
	public String index(Model model) {

		log.info("メンバー入力画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestMember")) {
			model.addAttribute("requestMember", new RequestMember());
		}
		return "/member/index";
	}

	/**
	 * [GET]メンバー入力フォームのアクション。
	 *
	 * @param membersId メンバーID
	 * @param model 入力フォームのオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping("/{membersId}")
	public String index(@PathVariable Long membersId, Model model) {

		log.info("メンバー入力画面のアクションが呼ばれました。");

		if (!model.containsAttribute("requestMember")) {
			model.addAttribute("requestMember", new RequestMember());
		}

		// メンバー一覧情報を取得する。
		Members members = membersService.findById(membersId);

		if (members == null) {
			// メンバーが取得できない場合は、Not Found。
			throw new AppNotFoundException();
		}

		RequestMember requestMember = new RequestMember();
		requestMember.setName(members.getName());
		requestMember.setEmailAddress(members.getEmailAddress());
		model.addAttribute("requestMember", requestMember);
		model.addAttribute("membersId", members.getId());
		model.addAttribute("isModify", true);

		return "/member/index";
	}

	/**
	 * [POST]メンバー登録アクション。
	 *
	 * @param requestMember 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 * @return テンプレートpath
	 */
	@PostMapping("/regist/")
	public String regist(@Validated @ModelAttribute RequestMember requestMember,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("メンバー登録処理のアクションが呼ばれました。：requestMember={}", requestMember);

		// バリデーション。
		if (result.hasErrors()) {
			// javascriptのバリデーションを改ざんしてリクエストした場合に通る処理。
			log.warn("バリデーションエラーが発生しました。：requestMember={}, result={}", requestMember, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestMember", requestMember);

			// 入力画面へリダイレクト。
			return "redirect:/member";
		}

		// メンバー検索を行う。（※「メールアドレス」で検索を行い、すでに登録済みの場合エラー。）
		List<Members> membersList = membersService.findByEmailAddress(requestMember.getEmailAddress());

		if (membersList != null && !membersList.isEmpty()) {
			log.warn("すでに登録済みのメンバーです。：requestMember={}", requestMember);

			// エラーメッセージをセット。
			result.rejectValue("emailAddress", StringUtil.BLANK, "指定のメールアドレスのメンバーは、すでに登録されています。");

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestMember", requestMember);

			// 入力画面へリダイレクト。
			return "redirect:/member";
		}

		// データ登録処理。
		membersService.save(requestMember);

		redirectAttributes.addFlashAttribute("isSuccess", "true");

		// 完了画面へリダイレクト。
		return "redirect:/member/complete";
	}

	/**
	 * [POST]メンバー編集アクション。
	 *
	 * @param membersId メンバーID
	 * @param requestMember 入力フォームの内容
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 * @return テンプレートpath
	 */
	@PostMapping("/regist/{membersId}")
	public String regist(@PathVariable Long membersId,
			@Validated @ModelAttribute RequestMember requestMember,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("メンバー編集処理のアクションが呼ばれました。：requestMember={}", requestMember);

		// バリデーション。
		if (result.hasErrors()) {
			// javascriptのバリデーションを改ざんしてリクエストした場合に通る処理。
			log.warn("バリデーションエラーが発生しました。：requestMember={}, result={}", requestMember, result);

			redirectAttributes.addFlashAttribute("validationErrors", result);
			redirectAttributes.addFlashAttribute("requestMember", requestMember);

			// 入力画面へリダイレクト。
			return "redirect:/member";
		}

		// メンバー検索を行う。
		Members members = membersService.findById(membersId);

		// メンバーが取得できなかったらエラー。
		if (members == null) {
			// メンバーが取得できない場合は、Not Found。
			throw new AppNotFoundException();
		}

		members.setName(requestMember.getName());
		members.setEmailAddress(requestMember.getEmailAddress());

		// データ登録処理。
		membersService.save(members);

		redirectAttributes.addFlashAttribute("isSuccess", "true");

		// 完了画面へリダイレクト。
		return "redirect:/member/complete";
	}

	/**
	 * [GET]完了アクション。
	 *
	 * @param requestMember 入力フォームの内容
	 * @param isSuccess 正常の遷移であるか、否か。（true.正常、false.不正アクセス）
	 * @param result バリデーション結果
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping("/complete")
	public String complete(@ModelAttribute("requestMember") RequestMember requestMember,
			@ModelAttribute("isSuccess") String isSuccess,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		log.info("メンバー登録完了画面のアクションが呼ばれました。");

		// 不正アクセスはトップへリダイレクト。（※直接URLを叩いての不正アクセスを制御。）
		if (!BooleanUtils.toBoolean(isSuccess)) {

			log.warn("不正なアクセスがありました。：requestMember={}", requestMember);
			result.rejectValue("", StringUtil.BLANK, "不正なアクセスがありました。");

			redirectAttributes.addFlashAttribute("validationErrors", result);

			// 入力画面へリダイレクト。
			return "redirect:/member";
		}

		log.info("リダイレクトパラメータ。：requestMember={}, isSuccess={}", requestMember, isSuccess);
		return "/member/complete";
	}

	/**
	 * [GET]削除アクション。
	 * @param membersId メンバーID
	 * @param redirectAttributes リダイレクト時に使用するオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping("/delete/{membersId}")
	public String delete(@PathVariable Long membersId,
			RedirectAttributes redirectAttributes) {

		// メンバー検索を行う。
		Members members = membersService.findById(membersId);

		// メンバーが取得できなかったらエラー。
		if (members == null) {
			// メンバーが取得できない場合は、Not Found。
			throw new AppNotFoundException();
		}

		membersService.delete(members);

		redirectAttributes.addFlashAttribute("isSuccess", "true");
		return "redirect:/";
	}
}
