package org.peach.common.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.peach.common.entity.ButtonApi;
import org.peach.common.mybatis.mapper.BaseMapper;

@Mapper
public interface ButtonApiMapper extends BaseMapper<ButtonApi> {

	@Delete("DELETE FROM cmn_button_api WHERE button_id = #{buttonId}")
	int physicalDeleteByButtonId(@Param("buttonId") Long buttonId);

	@Delete({
			"<script>",
			"DELETE FROM cmn_button_api WHERE button_id IN",
			"<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
			"#{id}",
			"</foreach>",
			"</script>",
	})
	int physicalDeleteByButtonIds(@Param("ids") List<Long> ids);
}