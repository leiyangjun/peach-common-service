package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.User} 生成的对外 VO，不同步时手工改。
 */
@Data
public class UserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "主体：INTERNAL=系统侧（运营/员工等），CUSTOMER=C 端消费者")
	private String subjectType;

	@Schema(description = "登录名；系统侧必填（由约束保证），C 端可空（手机/三方为主）")
	private String username;

	@Schema(description = "密码摘要；免密或纯三方登录可为空")
	private String password;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "真实姓名或对内展示名")
	private String realName;

	@Schema(description = "手机号")
	private String mobile;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "头像地址")
	private String avatarUrl;

	@Schema(description = "性别：0 未知，1 男，2 女")
	private Short gender;

	@Schema(description = "首次注册终端：如 ADMIN_WEB、MINI_APP、APP、OPEN_API（与网关/客户端约定枚举）")
	private String registerClient;

	@Schema(description = "最近登录时间")
	private Date lastLoginTime;

	@Schema(description = "最近登录终端，含义同 register_client")
	private String lastLoginClient;

	@Schema(description = "开放体系全局标识预留（如对接方用户 id、OAuth subject 等）；细粒度授权建议另建关联表")
	private String openUnionId;

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

