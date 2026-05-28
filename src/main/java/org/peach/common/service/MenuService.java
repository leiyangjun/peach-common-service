package org.peach.common.service;

import java.util.List;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.MenuInfoVO;
import org.peach.common.vo.MenuOpsPatchVO;
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
	
	/**
	 * 
	 * @Title: editParentId   
	 * @Description: 调整父级菜单ID   
	 * @param: @param menuId
	 * @param: @param parentId      
	 * @return: void      
	 * @throws
	 */
	void editParentId(Long menuId,Long parentId);

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

	/** 运维菜单详情（CATALOG / MENU） */
	MenuVO getMenuOpsDetail(Long menuId);

	/** 新建运维菜单（默认 CATALOG；CATALOG 自动插入 BTN_QUERY） */
	void createMenuOpsCatalog(MenuVO menuVO);

	/** 局部更新运维菜单（不可改 menuType；不删 menu_button） */
	void patchMenuOps(Long menuId, MenuOpsPatchVO patchVO);

	/**
	 * 物理删除运维目录：仅 CATALOG、无子节点；菜单类型不可删。
	 *
	 * @param menuId 目录主键
	 */
	void deleteMenuOpsById(Long menuId);
}
