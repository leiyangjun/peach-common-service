package org.peach.common.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前登录用户的菜单树与按钮授权（无关联查询，由用户→角色→角色按钮→菜单按钮分步组装）。
 */
@Data
@Schema(description = "当前用户菜单与按钮权限")
public class CurrentUserPermissionVO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "当前用户可见菜单树（含祖先目录）")
	private List<MenuVO> menuTree;

	@Schema(description = "当前用户已授权菜单按钮扁平列表")
	private List<CurrentUserMenuButtonItemVO> menuButtons;
}
