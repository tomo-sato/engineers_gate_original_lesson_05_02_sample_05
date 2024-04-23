package jp.dcworks.app.memberlist.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jp.dcworks.app.memberlist.AppPageWrapper;
import jp.dcworks.app.memberlist.api.MemberApiController.Response.PageInfo;
import jp.dcworks.app.memberlist.api.dto.MembersDto;
import jp.dcworks.app.memberlist.entity.Members;
import jp.dcworks.app.memberlist.service.MembersService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * メンバーに関するRestAPI.
 *
 * @author tomo-sato
 */
@Log4j2
@RestController
@RequestMapping("/api")
public class MemberApiController {

	/** メンバー関連サービスクラス。 */
	@Autowired
	private MembersService membersService;


	/**
	 * メンバーを取得する。
	 *
	 * @param sinceid 指定されたIDよりも新しいものを取得する。
	 * @return
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = "/getMembers")
	public Response getMembers(@RequestParam Long sinceId) throws JsonProcessingException {
		Response response = new Response(0);
		PageInfo pageInfo = new PageInfo();
		System.out.println(sinceId);

		// メンバーを取得する。
		Page<Members> pageMembers = membersService.findAllMembers(sinceId, 0);
		AppPageWrapper<Members> pager = new AppPageWrapper<Members>(pageMembers);
		log.info(pageMembers);

		List<Members> membersList = pageMembers.getContent();

		Long retSinceId = 0L;
		for (Members members : membersList) {
			retSinceId = (retSinceId < members.getId()) ? members.getId() : retSinceId;
		}

		pageInfo.setHasNext(pager.isHasNextPage());
		pageInfo.setSinceId(retSinceId);

		// マッパーオブジェクトを使ってDTOにマッピング。
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		String jsonAsString = mapper.writeValueAsString(pageMembers.getContent());
		List<MembersDto> data = mapper.readValue(jsonAsString, new TypeReference<List<MembersDto>>() {});

		response.setData(data);
		response.setPageInfo(pageInfo);

		log.info(response);
		return response;
	}

	/**
	 * レスポンスデータ。
	 *
	 * @author tomo-sato
	 */
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class Response {

		/** レスポンスコード：0.成功、-1.失敗 */
		private Integer responseCode;

		/** ページ情報。 */
		private PageInfo pageInfo;

		/** 取得結果。 */
		private List<MembersDto> data;

		/**
		 * コンストラクタ。
		 *
		 * @param responseCode レスポンスコード
		 */
		public Response(Integer responseCode) {
			this.responseCode = responseCode;
		}

		/**
		 * ページ情報。
		 *
		 * @author tomo-sato
		 */
		@Data
		@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
		public static class PageInfo {

			/** 指定されたIDよりも新しいものを取得する為のID。 */
			private Long sinceId = 0L;

			/** 次のページがあるか、否か。 */
			private boolean hasNext = false;
		}
	}
}
