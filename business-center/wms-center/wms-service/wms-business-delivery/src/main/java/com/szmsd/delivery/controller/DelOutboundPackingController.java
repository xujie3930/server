package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.service.IDelOutboundPackingService;
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
 * 装箱信息 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */


@Api(tags = {"装箱信息"})
@RestController
@RequestMapping("/del-outbound-packing")
public class DelOutboundPackingController extends BaseController {

    @Resource
    private IDelOutboundPackingService delOutboundPackingService;

    /**
     * 查询装箱信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询装箱信息模块列表", notes = "查询装箱信息模块列表")
    public TableDataInfo list(DelOutboundPacking delOutboundPacking) {
        startPage();
        List<DelOutboundPacking> list = delOutboundPackingService.selectDelOutboundPackingList(delOutboundPacking);
        return getDataTable(list);
    }

    /**
     * 导出装箱信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:export')")
    @Log(title = "装箱信息模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出装箱信息模块列表", notes = "导出装箱信息模块列表")
    public void export(HttpServletResponse response, DelOutboundPacking delOutboundPacking) throws IOException {
        List<DelOutboundPacking> list = delOutboundPackingService.selectDelOutboundPackingList(delOutboundPacking);
        ExcelUtil<DelOutboundPacking> util = new ExcelUtil<DelOutboundPacking>(DelOutboundPacking.class);
        util.exportExcel(response, list, "DelOutboundPacking");

    }

    /**
     * 获取装箱信息模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取装箱信息模块详细信息", notes = "获取装箱信息模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundPackingService.selectDelOutboundPackingById(id));
    }

    /**
     * 新增装箱信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:add')")
    @Log(title = "装箱信息模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增装箱信息模块", notes = "新增装箱信息模块")
    public R add(@RequestBody DelOutboundPacking delOutboundPacking) {
        return toOk(delOutboundPackingService.insertDelOutboundPacking(delOutboundPacking));
    }

    /**
     * 修改装箱信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:edit')")
    @Log(title = "装箱信息模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改装箱信息模块", notes = "修改装箱信息模块")
    public R edit(@RequestBody DelOutboundPacking delOutboundPacking) {
        return toOk(delOutboundPackingService.updateDelOutboundPacking(delOutboundPacking));
    }

    /**
     * 删除装箱信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPacking:DelOutboundPacking:remove')")
    @Log(title = "装箱信息模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除装箱信息模块", notes = "删除装箱信息模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delOutboundPackingService.deleteDelOutboundPackingByIds(ids));
    }

}
