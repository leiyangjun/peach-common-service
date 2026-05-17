package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.peach.common.code.BizMessageCode;
import org.peach.common.dto.BindRoleUsersDTO;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleUser;
import org.peach.common.entity.User;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.RoleService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理：关键字分页、编码唯一校验、物理删级联清理、用户绑定全量替换。
 *
 * @author leiyangjun
 */
@Service
public class RoleServiceImpl extends BaseAbstractService<RoleMapper, Role, RoleVO> implements RoleService {

	private final RoleUserMapper roleUserMapper;
	private final RoleButtonMapper roleButtonMapper;
	private final UserMapper userMapper;

	public RoleServiceImpl(RoleMapper mapper, RoleUserMapper roleUserMapper, RoleButtonMapper roleButtonMapper,
			UserMapper userMapper) {
		super(mapper, Role.class, RoleVO.class);
		this.roleUserMapper = roleUserMapper;
		this.roleButtonMapper = roleButtonMapper;
		this.userMapper = userMapper;
	}

	@Override
	@Transactional
	public Serializable save(RoleVO vo) {
		return persist(vo);
	}

	@Override
	@Transactional
	public Long persist(RoleVO vo) {
		Objects.requireNonNull(vo, "vo");
		boolean isNew = vo.getId() == null || vo.getId() <= 0L;
		if (isNew) {
			return createRole(vo);
		}
		return updateRole(vo);
	}

	private Long createRole(RoleVO vo) {
		String code = StringUtils.trimToEmpty(vo.getRoleCode());
		String name = StringUtils.trimToEmpty(vo.getRoleName());
		if (StringUtils.isBlank(code)) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_CODE_REQUIRED);
		}
		if (StringUtils.isBlank(name)) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NAME_REQUIRED);
		}
		if (Boolean.TRUE.equals(mapper.checkExist(code, Role.class, null))) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_CODE_EXISTS);
		}
		Role entity = BeanUtil.copy(vo, Role.class);
		entity.setId(null);
		entity.setRoleCode(code);
		entity.setRoleName(name.trim());
		if (entity.getValid() == null) {
			entity.setValid((short) 1);
		}
		mapper.insertBase(entity);
		return entity.getId();
	}

	private Long updateRole(RoleVO vo) {
		Role existing = mapper.selectBaseByKey(vo.getId(), Role.class);
		if (existing == null) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NOT_FOUND);
		}
		String code = vo.getRoleCode() == null ? null : vo.getRoleCode().trim();
		if (StringUtils.isNotBlank(code) && !code.equals(StringUtils.trimToEmpty(existing.getRoleCode()))) {
			if (Boolean.TRUE.equals(mapper.checkExist(code, Role.class, existing.getId()))) {
				throw BizException.validWarn(BizMessageCode.Role.ROLE_CODE_CONFLICT);
			}
		}
		if (StringUtils.isBlank(StringUtils.trimToEmpty(vo.getRoleName()))) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NAME_REQUIRED);
		}
		Role patch = BeanUtil.copy(vo, Role.class);
		patch.setId(vo.getId());
		patch.setValid(null);
		patch.setCreator(null);
		patch.setCreateTime(null);
		patch.setEditor(null);
		patch.setEditTime(null);
		if (StringUtils.isNotBlank(code)) {
			patch.setRoleCode(code);
		}
		patch.setRoleName(vo.getRoleName().trim());
		if (hasUpdatablePatch(patch)) {
			mapper.updateBase(patch);
		}
		return vo.getId();
	}

	private boolean hasUpdatablePatch(Role p) {
		return p.getRoleCode() != null || p.getRoleName() != null || p.getRemark() != null;
	}

	@Override
	@Transactional
	public void hardDelete(Long id) {
		Role r = mapper.selectBaseByKey(id, Role.class);
		if (r == null) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NOT_FOUND);
		}
		roleButtonMapper.physicalDeleteByRoleId(id);
		roleUserMapper.deleteByRoleId(id);
		Integer n = mapper.deleteBaseByKey(id, Role.class);
		if (n == null || n <= 0) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_HARD_DELETE_FAILED);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> listUserIds(Long roleId) {
		Role r = mapper.selectBaseByKey(roleId, Role.class);
		if (r == null) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NOT_FOUND);
		}
		List<Long> ids = roleUserMapper.listUserIdsByRoleId(roleId);
		return ids == null ? List.of() : ids;
	}

	@Override
	@Transactional
	public void replaceRoleUsers(Long roleId, BindRoleUsersDTO dto) {
		Role r = mapper.selectBaseByKey(roleId, Role.class);
		if (r == null) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_NOT_FOUND);
		}
		roleUserMapper.deleteByRoleId(roleId);
		if (dto == null || dto.getUserIds() == null || dto.getUserIds().isEmpty()) {
			return;
		}
		Set<Long> ids = dto.getUserIds().stream().filter(Objects::nonNull).filter(x -> x > 0L)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		if (ids.isEmpty()) {
			return;
		}
		List<Long> idList = new ArrayList<>(ids);
		List<User> found = userMapper.selectBaseByKeys(idList, User.class, null);
		if (found == null || found.size() != idList.size()) {
			throw BizException.validWarn(BizMessageCode.Role.ROLE_BIND_USER_NOT_FOUND);
		}
		Set<Long> ok = found.stream().map(User::getId).collect(Collectors.toSet());
		for (Long uid : idList) {
			if (!ok.contains(uid)) {
				throw BizException.validWarn(BizMessageCode.Role.ROLE_BIND_USER_NOT_FOUND);
			}
		}
		for (Long userId : idList) {
			RoleUser ru = new RoleUser();
			ru.setRoleId(roleId);
			ru.setUserId(userId);
			roleUserMapper.insertBase(ru);
		}
	}
}
