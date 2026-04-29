package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.MenuMapper;
import org.peach.common.entity.Menu;
import org.peach.common.vo.MenuVO;
import org.peach.common.service.MenuService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class MenuServiceImpl extends BaseAbstractService<MenuMapper, Menu, MenuVO> implements MenuService {

	public MenuServiceImpl(MenuMapper mapper) {
		super(mapper, Menu.class, MenuVO.class);
	}
}

