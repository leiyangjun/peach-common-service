package org.peach.common.service;

import java.util.List;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.ButtonVO;

/**
 * 业务服务接口：通用 CRUD 见 {@link BaseInterfaceService}，此处仅可追加扩展方法。
 *
 * @author leiyangjun
 */
public interface ButtonService extends BaseInterfaceService<ButtonVO> {
	
	List<ButtonVO> getButtonList();
}
