package org.peach.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.peach.common.entity.MenuButton;
import org.peach.common.mybatis.mapper.BaseMapper;
import org.peach.common.vo.MenuButtonPickerRowVO;

@Mapper
public interface MenuButtonMapper extends BaseMapper<MenuButton> {

	@Select("SELECT mb.id AS menu_button_id, b.id AS dict_button_id, mb.button_code, mb.button_name, mb.menu_id, m.menu_name "
			+ "FROM cmn_menu_button mb INNER JOIN cmn_button b ON b.button_code = mb.button_code "
			+ "INNER JOIN cmn_menu m ON m.id = mb.menu_id "
			+ "WHERE mb.menu_id = #{menuId} AND mb.valid = 1 ORDER BY mb.order_no, mb.id")
	List<MenuButtonPickerRowVO> listBindRowsByMenuId(@Param("menuId") Long menuId);

	@Select("SELECT mb.id FROM cmn_menu_button mb WHERE mb.menu_id = #{menuId}")
	List<Long> listIdsByMenuId(@Param("menuId") Long menuId);

	@Select("SELECT mb.id AS menu_button_id, b.id AS dict_button_id, mb.menu_id AS menu_id, m.menu_name AS menu_name, "
			+ "mb.button_code AS button_code, mb.button_name AS button_name "
			+ "FROM cmn_menu_button mb INNER JOIN cmn_menu m ON m.id = mb.menu_id "
			+ "INNER JOIN cmn_button b ON b.button_code = mb.button_code "
			+ "WHERE mb.valid = 1 AND m.valid = 1 AND m.menu_type IN ('MENU', 'CATALOG') "
			+ "ORDER BY m.order_no, m.id, mb.order_no, mb.id")
	List<MenuButtonPickerRowVO> listAllMenuButtonsForRolePicker();

	@Delete("DELETE FROM cmn_menu_button WHERE menu_id = #{menuId}")
	int physicalDeleteByMenuId(@Param("menuId") Long menuId);
}