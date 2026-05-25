package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MenuButtonRoleVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "当前角色ID")
	private Long roleId;

	@Schema(description = "所属菜单 ID（cmn_menu.id）")
	private Long menuId;

	@Schema(description = "菜单 ID（cmn_button.id）")
	private Long buttonId;

	@Schema(description = "按钮编码：如 SYS_USER_ADD、SYS_USER_DELETE")
	private String buttonCode;

	@Schema(description = "按钮名称：如 新增、删除、导出")
	private String buttonName;

	@Schema(description = "是否具备权限，true具备，false不具备--前端勾选对应按钮，那么该值为true，没有勾选该值为false")
	private Boolean permission;

}
