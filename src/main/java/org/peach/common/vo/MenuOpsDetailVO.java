package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 运维目录详情：含服务端生成的 menuCode、routePath。
 *
 * @author leiyangjun
 * @date 2026-05-26
 */
@Data
@Schema(description = "运维目录详情")
public class MenuOpsDetailVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键")
	private Long id;

	@Schema(description = "父菜单 ID")
	private Long parentId;

	@Schema(description = "菜单编码（只读）")
	private String menuCode;

	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "菜单类型：CATALOG=目录，MENU=菜单")
	private String menuType;

	@Schema(description = "路由路径（只读）")
	private String routePath;

	@Schema(description = "图标")
	private String icon;

	@Schema(description = "排序号")
	private Integer orderNo;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "是否有效")
	private Short valid;

	@Schema(description = "创建时间")
	private Date createTime;

	@Schema(description = "更新时间")
	private Date editTime;
}
