package org.peach.common.service;

import java.util.List;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuTreeVO;
import org.peach.common.vo.MenuVO;

/**
 * 业务服务接口：通用 CRUD 见 {@link BaseInterfaceService}，此处仅可追加扩展方法。
 */
public interface MenuService extends BaseInterfaceService<MenuVO> {

	/**
	 * 
	 * @Title: saveOrUpdate
	 * @Description: 保存或更新菜单
	 * @param: @param menuInfoVO
	 * @return: void
	 * @throws
	 */
	void saveOrUpdate(MenuInfoVO menuInfoVO);

	MenuInfoVO getMenuInfoById(Long menuId);

	/**
	 * 
	 * @Title: getMenuTreeAll
	 * @Description: 获取所有菜单树包含不显示菜单
	 * @param: @return
	 * @return: List<MenuVO>
	 * @throws
	 */
	List<MenuTreeVO> getMenuTreeAll();

	/**
	 * 
	 * @Title: deleteMenuById
	 * @Description: 物理删除该菜单
	 * @param: @param id
	 * @return: void
	 * @throws
	 */
	void deleteMenuById(Long menuId);
}
