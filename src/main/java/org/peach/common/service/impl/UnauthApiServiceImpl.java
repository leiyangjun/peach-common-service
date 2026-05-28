package org.peach.common.service.impl;

import java.io.Serializable;
import org.peach.common.code.BizMessageCode;
import org.peach.common.entity.UnauthApi;
import org.peach.common.mapper.UnauthApiMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.UnauthApiService;
import org.peach.common.service.notify.UnauthApiChangeNotifier;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.UnauthApiVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 免鉴权 API：保存前拼接 final_path；唯一性由 {@link UnauthApi#finalPath} {@code @Unique} + {@code saveOrUpdate} 校验。
 */
@Service
public class UnauthApiServiceImpl extends BaseAbstractService<UnauthApiMapper, UnauthApi, UnauthApiVO>
	implements UnauthApiService {

	private final UnauthApiChangeNotifier changeNotifier;

	public UnauthApiServiceImpl(UnauthApiMapper mapper, UnauthApiChangeNotifier changeNotifier) {
		super(mapper, UnauthApi.class, UnauthApiVO.class);
		this.changeNotifier = changeNotifier;
	}

	@Override
	@Transactional
	public Serializable save(UnauthApiVO vo) {
		UnauthApi entity = BeanUtil.copy(vo, UnauthApi.class);
		mapper.saveOrUpdate(entity);
		changeNotifier.afterChange();
		return entity.getId();
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		UnauthApi row = mapper.selectBaseByKey(id, UnauthApi.class);
		if (row == null) {
			throw BizException.validWarn(BizMessageCode.Unauth.NOT_FOUND);
		}
		if (row.getDeletable() != null && row.getDeletable() == 0) {
			throw BizException.validWarn(BizMessageCode.Unauth.NOT_DELETABLE);
		}
		mapper.deleteBaseByKey(id, UnauthApi.class);
		changeNotifier.afterChange();
	}

	@Override
	@Transactional
	public Short toggleValid(Long id) {
		UnauthApi row = mapper.selectBaseByKey(id, UnauthApi.class);
		if (row == null) {
			throw BizException.validWarn(BizMessageCode.Unauth.NOT_FOUND);
		}
		short cur = row.getValid() == null ? 0 : row.getValid();
		short next = cur == 1 ? (short) 0 : (short) 1;
		UnauthApi patch = new UnauthApi();
		patch.setId(id);
		patch.setValid(next);
		mapper.updateBase(patch);
		changeNotifier.afterChange();
		return next;
	}

}
