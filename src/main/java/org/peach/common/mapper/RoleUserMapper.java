package org.peach.common.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.peach.common.entity.RoleUser;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface RoleUserMapper extends BaseMapper<RoleUser> {

	@Delete("DELETE FROM cmn_role_user WHERE role_id = #{roleId}")
	int deleteByRoleId(@Param("roleId") Long roleId);

	@Select("SELECT user_id FROM cmn_role_user WHERE role_id = #{roleId} ORDER BY user_id")
	List<Long> listUserIdsByRoleId(@Param("roleId") Long roleId);
}
