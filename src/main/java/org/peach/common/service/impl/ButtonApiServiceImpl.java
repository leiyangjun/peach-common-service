package org.peach.common.service.impl;

import org.peach.common.mybatis.service.BaseAbstractService;
import org.springframework.stereotype.Service;

import org.peach.common.mapper.ButtonApiMapper;
import org.peach.common.entity.ButtonApi;
import org.peach.common.vo.ButtonApiVO;
import org.peach.common.service.ButtonApiService;

/**
 * 继承 {@link BaseAbstractService}。
 */
@Service
public class ButtonApiServiceImpl extends BaseAbstractService<ButtonApiMapper, ButtonApi, ButtonApiVO> implements ButtonApiService {

	public ButtonApiServiceImpl(ButtonApiMapper mapper) {
		super(mapper, ButtonApi.class, ButtonApiVO.class);
	}
}

