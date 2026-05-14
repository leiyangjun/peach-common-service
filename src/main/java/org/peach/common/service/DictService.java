package org.peach.common.service;

import java.io.Serializable;
import java.util.List;

import org.peach.common.dto.DictPageQuery;
import org.peach.common.mybatis.model.vo.PageVO;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.mybatis.service.BaseInterfaceService;
import org.peach.common.vo.DictVO;

import com.github.pagehelper.PageInfo;

/**
 * 码表配置：分页（独立查询对象）、详情、保存、状态切换、物理删除。
 * <p>
 * 分页说明：{@link #listPage(DictPageQuery, PageVO, SortVO)} 为 HTTP GET 推荐入口（含 {@code listStatusFlag} 等分页专用参数）；<br>
 * 继承的 {@link org.peach.common.mybatis.service.BaseInterfaceService#listPage(java.io.Serializable, org.peach.common.mybatis.model.vo.SearchVO, PageVO, SortVO)} 在实现类中映射为
 * 仅 {@code searchValue}（与实体上的 {@code @SearchValue} 列做 OR 模糊），不含 {@code listStatusFlag}（与 {@link org.peach.common.mvc.web.BaseController} 默认分页绑定时若需状态筛选请走 DictPageQuery 接口）。
 * </p>
 */
public interface DictService extends BaseInterfaceService<DictVO> {

	/**
	 * 关键字分页：条件取自 {@link DictPageQuery}，与行数据 VO 字段解耦。
	 */
	PageInfo<DictVO> listPage(DictPageQuery query, PageVO page, SortVO sort);

	/**
	 * 列出库中已存在的字典类型编码（去重），供前端主档筛选与表单下拉。
	 */
	List<String> listDistinctTypes();

	/**
	 * 新增或修改：{@code id == null} 或 {@code id <= 0} 为新增；返回主键。
	 */
	Long persist(DictVO vo);

	/**
	 * 切换 {@code status}（启用/停用）。
	 *
	 * @return 切换后的 status（0 或 1）
	 */
	Short toggleStatus(Long id);

	/** 物理删除单行。 */
	void hardDelete(Long id);
}
