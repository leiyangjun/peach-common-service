package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.RoleButton} 生成的对外 VO，不同步时手工改。
 */
@Data
public class RoleButtonVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "关系编码：如 RB_ROLE_ADMIN_SYS_USER_ADD")
	private String relationCode;

	@Schema(description = "角色 ID（cmn_role.id）")
	private Long roleId;

	@Schema(description = "按钮 ID（cmn_menu_button.id）")
	private Long buttonId;

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

