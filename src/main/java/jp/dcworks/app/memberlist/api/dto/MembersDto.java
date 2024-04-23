package jp.dcworks.app.memberlist.api.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * メンバーDTOクラス。
 *
 * @author tomo-sato
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MembersDto {

	/** ID */
	private Long id;

	/** 名前 */
	private String name;

	/** メールアドレス */
	private String emailAddress;

	/** 作成日時 */
	private Date created;

	/** 更新日時 */
	private Date updated;

	/** 作成日時（yyyy-MM-dd HH:mm:ss） */
	private String formatedCreated;

	/** 更新日時（yyyy-MM-dd HH:mm:ss） */
	private String formatedUpdated;


	public String getFormatedCreated() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.formatedCreated = sdf.format(this.created);
		return this.formatedCreated;
	}

	public String getFormatedUpdated() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.formatedUpdated = sdf.format(this.updated);
		return this.formatedUpdated;
	}
}
