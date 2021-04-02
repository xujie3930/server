package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundPackageQueue;
import com.szmsd.delivery.service.IDelOutboundPackageQueueService;
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
 * 出库单核重记录 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-04-02
 */


@Api(tags = {"出库单核重记录"})
@RestController
@RequestMapping("/del-outbound-package-queue")
public class DelOutboundPackageQueueController extends BaseController {

    @Resource
    private IDelOutboundPackageQueueService delOutboundPackageQueueService;

    /**
     * 查询出库单核重记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackageQueue:DelOutboundPackageQueue:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询出库单核重记录模块列表", notes = "查询出库单核重记录模块列表")
    public TableDataInfo list(DelOutboundPackageQueue delOutboundPackageQueue) {
        startPage();
        List<DelOutboundPackageQueue> list = delOutboundPackageQueueService.selectDelOutboundPackageQueueList(delOutboundPackageQueue);
        return getDataTable(list);
    }

    /**
     * 导出出库单核重记录模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackageQueue:DelOutboundPackageQueue:export')")
    @Log(title = "出库单核重记录模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出出库单核重记录模块列表", notes = "导出出库单核重记录模块列表")
    public void export(HttpServletResponse response, DelOutboundPackageQueue delOutboundPackageQueue) throws IOException {
        List<DelOutboundPackageQueue> list = delOutboundPackageQueueService.selectDelOutboundPackageQueueList(delOutboundPackageQueue);
        ExcelUtil<DelOutboundPackageQueue> util = new ExcelUtil<DelOutboundPackageQueue>(DelOutboundPackageQueue.class);
        util.exportExcel(response, list, "DelOutboundPackageQueue");

    }

    /**
     * 获取出库单核重记录模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackageQueue:DelOutboundPackageQueue:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取出库单核重记录模块详细信息", notes = "获取出库单核重记录模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundPackageQueueService.selectDelOutboundPackageQueueById(id));
    }

    /**
     * 新增出库单核重记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackageQueue:DelOutboundPackageQueue:add')")
    @Log(title = "出库单核重记录模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增出库单核重记录模块", notes = "新增出库单核重记录模块")
    public R add(@RequestBody DelOutboundPackageQueue delOutboundPackageQueue) {
        return toOk(delOutboundPackageQueueService.insertDelOutboundPackageQueue(delOutboundPackageQueue));
    }

    /**
     * 修改出库单核重记录模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackageQueue:DelOutboundPackageQueue:edit')")
    @Log(title = "出库单核重记录模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改出库单核重记录模块", notes = "修改出库单核重记录模块")
    public R edit(@RequestBody DelOutboundPackageQueue delOutboundPackageQueue) {
        return toOk(delOutboundPackageQueueService.updateDelOutboundPackageQueue(delOutboundPackageQueue));
    }

}
