package org.peach.common.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.peach.common.mvc.annotation.json.Sensitive;
import org.peach.common.mvc.annotation.json.SensitiveType;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 用户对外 VO（无 Bean Validation，由业务层按场景处理）。 */
@Data
public class UserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "用户类型：system=系统侧，app=应用端")
	private String userType;

	@Schema(description = "登录名；系统侧必填（由约束保证），app 端可空（手机/三方为主）")
	private String username;

	/**
	 * 密码摘要：与 {@link org.peach.common.entity.User#password} 同列，仅存哈希（如 BCrypt）。
	 * <p>
	 * JSON 写出时经 {@link Sensitive} 掩码展示；库内与内存仍为完整摘要。实体无单独
	 * {@code passwordDigest} 字段名时，本成员即「摘要」唯一载体。
	 * </p>
 *
 * @author leiyangjun
 */
	@Sensitive(SensitiveType.CUSTOM)
	@Schema(description = "密码摘要（JSON 掩码展示，不落明文）")
	private String password;

	/**
	 * 明文口令：仅请求体写入；响应 JSON 不序列化，故无需 {@link Sensitive}。
	 * 与 {@link #password}（摘要）区分：前者不入库、不出响应，后者为库中哈希。
 *
 * @author leiyangjun
 */
	@Schema(description = "明文口令：仅新增或修改密码时传入，不落库")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String plainPassword;

	private String nickname;

	@Schema(description = "真实姓名或对内展示名")
	private String realName;

	@Schema(description = "手机号")
	private String mobile;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "头像地址或对象存储键")
	private String avatar;

	@Schema(description = "性别：0 未知，1 男，2 女")
	private Short gender;

	@Schema(description = "证件类型编码，可空")
	private String certType;

	@Schema(description = "证件号码，可空")
	private String certNo;

	@Schema(description = "最近登录时间")
	private Date lastLoginTime;

	@Schema(description = "最近登录终端")
	private String lastLoginClient;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "是否有效：1=有效 0=无效（逻辑删除，SMALLINT）")
	private Short valid;

	@Schema(description = "创建人 ID：雪花 64 位，对应 Java long")
	private Long creator;

	@Schema(description = "修改人 ID：雪花 64 位，对应 Java long")
	private Long editor;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}
