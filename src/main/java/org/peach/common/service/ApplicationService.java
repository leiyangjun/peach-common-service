package org.peach.common.service;

import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.ApplicationVO;

/**
 * 业务服务接口：通用 CRUD 见 {@link BaseInterfaceService}，此处仅可追加扩展方法。
 *
 * @author leiyangjun
 */
public interface ApplicationService extends BaseInterfaceService<ApplicationVO> {

	void deleteAppById(Long id);
}
