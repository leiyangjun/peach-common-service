package org.peach.common.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.peach.common.dto.MenuButtonApiReplaceDTO;
import org.peach.common.dto.MenuButtonBindingItemDTO;
import org.peach.common.dto.MenuButtonReplaceDTO;
import org.peach.common.dto.RoleMenuButtonReplaceDTO;
import org.peach.common.entity.ButtonApi;
import org.peach.common.entity.ButtonDict;
import org.peach.common.entity.Menu;
import org.peach.common.entity.MenuButton;
import org.peach.common.entity.Role;
import org.peach.common.entity.RoleButton;
import org.peach.common.entity.RoleUser;
import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.mapper.ButtonDictMapper;
import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mapper.RoleButtonMapper;
import org.peach.common.mapper.RoleMapper;
import org.peach.common.mapper.RoleUserMapper;
import org.peach.common.mybatis.code.CrudBizCode;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mvc.util.ApiMeta;
import org.peach.common.utils.BeanUtil;
import org.peach.common.utils.IdUtil;
import org.peach.common.utils.LoginUserUtil;
import org.peach.common.utils.TreeUtil;
import org.peach.common.vo.CurrentUserMenuButtonItemVO;
import org.peach.common.vo.CurrentUserPermissionVO;
import org.peach.common.vo.MenuButtonPickerRowVO;
import org.peach.common.vo.MenuVO;
import org.peach.common.vo.RegistryServiceItemVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * 菜单按钮字典绑定、按钮 API 绑定、角色菜单按钮绑定、注册服务列表。
 */
@Service
@RequiredArgsConstructor
public class PermissionFacadeService {

	private static final String MENU_TYPE_MENU = "MENU";
	private static final String MENU_TYPE_CATALOG = "CATALOG";
	private static final String DICT_CODE_VIEW = "BTN_DEFAULT";
	private static final String GATEWAY_SERVICE_ID = "peach-gateway";

	private final MenuMapper menuMapper;
	private final MenuButtonMapper menuButtonMapper;
	private final ButtonDictMapper buttonDictMapper;
	private final ButtonApiMapper buttonApiMapper;
	private final RoleButtonMapper roleButtonMapper;
	private final RoleMapper roleMapper;
	private final RoleUserMapper roleUserMapper;

	private final org.springframework.cloud.client.discovery.DiscoveryClient discoveryClient;

	/**
	 * 全局按钮字典列表（仅种子维护，无单独字典 CRUD 页）。
 *
 * @author leiyangjun
 */
	public List<ButtonDict> listButtonDict() {
		SortVO sort = new SortVO();
		sort.setSortName("sortNo");
		sort.setSortType("asc");
		return buttonDictMapper.selectBase(new ButtonDict(), sort);
	}

	/**
	 * 某菜单当前绑定的按钮行（含字典主键，便于前端回显多选）。
 *
 * @author leiyangjun
 */
	public List<MenuButtonPickerRowVO> listMenuButtonBindRows(Long menuId) {
		return menuButtonMapper.listBindRowsByMenuId(menuId);
	}

	/**
	 * 全量覆盖菜单下按钮实例：先清理角色/API 再删按钮行，再按字典插入。
	 * <p>
	 * 遗留接口：管理端推荐随 {@code POST /menu} 的 {@code buttonBindings} 一次提交；本接口仍供其他客户端或脚本使用。
	 * </p>
 *
 * @author leiyangjun
 */
	@Transactional(rollbackFor = Exception.class)
	public void replaceMenuButtons(Long menuId, MenuButtonReplaceDTO dto) {
		Menu menu = menuMapper.selectBaseByKey(menuId, Menu.class);
		if (menu == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单不存在");
		}
		if (!MENU_TYPE_MENU.equals(menu.getMenuType())) {
			throw BizException.validWarn(CrudBizCode.AFFECTED_ZERO, "仅类型为 MENU 的菜单可绑定按钮");
		}
		LinkedHashSet<Long> idSet = new LinkedHashSet<>();
		if (dto.getDictButtonIds() != null) {
			idSet.addAll(dto.getDictButtonIds());
		}
		Long viewDictId = requireDictIdByCode(DICT_CODE_VIEW);
		idSet.add(viewDictId);

		List<ButtonDict> dictRows = loadDictsByIds(idSet);
		dictRows.sort(Comparator.comparing(ButtonDict::getSortNo, Comparator.nullsLast(Integer::compareTo))
				.thenComparing(ButtonDict::getId, Comparator.nullsLast(Long::compareTo)));
		physicalResetMenuButtonsByDictOrder(menuId, dictRows);
	}

