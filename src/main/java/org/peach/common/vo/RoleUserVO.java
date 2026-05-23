package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.peach.common.mybatis.annotation.ID;
import org.peach.common.mybatis.annotation.TableName;

/**
 * 角色与用户多对多关联实体，对应表 {@code cmn_role_user}。
 *
 * @author leiyangjun
 */
@Data
@TableName("cmn_role_user")
@Schema(description = "角色-用户关联")
public class RoleUserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位")
	@ID
	private Long id;

	@Schema(description = "角色 ID")
	private Long roleId;

	@Schema(description = "用户 ID")
	private Long userId;

	@Schema(description = "创建时间")
	private Date createTime;
}
