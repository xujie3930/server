package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundCombination;
import com.szmsd.delivery.service.IDelOutboundCombinationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * 出库单组合信息 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-07-02
 */


@Api(tags = {"出库单组合信息"})
@RestController
@RequestMapping("/del-outbound-combination")
public class DelOutboundCombinationController extends BaseController {

    @Resource
    private IDelOutboundCombinationService delOutboundCombinationService;

    /**
     * 查询出库单组合信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询出库单组合信息模块列表", notes = "查询出库单组合信息模块列表")
    public TableDataInfo list(DelOutboundCombination delOutboundCombination) {
        startPage();
        List<DelOutboundCombination> list = delOutboundCombinationService.selectDelOutboundCombinationList(delOutboundCombination);
        return getDataTable(list);
    }

    /**
     * 导出出库单组合信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:export')")
    @Log(title = "出库单组合信息模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出出库单组合信息模块列表", notes = "导出出库单组合信息模块列表")
    public void export(HttpServletResponse response, DelOutboundCombination delOutboundCombination) throws IOException {
        List<DelOutboundCombination> list = delOutboundCombinationService.selectDelOutboundCombinationList(delOutboundCombination);
        ExcelUtil<DelOutboundCombination> util = new ExcelUtil<DelOutboundCombination>(DelOutboundCombination.class);
        util.exportExcel(response, list, "DelOutboundCombination");

    }

    /**
     * 获取出库单组合信息模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取出库单组合信息模块详细信息", notes = "获取出库单组合信息模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundCombinationService.selectDelOutboundCombinationById(id));
    }

    /**
     * 新增出库单组合信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:add')")
    @Log(title = "出库单组合信息模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增出库单组合信息模块", notes = "新增出库单组合信息模块")
    public R add(@RequestBody DelOutboundCombination delOutboundCombination) {
        return toOk(delOutboundCombinationService.insertDelOutboundCombination(delOutboundCombination));
    }

    /**
     * 修改出库单组合信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:edit')")
    @Log(title = "出库单组合信息模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改出库单组合信息模块", notes = "修改出库单组合信息模块")
    public R edit(@RequestBody DelOutboundCombination delOutboundCombination) {
        return toOk(delOutboundCombinationService.updateDelOutboundCombination(delOutboundCombination));
    }

    /**
     * 删除出库单组合信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCombination:DelOutboundCombination:remove')")
    @Log(title = "出库单组合信息模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除出库单组合信息模块", notes = "删除出库单组合信息模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delOutboundCombinationService.deleteDelOutboundCombinationByIds(ids));
    }

}