	/**
	 * 与菜单保存同事务：按类型覆盖按钮及 API；{@code items} 在 MENU 下为全量（服务端并入 BTN_DEFAULT），CATALOG 下忽略内容仅保留
	 * BTN_DEFAULT。
 *
 * @author leiyangjun
 */
	@Transactional(rollbackFor = Exception.class)
	public void saveMenuWithButtonBindings(Long menuId, String menuType, List<MenuButtonBindingItemDTO> items) {
		Menu menu = menuMapper.selectBaseByKey(menuId, Menu.class);
		if (menu == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单不存在");
		}
		if (!Objects.equals(menu.getMenuType(), menuType)) {
			throw BizException.validWarn(CrudBizCode.TABLE_KEY_INVALID, "菜单类型与请求体不一致");
		}
		if (MENU_TYPE_CATALOG.equals(menuType)) {
			Long viewDictId = requireDictIdByCode(DICT_CODE_VIEW);
			LinkedHashSet<Long> idSet = new LinkedHashSet<>();
			idSet.add(viewDictId);
			List<ButtonDict> dictRows = loadDictsByIds(idSet);
			physicalResetMenuButtonsByDictOrder(menuId, dictRows);
			return;
		}
		if (MENU_TYPE_MENU.equals(menuType)) {
			List<MenuButtonBindingItemDTO> safe = items == null ? List.of() : items;
			replaceMenuButtonsWithApisFromItems(menuId, safe);
			return;
		}
		throw BizException.validWarn(CrudBizCode.AFFECTED_ZERO, "当前菜单类型不支持按钮绑定");
	}

	private void replaceMenuButtonsWithApisFromItems(long menuId, List<MenuButtonBindingItemDTO> items) {
		Menu menu = menuMapper.selectBaseByKey(menuId, Menu.class);
		if (menu == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单不存在");
		}
		if (!MENU_TYPE_MENU.equals(menu.getMenuType())) {
			throw BizException.validWarn(CrudBizCode.AFFECTED_ZERO, "仅类型为 MENU 的菜单可绑定按钮");
		}
		LinkedHashSet<Long> idSet = new LinkedHashSet<>();
		Map<Long, MenuButtonBindingItemDTO> byDict = new LinkedHashMap<>();
		for (MenuButtonBindingItemDTO it : items) {
			if (it == null || it.getDictButtonId() == null) {
				continue;
			}
			if (byDict.containsKey(it.getDictButtonId())) {
				throw BizException.validWarn(CrudBizCode.TABLE_KEY_INVALID, "buttonBindings 中存在重复的 dictButtonId");
			}
			byDict.put(it.getDictButtonId(), it);
			idSet.add(it.getDictButtonId());
		}
		Long viewDictId = requireDictIdByCode(DICT_CODE_VIEW);
		idSet.add(viewDictId);

		List<ButtonDict> dictRows = loadDictsByIds(idSet);
		dictRows.sort(Comparator.comparing(ButtonDict::getSortNo, Comparator.nullsLast(Integer::compareTo))
				.thenComparing(ButtonDict::getId, Comparator.nullsLast(Long::compareTo)));
		Map<Long, Long> dictIdToMb = physicalResetMenuButtonsByDictOrder(menuId, dictRows);
		for (ButtonDict d : dictRows) {
			Long mbId = dictIdToMb.get(d.getId());
			if (mbId == null) {
				continue;
			}
			MenuButtonBindingItemDTO hit = byDict.get(d.getId());
			List<ApiMeta> apis = hit != null && hit.getApis() != null ? hit.getApis() : List.of();
			writeApisForMenuButton(mbId, apis);
		}
	}

