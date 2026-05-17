package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 菜单按钮绑定行或角色选择器行：联合查询结果。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "菜单按钮与字典、菜单名称的联合展示")
public class MenuButtonPickerRowVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "cmn_menu_button.id")
	private Long menuButtonId;

	@Schema(description = "cmn_button.id，角色选择器场景可能为空")
	private Long dictButtonId;

	@Schema(description = "所属菜单 id")
	private Long menuId;

	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "按钮编码")
	private String buttonCode;

	@Schema(description = "按钮名称")
	private String buttonName;
}
