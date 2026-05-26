package org.peach.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.peach.common.entity.Application;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}