package org.peach.common.service.notify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 从库表物化角色权限 Redis 快照：ROLE_USERS（roleCode→userIds）、ROLE_APIS（roleCode→API 列表）。
 * <p>
 * 快照键统一使用 {@link Role#roleCode}（库表唯一约束，稳定业务编码），不使用 roleId。
 * API 的 {@code finalPath} 与 {@code cmn_unauth_api.final_path} 内部微服务规则一致：
 * {@code /{serviceName}{urlPath}}，供网关 WhitelistFilter 按原始请求路径 Ant 匹配（无 {@code /peach-gateway} 前缀）。
 * </p>
 *
 * @author leiyangjun
 * @date 2026-05-29
 */
@Component
public class RoleSnapshotBuilder {

	private final RoleMapper roleMapper;

	private final RoleUserMapper roleUserMapper;

	private final RoleButtonMapper roleButtonMapper;

	private final ButtonApiMapper buttonApiMapper;

	public RoleSnapshotBuilder(RoleMapper roleMapper, RoleUserMapper roleUserMapper,
			RoleButtonMapper roleButtonMapper, ButtonApiMapper buttonApiMapper) {
		this.roleMapper = roleMapper;
		this.roleUserMapper = roleUserMapper;
		this.roleButtonMapper = roleButtonMapper;
		this.buttonApiMapper = buttonApiMapper;
	}

	/**
	 * 构建 roleCode → 有效用户 ID 列表（升序、去重）。
	 */
	public Map<String, List<Long>> buildRoleUsers() {
		Map<Long, String> roleIdToCode = loadValidRoleIdToCode();
		if (roleIdToCode.isEmpty()) {
			return Map.of();
		}
		List<RoleUser> roleUsers = roleUserMapper.selectByLambda(LambdaSelect.of(RoleUser.class));
		Map<String, Set<Long>> grouped = new HashMap<>();
		for (RoleUser row : roleUsers) {
			String roleCode = roleIdToCode.get(row.getRoleId());
			if (roleCode == null) {
				continue;
			}
			grouped.computeIfAbsent(roleCode, k -> new LinkedHashSet<>()).add(row.getUserId());
		}
		return toSortedListMap(grouped);
	}

	/**
	 * 构建 roleCode → 物化 API 列表（role→button→button_api，按 method+finalPath 去重）。
	 */
	public Map<String, List<RoleApiItem>> buildRoleApis() {
		Map<Long, String> roleIdToCode = loadValidRoleIdToCode();
		if (roleIdToCode.isEmpty()) {
			return Map.of();
		}
		List<RoleButton> roleButtons =
				roleButtonMapper.selectByLambda(LambdaSelect.of(RoleButton.class).valid());
		List<ButtonApi> buttonApis =
				buttonApiMapper.selectByLambda(LambdaSelect.of(ButtonApi.class).valid());
		Map<String, List<ButtonApi>> apisByMenuButton = buttonApis.stream()
				.collect(Collectors.groupingBy(ba -> menuButtonKey(ba.getMenuId(), ba.getButtonId())));

		Map<String, Set<String>> dedupeKeys = new HashMap<>();
		Map<String, List<RoleApiItem>> result = new LinkedHashMap<>();
		for (RoleButton rb : roleButtons) {
			String roleCode = roleIdToCode.get(rb.getRoleId());
			if (roleCode == null) {
				continue;
			}
			List<ButtonApi> apis = apisByMenuButton
					.getOrDefault(menuButtonKey(rb.getMenuId(), rb.getButtonId()), List.of());
			for (ButtonApi api : apis) {
				RoleApiItem item = toApiItem(api);
				if (item == null) {
					continue;
				}
				String dedupe = item.getMethod() + "|" + item.getFinalPath();
				Set<String> seen = dedupeKeys.computeIfAbsent(roleCode, k -> new LinkedHashSet<>());
				if (!seen.add(dedupe)) {
					continue;
				}
				result.computeIfAbsent(roleCode, k -> new ArrayList<>()).add(item);
			}
		}
		return result;
	}

	private Map<Long, String> loadValidRoleIdToCode() {
		List<Role> roles = roleMapper.selectByLambda(LambdaSelect.of(Role.class).valid());
		return roles.stream()
				.filter(r -> StringUtils.hasText(r.getRoleCode()))
				.collect(Collectors.toMap(Role::getId, Role::getRoleCode, (a, b) -> a, LinkedHashMap::new));
	}

	private static RoleApiItem toApiItem(ButtonApi api) {
		String finalPath = toFinalPath(api.getServiceName(), api.getUrlPath());
		if (finalPath == null || !StringUtils.hasText(api.getMethod())) {
			return null;
		}
		return new RoleApiItem(api.getMethod().trim().toUpperCase(), finalPath);
	}

	/**
	 * 将按钮 API 元数据拼成网关 Ant 最终路径，与 {@code cmn_unauth_api.final_path} 内部微服务规则一致。
	 *
	 * @return {@code /{serviceName}{urlPath}}；参数不完整时返回 {@code null}
	 */
	private static String toFinalPath(String serviceName, String urlPath) {
		if (!StringUtils.hasText(serviceName) || !StringUtils.hasText(urlPath)) {
			return null;
		}
		String sid = serviceName.trim().replaceAll("^/+", "");
		String path = urlPath.trim();
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return "/" + sid + path;
	}

	private static String menuButtonKey(Long menuId, Long buttonId) {
		return menuId + ":" + buttonId;
	}

	private static Map<String, List<Long>> toSortedListMap(Map<String, Set<Long>> grouped) {
		return grouped.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> {
					List<Long> ids = new ArrayList<>(e.getValue());
					Collections.sort(ids);
					return List.copyOf(ids);
				}, (a, b) -> a, LinkedHashMap::new));
	}
}
