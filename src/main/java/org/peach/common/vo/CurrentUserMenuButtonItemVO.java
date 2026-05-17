package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前用户在某菜单下已授权的按钮（扁平列表，供前端按 routePath / menuCode 校验显隐）。
 */
@Data
@Schema(description = "当前用户菜单按钮授权项")
public class CurrentUserMenuButtonItemVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "cmn_menu_button.id")
	private Long menuButtonId;

	@Schema(description = "所属菜单 id")
	private Long menuId;

	@Schema(description = "菜单编码")
	private String menuCode;

	@Schema(description = "菜单路由路径")
	private String routePath;

	@Schema(description = "按钮编码，如 BTN_ADD")
	private String buttonCode;

	@Schema(description = "按钮名称")
	private String buttonName;
}
