package org.peach.common.service;

import java.util.List;

import org.peach.common.vo.MenuVO;
import org.peach.common.mybatis.service.BaseInterfaceService;

/**
 * 业务服务接口：通用 CRUD 见 {@link BaseInterfaceService}，此处仅可追加扩展方法。
 */
public interface MenuService extends BaseInterfaceService<MenuVO> {

	/**
	 * 查询有效菜单（{@code valid=1}）并组装为树（不分页）。
 *
 * @author leiyangjun
 */
	List<MenuVO> listMenuTreeValid();

	/**
	 * 查询全部菜单记录并组装为树（不分页），不过滤有效标记。
 *
 * @author leiyangjun
 */
	List<MenuVO> listMenuTreeAll();

	/**
	 * 按主键物理删除菜单；存在子菜单时不允许删除。
 *
 * @author leiyangjun
 */
	void deletePhysically(Long id);
}
