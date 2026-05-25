package org.peach.common.service.impl;

import java.util.List;
import org.peach.common.code.BizMessageCode;
import org.peach.common.entity.Dict;
import org.peach.common.mapper.DictMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.DictService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.DictVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 码表管理：关键字分页与状态筛选、（类型+存储值）唯一校验、状态切换与物理删库。
 */
@Service
public class DictServiceImpl extends BaseAbstractService<DictMapper, Dict, DictVO> implements DictService {

	public DictServiceImpl(DictMapper mapper) {
		super(mapper, Dict.class, DictVO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getDictTypes() {
		return mapper.selectDictTypes();
	}

	@Override
	@Transactional
	public void switchStatus(Long id) {
		Dict dict = mapper.selectBaseByKey(id, Dict.class);
		if (dict == null) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_NOT_FOUND);
		}
		if (dict.getStatus().shortValue() == 1) {
			this.mapper.logicDeleteByKey(id, Dict.class);
		} else {
			this.mapper.logicRecoveryByKey(id, Dict.class);
		}
	}

	@Override
	@Transactional
	public void deleteDictByKey(Long id) {
		mapper.deleteBaseByKey(id, Dict.class);
	}

	@Override
	public List<DictVO> getByDictType(String dictType) {
		Dict dict = new Dict();
		dict.setDictType(dictType);
		dict.setStatus(Short.valueOf("1"));
		SortVO sortVO = new SortVO();
		sortVO.setSortName("sortNo");
		sortVO.setSortName("ASC");
		return BeanUtil.copyList(this.mapper.selectBase(dict, sortVO), DictVO.class);
	}
}
