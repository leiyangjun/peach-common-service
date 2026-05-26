package org.peach.common.service.impl;

import org.peach.common.entity.Application;
import org.peach.common.mapper.ApplicationMapper;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.ApplicationService;
import org.peach.common.vo.ApplicationVO;
import org.springframework.stereotype.Service;

/**
 * 继承 {@link BaseAbstractService}。
 *
 * @author leiyangjun
 */
@Service
public class ApplicationServiceImpl extends BaseAbstractService<ApplicationMapper, Application, ApplicationVO> implements ApplicationService {

	public ApplicationServiceImpl(ApplicationMapper mapper) {
		super(mapper, Application.class, ApplicationVO.class);
	}
	
	@Override
	public void deleteAppById(Long id) {
		this.mapper.deleteBaseByKey(id, Application.class);
	}
}
