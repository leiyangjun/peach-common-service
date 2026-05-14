package org.peach.common.web;

import java.io.Serializable;
import java.util.List;

import org.peach.common.dto.DictPageQuery;
import org.peach.common.mvc.result.ApiResult;
import org.peach.common.mybatis.model.vo.PageVO;
import org.peach.common.mybatis.model.vo.SortVO;
import org.peach.common.service.impl.DictServiceImpl;
import org.peach.common.vo.DictVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 码表配置 REST：分页条件使用 {@link DictPageQuery}；行数据与保存体为 {@link DictVO}；启用/停用仅改 {@code status}，物理删除走 hard。
 */
@RestController
@RequestMapping("/dict")
@Tag(name = "码表配置", description = "分页、详情、保存、状态切换、物理删除")
public class DictController {

	private final DictServiceImpl service;

	public DictController(DictServiceImpl service) {
		this.service = service;
	}

	@Operation(summary = "根据主键查询详情")
	@GetMapping("/{id}")
	public ApiResult<DictVO> getById(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		return ApiResult.ok(service.getById(id));
	}

	@Operation(summary = "分页查询", description = "查询参数见 DictPageQuery：searchValue（类型/标签/存储值 OR 模糊）、listStatusFlag（null=全部，0=停用，1=启用）")
	@GetMapping("/page")
	public ApiResult<PageInfo<DictVO>> page(@ModelAttribute DictPageQuery query, PageVO page, SortVO sort) {
		return ApiResult.ok(service.listPage(query, page, sort));
	}

	/** 列出库中已存在的字典类型（去重），供新增/编辑抽屉表单下拉 */
	@Operation(summary = "字典类型列表", description = "返回 cmn_dict 中不重复且非空的 dict_type（字典序），供新增/编辑抽屉表单下拉")
	@GetMapping("/types")
	public ApiResult<List<String>> types() {
		return ApiResult.ok(service.listDistinctTypes());
	}

	@Operation(summary = "保存或更新", description = "新增勿带主键；更新须带主键。响应 data 为主键。")
	@PostMapping
	public ApiResult<Serializable> save(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DictVO JSON", required = true) @RequestBody DictVO body) {
		return ApiResult.ok(service.save(body));
	}

	@Operation(summary = "切换启用状态", description = "在 status=1 与 0 之间切换，返回切换后的值")
	@PostMapping("/{id}/toggle-status")
	public ApiResult<Short> toggleStatus(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		return ApiResult.ok(service.toggleStatus(id));
	}

	@Operation(summary = "物理删除", description = "从库表永久删除该行")
	@DeleteMapping("/{id}/hard")
	public ApiResult<Void> hardDelete(
			@Parameter(name = "id", required = true, in = ParameterIn.PATH) @PathVariable Long id) {
		service.hardDelete(id);
		return ApiResult.ok();
	}
}