	/**
	 * 物理重写菜单下按钮行（不校验菜单类型）：删除旧按钮的角色/API/行，按字典顺序插入新行。
	 *
	 * @return 字典 id → 新 menu_button.id
 *
 * @author leiyangjun
 */
	private Map<Long, Long> physicalResetMenuButtonsByDictOrder(long menuId, List<ButtonDict> dictRowsOrdered) {
		List<Long> oldIds = menuButtonMapper.listIdsByMenuId(menuId);
		if (oldIds != null && !oldIds.isEmpty()) {
			roleButtonMapper.physicalDeleteByButtonIds(oldIds);
			buttonApiMapper.physicalDeleteByButtonIds(oldIds);
		}
		menuButtonMapper.physicalDeleteByMenuId(menuId);

		Map<Long, Long> dictIdToButtonId = new LinkedHashMap<>();
		int order = 0;
		for (ButtonDict d : dictRowsOrdered) {
			long newMbId = IdUtil.shortSnowId();
			MenuButton mb = new MenuButton();
			mb.setId(newMbId);
			mb.setMenuId(menuId);
			mb.setButtonCode(d.getButtonCode());
			mb.setButtonName(d.getButtonName());
			mb.setOrderNo(order++);
			mb.setRemark(null);
			menuButtonMapper.insertBase(mb);
			dictIdToButtonId.put(d.getId(), newMbId);
		}
		return dictIdToButtonId;
	}

	/**
	 * 全量覆盖某菜单按钮实例上的 API 绑定；{@code api_code} 由 method+urlPath 稳定派生。
	 * <p>
	 * 遗留接口：推荐随菜单 {@code POST /menu} 一次提交；本接口仍供其他客户端使用。
	 * </p>
 *
 * @author leiyangjun
 */
	@Transactional(rollbackFor = Exception.class)
	public void replaceMenuButtonApis(Long menuButtonId, MenuButtonApiReplaceDTO dto) {
		MenuButton mb = menuButtonMapper.selectBaseByKey(menuButtonId, MenuButton.class);
		if (mb == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单按钮不存在");
		}
		writeApisForMenuButton(menuButtonId, dto == null ? null : dto.getApis());
	}

	private void writeApisForMenuButton(long menuButtonId, List<ApiMeta> apis) {
		buttonApiMapper.physicalDeleteByButtonId(menuButtonId);
		if (apis == null || apis.isEmpty()) {
			return;
		}
		for (ApiMeta meta : apis) {
			if (meta == null) {
				continue;
			}
			if (StringUtils.isBlank(meta.getMethod()) || StringUtils.isBlank(meta.getUrlPath())) {
				throw BizException.validWarn(CrudBizCode.TABLE_KEY_INVALID, "API 元数据中 method、urlPath 不能为空");
			}
			ButtonApi row = new ButtonApi();
			row.setId(IdUtil.shortSnowId());
			row.setButtonId(menuButtonId);
			row.setApiCode(stableApiCode(meta.getMethod(), meta.getUrlPath()));
			row.setMethod(meta.getMethod().trim().toUpperCase(Locale.ROOT));
			row.setSummary(meta.getSummary());
			row.setDescription(meta.getDescription());
			row.setApiDesc(meta.getApiDesc());
			row.setUrlPath(meta.getUrlPath().trim());
			row.setPathPattern(meta.getPathPattern());
			row.setServiceName(meta.getServiceName());
			row.setApiType(meta.getApiType());
			buttonApiMapper.insertBase(row);
		}
	}

	/**
	 * Nacos 发现的服务 id 列表（排除网关），供前端下拉。
 *
 * @author leiyangjun
 */
	public List<RegistryServiceItemVO> listRegistryServices() {
		List<String> names = discoveryClient.getServices();
		if (names == null) {
			return List.of();
		}
		return names.stream().filter(StringUtils::isNotBlank).filter(s -> !GATEWAY_SERVICE_ID.equalsIgnoreCase(s))
				.sorted(String.CASE_INSENSITIVE_ORDER).map(s -> {
					RegistryServiceItemVO vo = new RegistryServiceItemVO();
					vo.setServiceId(s);
					vo.setDisplayName(s);
					return vo;
				}).collect(Collectors.toList());
	}

	/**
	 * 某菜单按钮实例当前已绑定的 API（有效行），字段与 {@link ApiMeta} 对齐。
 *
 * @author leiyangjun
 */
	public List<ApiMeta> listMenuButtonApis(Long menuButtonId) {
		MenuButton mb = menuButtonMapper.selectBaseByKey(menuButtonId, MenuButton.class);
		if (mb == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单按钮不存在");
		}
		ButtonApi probe = new ButtonApi();
		probe.setButtonId(menuButtonId);
		probe.setValid((short) 1);
		List<ButtonApi> rows = buttonApiMapper.selectBase(probe, null);
		if (rows == null || rows.isEmpty()) {
			return List.of();
		}
		return rows.stream().map(PermissionFacadeService::buttonApiToApiMeta).collect(Collectors.toList());
	}

