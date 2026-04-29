package org.peach.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.peach.common.entity.Role;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
