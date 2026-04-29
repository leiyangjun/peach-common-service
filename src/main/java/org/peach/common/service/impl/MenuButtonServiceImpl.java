package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.MenuButtonMapper;
import org.peach.common.entity.MenuButton;
import org.peach.common.vo.MenuButtonVO;
import org.peach.common.service.MenuButtonService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class MenuButtonServiceImpl extends BaseAbstractService<MenuButtonMapper, MenuButton, MenuButtonVO> implements MenuButtonService {

	public MenuButtonServiceImpl(MenuButtonMapper mapper) {
		super(mapper, MenuButton.class, MenuButtonVO.class);
	}
}

