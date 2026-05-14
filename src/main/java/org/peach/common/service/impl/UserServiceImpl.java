package org.peach.common.service.impl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.peach.common.code.BizMessageCode;
import org.peach.common.dto.ResetPwdDTO;
import org.peach.common.entity.User;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.UserService;
import org.peach.common.util.BCryptUtil;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理：新增/修改返回主键；有效切换仅用 logicDeleteByKey / logicRecoveryByKey。
 */
@Service
public class UserServiceImpl extends BaseAbstractService<UserMapper, User, UserVO> implements UserService {

	private static final String TYPE_SYSTEM = "system";

	public UserServiceImpl(UserMapper mapper) {
		super(mapper, User.class, UserVO.class);
	}

	@Override
	@Transactional
	public Serializable save(UserVO vo) {
		return persist(vo);
	}

	@Override
	@Transactional
	public Long persist(UserVO vo) {
		boolean isNew = vo.getId() == null || vo.getId() <= 0L;
		if (isNew) {
			return createSystemUser(vo);
		}
		return updateUser(vo);
	}

	@Override
	@Transactional
	public Short toggleValid(Long id) {
		User u = mapper.selectBaseByKey(id, User.class);
		if (u == null) {
			throw BizException.validWarn(BizMessageCode.User.USER_NOT_FOUND);
		}
		short cur = u.getValid() == null ? 0 : u.getValid().shortValue();
		if (cur == 1) {
			mapper.logicDeleteByKey(id, User.class);
			return (short) 0;
		}
		mapper.logicRecoveryByKey(id, User.class);
		return (short) 1;
	}

	@Override
	@Transactional
	public void resetPwd(ResetPwdDTO dto) {
		User u = mapper.selectBaseByKey(dto.getId(), User.class);
		if (u == null) {
			throw BizException.validWarn(BizMessageCode.User.USER_NOT_FOUND);
		}
		if (!TYPE_SYSTEM.equals(u.getUserType())) {
			throw BizException.validWarn(BizMessageCode.User.ONLY_SYSTEM_USER_RESET_PWD);
		}
		User patch = new User();
		patch.setId(dto.getId());
		patch.setPassword(BCryptUtil.encode(dto.getNewPassword()));
		mapper.updateBase(patch);
	}

	@Override
	@Transactional
	public void hardDelete(Long id) {
		User u = mapper.selectBaseByKey(id, User.class);
		if (u == null) {
			throw BizException.validWarn(BizMessageCode.User.USER_NOT_FOUND);
		}
		if (!TYPE_SYSTEM.equals(u.getUserType())) {
			throw BizException.validWarn(BizMessageCode.User.ONLY_SYSTEM_USER_PHYSICAL_DELETE);
		}
		Integer n = mapper.deleteBaseByKey(id, User.class);
		if (n == null || n <= 0) {
			throw BizException.validWarn(BizMessageCode.User.USER_HARD_DELETE_FAILED);
		}
	}

	private Long createSystemUser(UserVO vo) {
		String login = vo.getUsername() == null ? "" : vo.getUsername().trim();
		if (Boolean.TRUE.equals(mapper.checkExist(login, User.class, null))) {
			throw BizException.validWarn(BizMessageCode.User.LOGIN_NAME_EXISTS);
		}
		User entity = BeanUtil.copy(vo, User.class);
		entity.setId(null);
		entity.setUserType(TYPE_SYSTEM);
		entity.setUsername(login);
		entity.setPassword(BCryptUtil.encode(vo.getPlainPassword()));
		if (entity.getValid() == null) {
			entity.setValid((short) 1);
		}
		mapper.insertBase(entity);
		return entity.getId();
	}

	private Long updateUser(UserVO vo) {
		User existing = mapper.selectBaseByKey(vo.getId(), User.class);
		if (existing == null) {
			throw BizException.validWarn(BizMessageCode.User.USER_NOT_FOUND);
		}
		if (!TYPE_SYSTEM.equals(existing.getUserType())) {
			return existing.getId();
		}
		String login = vo.getUsername() == null ? null : vo.getUsername().trim();
		if (login != null && !login.equals(StringUtils.trimToEmpty(existing.getUsername()))) {
			if (Boolean.TRUE.equals(mapper.checkExist(login, User.class, existing.getId()))) {
				throw BizException.validWarn(BizMessageCode.User.LOGIN_NAME_CONFLICT);
			}
		}
		User patch = BeanUtil.copy(vo, User.class);
		patch.setId(vo.getId());
		patch.setValid(null);
		patch.setUserType(TYPE_SYSTEM);
		patch.setPassword(null);
		patch.setCreator(null);
		patch.setCreateTime(null);
		patch.setEditor(null);
		patch.setEditTime(null);
		patch.setLastLoginTime(null);
		patch.setLastLoginClient(null);
		if (StringUtils.isNotBlank(vo.getPlainPassword())) {
			patch.setPassword(BCryptUtil.encode(vo.getPlainPassword()));
		}
		if (hasUpdatablePatch(patch)) {
			mapper.updateBase(patch);
		}
		return vo.getId();
	}

	private boolean hasUpdatablePatch(User p) {
		return p.getUsername() != null || p.getNickname() != null || p.getMobile() != null || p.getEmail() != null
				|| p.getRealName() != null || p.getRemark() != null || p.getGender() != null || p.getAvatar() != null
				|| p.getCertType() != null || p.getCertNo() != null || p.getPassword() != null;
	}
}
