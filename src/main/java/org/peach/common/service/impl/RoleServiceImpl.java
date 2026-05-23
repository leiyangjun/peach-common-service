package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.entity.User;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mapper.UserMapper;
import org.peach.common.mybatis.lambda.LambdaQuery;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.RoleService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.RoleUserVO;
import org.peach.common.vo.RoleVO;
import org.peach.common.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

	@Override
	public Serializable save(RoleVO vo) {
		return this.mapper.saveOrUpdate(BeanUtil.copy(vo, Role.class));
	}

	public RoleServiceImpl(RoleMapper mapper, RoleUserMapper roleUserMapper, RoleButtonMapper roleButtonMapper,
		UserMapper userMapper) {
		super(mapper, Role.class, RoleVO.class);
		this.roleUserMapper = roleUserMapper;
		this.roleButtonMapper = roleButtonMapper;
		this.userMapper = userMapper;
	}

	@Override
	@Transactional
	public void deleteRoleById(Long id) {
		this.mapper.deleteBaseByKey(id, Role.class);
		this.roleUserMapper.deleteByLambda(LambdaQuery.of(RoleUser.class).eq(RoleUser::getRoleId, id));
		this.roleButtonMapper.deleteByLambda(LambdaQuery.of(RoleButton.class).eq(RoleButton::getRoleId, id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserVO> getUserByRoleId(Long roleId) {
		List<RoleUser> roleUsers =
			this.roleUserMapper.selectByLambda(LambdaQuery.of(RoleUser.class).eq(RoleUser::getRoleId, roleId));
		List<Long> userIds = roleUsers.stream().map(RoleUser::getUserId).collect(Collectors.toList());
		List<User> result = null;
		if (!CollectionUtils.isEmpty(userIds)) {
			result = userMapper.selectBaseByKeys(userIds, User.class, null);
		}
		return BeanUtil.copyList(result, UserVO.class);
	}

	@Override
	@Transactional
	public void bindUser(Long roleId, List<RoleUserVO> users) {
		this.roleUserMapper.deleteByLambda(LambdaQuery.of(RoleUser.class).eq(RoleUser::getRoleId, roleId));
		if (!CollectionUtils.isEmpty(users)) {
			this.roleUserMapper.batchInsertBase(BeanUtil.copyList(users, RoleUser.class));
		}
	}
}
