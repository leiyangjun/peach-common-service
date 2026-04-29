package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.entity.RoleButton;
import org.peach.common.vo.RoleButtonVO;
import org.peach.common.service.RoleButtonService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class RoleButtonServiceImpl extends BaseAbstractService<RoleButtonMapper, RoleButton, RoleButtonVO> implements RoleButtonService {

	public RoleButtonServiceImpl(RoleButtonMapper mapper) {
		super(mapper, RoleButton.class, RoleButtonVO.class);
	}
}

