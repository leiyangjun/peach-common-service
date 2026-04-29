package org.peach.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/** 依据 {@link org.peach.common.entity.Menu} 生成的对外 VO，不同步时手工改。
 */
@Data
public class MenuVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "父菜单 ID：根节点可为 0 或 NULL")
	private Long parentId;

	@Schema(description = "菜单编码：如 SYS_USER_LIST、SYS_USER_ADD")
	private String menuCode;

	@Schema(description = "菜单名称")
	private String menuName;

	@Schema(description = "菜单类型：CATALOG=目录，MENU=菜单，BUTTON=按钮")
	private String menuType;

	@Schema(description = "前端路由路径")
	private String routePath;

	@Schema(description = "前端组件路径")
	private String componentPath;

	@Schema(description = "菜单图标")
	private String icon;

	@Schema(description = "同级排序号，越小越靠前")
	private Integer orderNo;

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

