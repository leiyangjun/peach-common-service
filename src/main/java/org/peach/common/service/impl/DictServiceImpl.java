package org.peach.common.service.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.peach.common.code.BizMessageCode;
import org.peach.common.dto.DictPageQuery;
import org.peach.common.entity.Dict;
import org.peach.common.mapper.DictMapper;
import org.peach.common.mvc.exception.BizException;
import org.peach.common.mybatis.model.vo.SearchVO;
import org.peach.common.mybatis.model.vo.PageVO;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseAbstractService;
import org.peach.common.service.DictService;
import org.peach.common.utils.BeanUtil;
import org.peach.common.vo.DictVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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
	public PageInfo<DictVO> listPage(DictPageQuery query, PageVO page, SortVO sort) {
		return queryDictPage(query, page, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> listDistinctTypes() {
		List<String> rows = mapper.selectDistinctDictTypes();
		return rows == null ? Collections.emptyList() : rows;
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional(readOnly = true)
	public PageInfo<DictVO> listPage(DictVO condition, SearchVO searchVO, PageVO page, SortVO sort) {
		DictPageQuery q = new DictPageQuery();
		if (searchVO != null && StringUtils.isNotBlank(searchVO.getSearchValue())) {
			q.setSearchValue(searchVO.getSearchValue().trim());
		}
		return queryDictPage(q, page, sort);
	}

	/**
	 * 码表分页核心：模糊关键字来自 {@link DictPageQuery#getSearchValue()}；{@code listStatusFlag} 见
	 * {@link #buildListCondition(DictPageQuery)}。
	 */
	private PageInfo<DictVO> queryDictPage(DictPageQuery query, PageVO page, SortVO sort) {
		SearchVO search = new SearchVO();
		if (query != null && StringUtils.isNotBlank(query.getSearchValue())) {
			search.setSearchValue(query.getSearchValue().trim());
		}
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		Dict cond = buildListCondition(query);
		List<Dict> rows = mapper.likeSelectBase(cond, search, sort);
		return toVoPageInfo(new PageInfo<>(rows));
	}

	/** 将分页查询参数转为实体等值条件（含 listStatusFlag → status）。 */
	private Dict buildListCondition(DictPageQuery query) {
		Dict cond = new Dict();
		if (query == null) {
			return cond;
		}
		Short flag = query.getListStatusFlag();
		if (flag != null) {
			cond.setStatus(flag);
		}
		return cond;
	}

	@Override
	@Transactional
	public Serializable save(DictVO vo) {
		return persist(vo);
	}

	@Override
	@Transactional
	public Long persist(DictVO vo) {
		Objects.requireNonNull(vo, "vo");
		boolean isNew = vo.getId() == null || vo.getId() <= 0L;
		if (isNew) {
			return createDict(vo);
		}
		return updateDict(vo);
	}

	private Long createDict(DictVO vo) {
		String type = StringUtils.trimToEmpty(vo.getDictType());
		String value = StringUtils.trimToEmpty(vo.getDictValue());
		String label = StringUtils.trimToEmpty(vo.getDictLabel());
		if (StringUtils.isBlank(type)) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_TYPE_REQUIRED);
		}
		if (StringUtils.isBlank(value)) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_VALUE_REQUIRED);
		}
		if (StringUtils.isBlank(label)) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_LABEL_REQUIRED);
		}
		if (existsSameTypeValue(type, value, null)) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_TYPE_VALUE_EXISTS);
		}
		Dict entity = BeanUtil.copy(vo, Dict.class);
		entity.setId(null);
		entity.setDictType(type);
		entity.setDictValue(value);
		entity.setDictLabel(label.trim());
		if (entity.getSortNo() == null) {
			entity.setSortNo(0);
		}
		if (entity.getStatus() == null) {
			entity.setStatus((short) 1);
		}
		if (entity.getParentId() == null) {
			entity.setParentId(0L);
		}
		if (entity.getIsDefault() == null) {
			entity.setIsDefault((short) 0);
		}
		mapper.insertBase(entity);
		return entity.getId();
	}

	private Long updateDict(DictVO vo) {
		Dict existing = mapper.selectBaseByKey(vo.getId(), Dict.class);
		if (existing == null) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_NOT_FOUND);
		}
		String type = vo.getDictType() == null ? null : vo.getDictType().trim();
		String value = vo.getDictValue() == null ? null : vo.getDictValue().trim();
		String label = vo.getDictLabel() == null ? null : vo.getDictLabel().trim();
		if (StringUtils.isBlank(StringUtils.trimToEmpty(type))) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_TYPE_REQUIRED);
		}
		if (StringUtils.isBlank(StringUtils.trimToEmpty(value))) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_VALUE_REQUIRED);
		}
		if (StringUtils.isBlank(StringUtils.trimToEmpty(label))) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_LABEL_REQUIRED);
		}
		if (existsSameTypeValue(type, value, vo.getId())) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_TYPE_VALUE_CONFLICT);
		}
		Dict patch = BeanUtil.copy(vo, Dict.class);
		patch.setId(vo.getId());
		patch.setCreator(null);
		patch.setCreateTime(null);
		patch.setEditor(null);
		patch.setEditTime(null);
		patch.setDictType(type);
		patch.setDictValue(value);
		patch.setDictLabel(label.trim());
		if (hasUpdatablePatch(patch)) {
			mapper.updateBase(patch);
		}
		return vo.getId();
	}

	private boolean existsSameTypeValue(String dictType, String dictValue, Long excludeId) {
		Dict probe = new Dict();
		probe.setDictType(dictType);
		probe.setDictValue(dictValue);
		List<Dict> list = mapper.selectBase(probe, null);
		if (list == null || list.isEmpty()) {
			return false;
		}
		if (excludeId == null) {
			return true;
		}
		for (Dict d : list) {
			if (d.getId() != null && !d.getId().equals(excludeId)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasUpdatablePatch(Dict p) {
		return p.getDictType() != null || p.getDictLabel() != null || p.getDictValue() != null || p.getSortNo() != null
				|| p.getRemark() != null || p.getStatus() != null || p.getParentId() != null || p.getCssClass() != null
				|| p.getListClass() != null || p.getIsDefault() != null;
	}

	@Override
	@Transactional
	public Short toggleStatus(Long id) {
		Dict row = mapper.selectBaseByKey(id, Dict.class);
		if (row == null) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_NOT_FOUND);
		}
		short cur = row.getStatus() == null ? 0 : row.getStatus().shortValue();
		short next = cur == 1 ? (short) 0 : (short) 1;
		Dict patch = new Dict();
		patch.setId(id);
		patch.setStatus(next);
		mapper.updateBase(patch);
		return next;
	}

	@Override
	@Transactional
	public void hardDelete(Long id) {
		Dict row = mapper.selectBaseByKey(id, Dict.class);
		if (row == null) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_NOT_FOUND);
		}
		Integer n = mapper.deleteBaseByKey(id, Dict.class);
		if (n == null || n <= 0) {
			throw BizException.validWarn(BizMessageCode.Dict.DICT_HARD_DELETE_FAILED);
		}
	}
}
