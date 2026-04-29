package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.UserMapper;
import org.peach.common.entity.User;
import org.peach.common.vo.UserVO;
import org.peach.common.service.UserService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class UserServiceImpl extends BaseAbstractService<UserMapper, User, UserVO> implements UserService {

	public UserServiceImpl(UserMapper mapper) {
		super(mapper, User.class, UserVO.class);
	}
}

