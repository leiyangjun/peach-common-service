package org.peach.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.peach.common.entity.Dict;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {

	/**
	 * 查询库中已出现的字典类型（去重、排序），供管理端下拉与筛选；不含逻辑删除条件（本表无 deleted 列）。
 *
 * @author leiyangjun
 */
	@Select("""
			SELECT DISTINCT dict_type
			FROM cmn_dict
			WHERE dict_type IS NOT NULL AND btrim(dict_type) <> ''
			ORDER BY dict_type
			""")
	List<String> selectDistinctDictTypes();
}
