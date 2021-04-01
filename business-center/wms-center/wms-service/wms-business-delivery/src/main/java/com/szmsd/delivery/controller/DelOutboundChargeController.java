package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.service.IDelOutboundChargeService;
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
 * 出库单费用明细 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */


@Api(tags = {"出库单费用明细"})
@RestController
@RequestMapping("/del-outbound-charge")
public class DelOutboundChargeController extends BaseController {

    @Resource
    private IDelOutboundChargeService delOutboundChargeService;

    /**
     * 查询出库单费用明细模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询出库单费用明细模块列表", notes = "查询出库单费用明细模块列表")
    public TableDataInfo list(DelOutboundCharge delOutboundCharge) {
        startPage();
        List<DelOutboundCharge> list = delOutboundChargeService.selectDelOutboundChargeList(delOutboundCharge);
        return getDataTable(list);
    }

    /**
     * 导出出库单费用明细模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:export')")
    @Log(title = "出库单费用明细模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出出库单费用明细模块列表", notes = "导出出库单费用明细模块列表")
    public void export(HttpServletResponse response, DelOutboundCharge delOutboundCharge) throws IOException {
        List<DelOutboundCharge> list = delOutboundChargeService.selectDelOutboundChargeList(delOutboundCharge);
        ExcelUtil<DelOutboundCharge> util = new ExcelUtil<DelOutboundCharge>(DelOutboundCharge.class);
        util.exportExcel(response, list, "DelOutboundCharge");

    }

    /**
     * 获取出库单费用明细模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取出库单费用明细模块详细信息", notes = "获取出库单费用明细模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundChargeService.selectDelOutboundChargeById(id));
    }

    /**
     * 新增出库单费用明细模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:add')")
    @Log(title = "出库单费用明细模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增出库单费用明细模块", notes = "新增出库单费用明细模块")
    public R add(@RequestBody DelOutboundCharge delOutboundCharge) {
        return toOk(delOutboundChargeService.insertDelOutboundCharge(delOutboundCharge));
    }

    /**
     * 修改出库单费用明细模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:edit')")
    @Log(title = "出库单费用明细模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改出库单费用明细模块", notes = "修改出库单费用明细模块")
    public R edit(@RequestBody DelOutboundCharge delOutboundCharge) {
        return toOk(delOutboundChargeService.updateDelOutboundCharge(delOutboundCharge));
    }

    /**
     * 删除出库单费用明细模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundCharge:DelOutboundCharge:remove')")
    @Log(title = "出库单费用明细模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除出库单费用明细模块", notes = "删除出库单费用明细模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delOutboundChargeService.deleteDelOutboundChargeByIds(ids));
    }

}
