package org.peach.common.service;

import java.io.Serializable;

import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.UnauthApiVO;

/**
 * 网关免鉴权 API 管理。
 */
public interface UnauthApiService extends BaseInterfaceService<UnauthApiVO> {

	@Override
	Serializable save(UnauthApiVO vo);

	void deleteById(Long id);

	Short toggleValid(Long id);
}
