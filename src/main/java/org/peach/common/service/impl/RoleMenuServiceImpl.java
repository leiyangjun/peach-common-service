package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.RoleMenuMapper;
import org.peach.common.entity.RoleMenu;
import org.peach.common.vo.RoleMenuVO;
import org.peach.common.service.RoleMenuService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class RoleMenuServiceImpl extends BaseAbstractService<RoleMenuMapper, RoleMenu, RoleMenuVO> implements RoleMenuService {

	public RoleMenuServiceImpl(RoleMenuMapper mapper) {
		super(mapper, RoleMenu.class, RoleMenuVO.class);
	}
}