	/**
	 * 角色已绑定的菜单按钮实例 id（仅有效行）。
 *
 * @author leiyangjun
 */
	public List<Long> listRoleMenuButtonIds(Long roleId) {
		RoleButton probe = new RoleButton();
		probe.setRoleId(roleId);
		probe.setValid((short) 1);
		List<RoleButton> rows = roleButtonMapper.selectBase(probe, null);
		return rows.stream().map(RoleButton::getButtonId).filter(id -> id != null).distinct().collect(Collectors.toList());
	}

	/**
	 * 角色绑定菜单按钮：全量覆盖 {@code cmn_role_button}。
 *
 * @author leiyangjun
 */
	@Transactional(rollbackFor = Exception.class)
	public void replaceRoleMenuButtons(Long roleId, RoleMenuButtonReplaceDTO dto) {
		Role role = roleMapper.selectBaseByKey(roleId, Role.class);
		if (role == null) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "角色不存在");
		}
		roleButtonMapper.physicalDeleteByRoleId(roleId);
		List<Long> ids = dto.getMenuButtonIds() == null ? List.of() : dto.getMenuButtonIds();
		LinkedHashSet<Long> idSet = new LinkedHashSet<>(ids);
		for (Long bid : idSet) {
			MenuButton mb = menuButtonMapper.selectBaseByKey(bid, MenuButton.class);
			if (mb == null) {
				throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "菜单按钮不存在: " + bid);
			}
			RoleButton rb = new RoleButton();
			rb.setId(IdUtil.shortSnowId());
			rb.setRoleId(roleId);
			rb.setButtonId(bid);
			roleButtonMapper.insertBase(rb);
		}
	}

	/**
	 * 全部「菜单类型」下的菜单按钮实例，供角色多选绑定。
 *
 * @author leiyangjun
 */
	public List<MenuButtonPickerRowVO> listAllMenuButtonsForRolePicker() {
		return menuButtonMapper.listAllMenuButtonsForRolePicker();
	}

	/**
	 * 当前登录用户菜单与按钮权限：用户→角色→角色按钮→菜单按钮→菜单（分步 BaseMapper，无关联查询）。
	 */
	@Transactional(readOnly = true)
	public CurrentUserPermissionVO listCurrentUserPermission() {
		Long userId = LoginUserUtil.getLoginUserId();
		if (userId == null) {
			throw BizException.validWarn(CrudBizCode.TABLE_KEY_INVALID, "未登录或无法识别当前用户");
		}

		RoleUser roleUserProbe = new RoleUser();
		roleUserProbe.setUserId(userId);
		List<RoleUser> roleUsers = roleUserMapper.selectBase(roleUserProbe, null);
		if (roleUsers == null || roleUsers.isEmpty()) {
			return emptyCurrentUserPermission();
		}

		LinkedHashSet<Long> roleIds = roleUsers.stream().map(RoleUser::getRoleId).filter(Objects::nonNull)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		if (roleIds.isEmpty()) {
			return emptyCurrentUserPermission();
		}

		LinkedHashSet<Long> menuButtonIds = new LinkedHashSet<>();
		for (Long roleId : roleIds) {
			RoleButton roleButtonProbe = new RoleButton();
			roleButtonProbe.setRoleId(roleId);
			roleButtonProbe.setValid((short) 1);
			List<RoleButton> roleButtons = roleButtonMapper.selectBase(roleButtonProbe, null);
			if (roleButtons == null) {
				continue;
			}
			for (RoleButton rb : roleButtons) {
				if (rb.getButtonId() != null) {
					menuButtonIds.add(rb.getButtonId());
				}
			}
		}
		if (menuButtonIds.isEmpty()) {
			return emptyCurrentUserPermission();
		}

		List<MenuButton> menuButtons = menuButtonMapper.selectBaseByKeys(new ArrayList<>(menuButtonIds),
				MenuButton.class, null);
		if (menuButtons == null) {
			menuButtons = List.of();
		}
		List<MenuButton> activeButtons = menuButtons.stream()
				.filter(mb -> mb.getValid() != null && mb.getValid() == 1 && mb.getMenuId() != null)
				.collect(Collectors.toList());
		if (activeButtons.isEmpty()) {
			return emptyCurrentUserPermission();
		}

		Menu menuProbe = new Menu();
		menuProbe.setValid((short) 1);
		SortVO menuSort = new SortVO();
		menuSort.setSortName("orderNo");
		menuSort.setSortType("asc");
		List<Menu> allValidMenus = menuMapper.selectBase(menuProbe, menuSort);
		if (allValidMenus == null) {
			allValidMenus = List.of();
		}
		Map<Long, Menu> menuById = allValidMenus.stream()
				.collect(Collectors.toMap(Menu::getId, m -> m, (a, b) -> a, LinkedHashMap::new));

		LinkedHashSet<Long> visibleMenuIds = new LinkedHashSet<>();
		for (MenuButton mb : activeButtons) {
			collectMenuAncestors(mb.getMenuId(), menuById, visibleMenuIds);
		}

		List<MenuVO> flatMenus = allValidMenus.stream().filter(m -> visibleMenuIds.contains(m.getId()))
				.map(m -> BeanUtil.copy(m, MenuVO.class)).collect(Collectors.toList());
		List<MenuVO> menuTree = TreeUtil.tree(flatMenus, MenuVO.class);

		List<CurrentUserMenuButtonItemVO> buttonItems = new ArrayList<>();
		for (MenuButton mb : activeButtons) {
			Menu menu = menuById.get(mb.getMenuId());
			CurrentUserMenuButtonItemVO item = new CurrentUserMenuButtonItemVO();
			item.setMenuButtonId(mb.getId());
			item.setMenuId(mb.getMenuId());
			item.setButtonCode(mb.getButtonCode());
			item.setButtonName(mb.getButtonName());
			if (menu != null) {
				item.setMenuCode(menu.getMenuCode());
				item.setRoutePath(menu.getRoutePath());
			}
			buttonItems.add(item);
		}

		CurrentUserPermissionVO vo = new CurrentUserPermissionVO();
		vo.setMenuTree(menuTree);
		vo.setMenuButtons(buttonItems);
		return vo;
	}

	private CurrentUserPermissionVO emptyCurrentUserPermission() {
		CurrentUserPermissionVO vo = new CurrentUserPermissionVO();
		vo.setMenuTree(List.of());
		vo.setMenuButtons(List.of());
		return vo;
	}

	/** 将菜单及其祖先目录 id 纳入可见集合（内存回溯，无 JOIN）。 */
	private void collectMenuAncestors(Long menuId, Map<Long, Menu> menuById, LinkedHashSet<Long> out) {
		Long cur = menuId;
		while (cur != null && cur > 0L) {
			if (!out.add(cur)) {
				break;
			}
			Menu menu = menuById.get(cur);
			if (menu == null) {
				break;
			}
			Long parentId = menu.getParentId();
			if (parentId == null || parentId <= 0L) {
				break;
			}
			cur = parentId;
		}
	}

	private Long requireDictIdByCode(String code) {
		ButtonDict probe = new ButtonDict();
		probe.setButtonCode(code);
		List<ButtonDict> list = buttonDictMapper.selectBase(probe, null);
		if (list == null || list.isEmpty()) {
			throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "字典缺少按钮编码「" + code + "」，请执行 init_table 种子");
		}
		return list.get(0).getId();
	}

	private List<ButtonDict> loadDictsByIds(LinkedHashSet<Long> idSet) {
		List<ButtonDict> out = new ArrayList<>();
		for (Long id : idSet) {
			ButtonDict row = buttonDictMapper.selectBaseByKey(id, ButtonDict.class);
			if (row == null) {
				throw BizException.validWarn(CrudBizCode.RECORD_NOT_FOUND, "无效的按钮字典 id: " + id);
			}
			out.add(row);
		}
		return out;
	}

	private static ApiMeta buttonApiToApiMeta(ButtonApi ba) {
		ApiMeta m = new ApiMeta();
		m.setMethod(ba.getMethod());
		m.setSummary(ba.getSummary());
		m.setDescription(ba.getDescription());
		m.setApiDesc(ba.getApiDesc());
		m.setUrlPath(ba.getUrlPath());
		m.setPathPattern(ba.getPathPattern());
		m.setServiceName(ba.getServiceName());
		m.setApiType(ba.getApiType());
		return m;
	}

	private static String stableApiCode(String method, String urlPath) {
		String raw = (method == null ? "" : method.trim().toUpperCase(Locale.ROOT)) + ":"
				+ (urlPath == null ? "" : urlPath.trim());
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5", e);
		}
	}
}
