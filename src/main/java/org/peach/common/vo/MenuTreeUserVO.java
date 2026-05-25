package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import org.peach.common.utils.annotation.TreeChildren;
import org.peach.common.utils.annotation.TreeId;
import org.peach.common.utils.annotation.TreeParentId;
import org.peach.common.utils.annotation.TreeSortField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 
 * @Title: MenuTreeUserVO.java
 * @Description: 用户菜单树数据传输对象用于登陆后获取菜单按钮权限
 * @author: leiyangjun
 * @date: 2026年5月25日 21:34:11
 */
@Data
public class MenuTreeUserVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@TreeId
	@Schema(description = "主键：雪花 64 位，对应 Java long")
	private Long id;

	@Schema(description = "父菜单 ID：根节点可为 0 或 NULL")
	@TreeParentId
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
	
	@TreeSortField
	@Schema(description = "同级排序号，越小越靠前")
	private Integer orderNo;

	@Schema(description = "是否超级管理员")
	private boolean admin;

	/** 菜单树子节点；列表/详情无树语义时可不传或为 null */
	@Schema(description = "子菜单列表（仅树接口填充）")
	@TreeChildren
	private List<MenuTreeUserVO> children;

	/**
	 * 
	 */
	@Schema(description = "该菜单下能访问的按钮明细列表")
	private List<ButtonUserVO> buttons;

}
