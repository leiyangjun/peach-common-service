package org.peach.common.service.impl;

import java.util.List;
import org.peach.common.entity.Button;
import org.peach.common.mapper.ButtonMapper;
import org.peach.common.mybatis.lambda.LambdaSelect;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.ButtonService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.ButtonVO;
import org.springframework.stereotype.Service;

/**
 * 继承 {@link BaseAbstractService}。
 *
 * @author leiyangjun
 */
@Service
public class ButtonServiceImpl extends BaseAbstractService<ButtonMapper, Button, ButtonVO> implements ButtonService {

	public ButtonServiceImpl(ButtonMapper mapper) {
		super(mapper, Button.class, ButtonVO.class);
	}
	
	@Override
	public List<ButtonVO> getButtonList() {
		return BeanUtil.copyList(this.mapper.selectByLambda(LambdaSelect.of(Button.class)), ButtonVO.class);
	}
}
