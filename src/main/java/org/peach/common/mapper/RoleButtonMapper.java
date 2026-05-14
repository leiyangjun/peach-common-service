package org.peach.common.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.peach.common.entity.RoleButton;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface RoleButtonMapper extends BaseMapper<RoleButton> {

	/** 按角色物理清理关联行（删除角色前调用） */
	@Delete("DELETE FROM cmn_role_button WHERE role_id = #{roleId}")
	int physicalDeleteByRoleId(@Param("roleId") Long roleId);
}