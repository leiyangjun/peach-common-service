package org.peach.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.peach.common.entity.Button;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface ButtonMapper extends BaseMapper<Button> {
}