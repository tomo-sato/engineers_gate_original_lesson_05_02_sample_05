package jp.dcworks.app.memberlist.controller;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.dcworks.app.memberlist.AppPageWrapper;
import jp.dcworks.app.memberlist.entity.Members;
import jp.dcworks.app.memberlist.service.MembersService;
import lombok.extern.log4j.Log4j2;

/**
 * ホーム画面コントローラー。
 *
 * @author tomo-sato
 */
@Log4j2
@Controller
@RequestMapping("/")
public class HomeController {

	/** メンバー関連サービスクラス。 */
	@Autowired
	private MembersService membersService;

	/**
	 * [GET]メンバー一覧画面のアクション。
	 *
	 * @param isSuccess 削除完了フラグ
	 * @param model 入力フォームのオブジェクト
	 * @return テンプレートpath
	 */
	@GetMapping(path = { "", "/" })
	public String index(@RequestParam(defaultValue = "0") String page,
			@ModelAttribute("isSuccess") String isSuccess,
			Model model) {

		log.info("メンバー一覧画面のアクションが呼ばれました。");

		int ipage = NumberUtils.toInt(page, 0);

		// メンバーを取得する。
		Page<Members> pageMembers = membersService.findAllMembers(ipage);
		AppPageWrapper<Members> pager = new AppPageWrapper<Members>(pageMembers);

		List<Members> membersList = pager.getContent();
		Long sinceId = 0L;
		for (Members members : membersList) {
			sinceId = (sinceId < members.getId()) ? members.getId() : sinceId;
		}

		model.addAttribute("membersList", membersList);
		model.addAttribute("sinceId", sinceId);
		model.addAttribute("pager", pager);
		model.addAttribute("isSuccess", BooleanUtils.toBoolean(isSuccess));

		return "home/index";
	}

	@GetMapping("/test/shoulder")
	public String testShoulder(Model model) {
		Page<Members> membersList = membersService.findAllMembers(0);
		AppPageWrapper<Members> pager = new AppPageWrapper<Members>(membersList);
		model.addAttribute("pager", pager);
		return "common/shoulder_fragment";
	}
}
