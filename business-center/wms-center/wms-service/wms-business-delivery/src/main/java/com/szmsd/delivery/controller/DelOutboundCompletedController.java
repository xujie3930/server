package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
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
 * 出库单完成记录 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */


@Api(tags = {"出库单完成记录"})
@RestController
@RequestMapping("/del-outbound-completed")
public class DelOutboundCompletedController extends BaseController {

    @Resource
    private IDelOutboundCompletedService delOutboundCompletedService;

    /**
     * 查询出库单完成记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询出库单完成记录模块列表", notes = "查询出库单完成记录模块列表")
    public TableDataInfo list(DelOutboundCompleted delOutboundCompleted) {
        startPage();
        List<DelOutboundCompleted> list = delOutboundCompletedService.selectDelOutboundCompletedList(delOutboundCompleted);
        return getDataTable(list);
    }

    /**
     * 导出出库单完成记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:export')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出出库单完成记录模块列表", notes = "导出出库单完成记录模块列表")
    public void export(HttpServletResponse response, DelOutboundCompleted delOutboundCompleted) throws IOException {
        List<DelOutboundCompleted> list = delOutboundCompletedService.selectDelOutboundCompletedList(delOutboundCompleted);
        ExcelUtil<DelOutboundCompleted> util = new ExcelUtil<DelOutboundCompleted>(DelOutboundCompleted.class);
        util.exportExcel(response, list, "DelOutboundCompleted");

    }

    /**
     * 获取出库单完成记录模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取出库单完成记录模块详细信息", notes = "获取出库单完成记录模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundCompletedService.selectDelOutboundCompletedById(id));
    }

    /**
     * 新增出库单完成记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:add')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增出库单完成记录模块", notes = "新增出库单完成记录模块")
    public R add(@RequestBody DelOutboundCompleted delOutboundCompleted) {
        return toOk(delOutboundCompletedService.insertDelOutboundCompleted(delOutboundCompleted));
    }

    /**
     * 修改出库单完成记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:edit')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改出库单完成记录模块", notes = "修改出库单完成记录模块")
    public R edit(@RequestBody DelOutboundCompleted delOutboundCompleted) {
        return toOk(delOutboundCompletedService.updateDelOutboundCompleted(delOutboundCompleted));
    }

    /**
     * 删除出库单完成记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCompleted:DelOutboundCompleted:remove')")
    @Log(title = "出库单完成记录模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除出库单完成记录模块", notes = "删除出库单完成记录模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delOutboundCompletedService.deleteDelOutboundCompletedByIds(ids));
    }

}
