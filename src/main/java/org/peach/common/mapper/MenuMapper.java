package org.peach.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.peach.common.entity.Menu;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}
