package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.peach.common.code.BizMessageCode;
import org.peach.common.entity.Menu;
import org.peach.common.mapper.MenuMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.MenuService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.utils.TreeUtil;
import org.peach.common.vo.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单业务实现：树形列表（不分页）及保存前父子关系校验；树结构由 {@link TreeUtil} 组装。
 */
@Service
public class MenuServiceImpl extends BaseAbstractService<MenuMapper, Menu, MenuVO> implements MenuService {

	public MenuServiceImpl(MenuMapper mapper) {
		super(mapper, Menu.class, MenuVO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVO> listMenuTreeValid() {
		return TreeUtil.tree((queryFlatMenus(true)), MenuVO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVO> listMenuTreeAll() {
		return TreeUtil.tree((queryFlatMenus(false)), MenuVO.class);
	}

	/**
	 * 平铺查询菜单；{@code validOnly=true} 时仅 {@code valid=1}，否则不按有效标记过滤。
	 */
	private List<MenuVO> queryFlatMenus(boolean validOnly) {
		Menu cond = new Menu();
		if (validOnly) {
			cond.setValid((short) 1);
		}
		SortVO sort = new SortVO();
		sort.setSortName("orderNo");
		sort.setSortType("asc");
		List<Menu> rows = mapper.selectBase(cond, sort);
		return rows.stream().map(e -> BeanUtil.copy(e, MenuVO.class)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Serializable save(MenuVO vo) {
		Objects.requireNonNull(vo, "vo");
		validateMenuForSave(vo);
		return super.save(vo);
	}

	private void validateMenuForSave(MenuVO vo) {
		if (StringUtils.isBlank(vo.getMenuCode())) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_CODE_REQUIRED);
		}
		if (StringUtils.isBlank(vo.getMenuName())) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_NAME_REQUIRED);
		}
		if (StringUtils.isBlank(vo.getMenuType())) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_TYPE_REQUIRED);
		}
		Long pid = vo.getParentId();
		if (pid != null && pid != 0L) {
			Long current = pid;
			while (current != null && current != 0L) {
				if (vo.getId() != null && current.equals(vo.getId())) {
					throw BizException.validWarn(BizMessageCode.Menu.MENU_PARENT_CYCLE);
				}
				Menu p = mapper.selectBaseByKey(current, Menu.class);
				if (p == null) {
					throw BizException.validWarn(BizMessageCode.Menu.MENU_PARENT_NOT_FOUND);
				}
				current = p.getParentId();
			}
		}
	}

	@Override
	@Transactional
	public void deletePhysically(Long id) {
		Menu row = mapper.selectBaseByKey(id, Menu.class);
		if (row == null) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_NOT_FOUND_OR_DELETED);
		}
		Menu childCond = new Menu();
		childCond.setParentId(id);
		SortVO sort = new SortVO();
		long childCount = mapper.selectBase(childCond, sort).size();
		if (childCount > 0) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_HAS_CHILDREN);
		}
		int n = mapper.deleteBaseByKey(id, Menu.class);
		if (n <= 0) {
			throw BizException.validWarn(BizMessageCode.Menu.MENU_DELETE_FAILED);
		}
	}
}
