package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.RoleMapper;
import org.peach.common.entity.Role;
import org.peach.common.vo.RoleVO;
import org.peach.common.service.RoleService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class RoleServiceImpl extends BaseAbstractService<RoleMapper, Role, RoleVO> implements RoleService {

	public RoleServiceImpl(RoleMapper mapper) {
		super(mapper, Role.class, RoleVO.class);
	}
}

