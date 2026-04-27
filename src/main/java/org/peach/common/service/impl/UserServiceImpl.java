package org.peach.common.service.impl;

import org.peach.common.entity.User;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.security.LoginUserVO;
import org.peach.common.service.UserService;
import org.peach.common.utils.CurrentLoginUserUtil;
import org.peach.common.vo.UserVO;
import org.springframework.stereotype.Service;

/** 继承 {@link BaseAbstractService}。 */
@Service
public class UserServiceImpl extends BaseAbstractService<UserMapper, User, UserVO> implements UserService {

	public UserServiceImpl(UserMapper mapper) {
		super(mapper, User.class, UserVO.class);
	}

	@Override
	public LoginUserVO currentProfile() {
		LoginUserVO snap = CurrentLoginUserUtil.currentOrThrow();
		if (snap.getId() == null) {
			throw new IllegalStateException("令牌中缺少用户主键 sub");
		}
		User db = mapper.selectBaseByKey(snap.getId(), User.class);
		if (db == null || !"1".equals(db.getValid())) {
			throw new IllegalStateException("用户不存在或已失效");
		}
		CurrentLoginUserUtil.mergeProfile(snap, db.getNickname(), db.getMobile(), db.getAvatarUrl());
		return snap;
	}
}
