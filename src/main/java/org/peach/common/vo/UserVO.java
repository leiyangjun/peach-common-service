package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.entity.User;

/** 依据 {@link User} 生成的对外 VO，不同步时手工改。 */
@Data
public class UserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键 id")
	private Long id;

	@Schema(description = "主体类型")
	private String subjectType;

	@Schema(description = "登录名")
	private String username;

	@Schema(description = "密码（接口层通常不回传）")
	private String password;

	@Schema(description = "昵称")
	private String nickname;

	@Schema(description = "真实姓名")
	private String realName;

	@Schema(description = "手机号")
	private String mobile;

	@Schema(description = "邮箱")
	private String email;

	@Schema(description = "头像地址")
	private String avatarUrl;

	@Schema(description = "性别：0 未知，1 男，2 女")
	private Integer gender;

	@Schema(description = "首次注册终端")
	private String registerClient;

	@Schema(description = "最近登录时间")
	private Date lastLoginTime;

	@Schema(description = "最近登录终端")
	private String lastLoginClient;

	@Schema(description = "开放体系用户标识")
	private String openUnionId;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "是否有效")
	private String valid;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "最后更新时间")
	private Date editTime;
}
