package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 运维目录新建入参：仅白名单字段；menuCode/routePath 由服务端生成。
 *
 * @author leiyangjun
 * @date 2026-05-26
 */
@Data
@Schema(description = "运维目录新建")
public class MenuOpsSaveVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@NotNull(message = "父菜单不能为空")
	@Schema(description = "父菜单 ID，根节点传 0")
	private Long parentId;

	@NotBlank(message = "菜单名称不能为空")
	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "菜单类型：仅 CATALOG（可省略，默认目录）")
	private String menuType;

	@Schema(description = "同级排序号")
	private Integer orderNo;

	@Schema(description = "图标组件名")
	private String icon;

	@Schema(description = "是否有效：1=有效 0=无效")
	private Short valid;

	@Schema(description = "备注")
	private String remark;
}
