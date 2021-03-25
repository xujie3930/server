package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutboundPackingDetail;
import com.szmsd.delivery.service.IDelOutboundPackingDetailService;
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
 * 装箱明细信息 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */


@Api(tags = {"装箱明细信息"})
@RestController
@RequestMapping("/del-outbound-packing-detail")
public class DelOutboundPackingDetailController extends BaseController {

    @Resource
    private IDelOutboundPackingDetailService delOutboundPackingDetailService;

    /**
     * 查询装箱明细信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询装箱明细信息模块列表", notes = "查询装箱明细信息模块列表")
    public TableDataInfo list(DelOutboundPackingDetail delOutboundPackingDetail) {
        startPage();
        List<DelOutboundPackingDetail> list = delOutboundPackingDetailService.selectDelOutboundPackingDetailList(delOutboundPackingDetail);
        return getDataTable(list);
    }

    /**
     * 导出装箱明细信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:export')")
    @Log(title = "装箱明细信息模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出装箱明细信息模块列表", notes = "导出装箱明细信息模块列表")
    public void export(HttpServletResponse response, DelOutboundPackingDetail delOutboundPackingDetail) throws IOException {
        List<DelOutboundPackingDetail> list = delOutboundPackingDetailService.selectDelOutboundPackingDetailList(delOutboundPackingDetail);
        ExcelUtil<DelOutboundPackingDetail> util = new ExcelUtil<DelOutboundPackingDetail>(DelOutboundPackingDetail.class);
        util.exportExcel(response, list, "DelOutboundPackingDetail");

    }

    /**
     * 获取装箱明细信息模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取装箱明细信息模块详细信息", notes = "获取装箱明细信息模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundPackingDetailService.selectDelOutboundPackingDetailById(id));
    }

    /**
     * 新增装箱明细信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:add')")
    @Log(title = "装箱明细信息模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增装箱明细信息模块", notes = "新增装箱明细信息模块")
    public R add(@RequestBody DelOutboundPackingDetail delOutboundPackingDetail) {
        return toOk(delOutboundPackingDetailService.insertDelOutboundPackingDetail(delOutboundPackingDetail));
    }

    /**
     * 修改装箱明细信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:edit')")
    @Log(title = "装箱明细信息模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改装箱明细信息模块", notes = "修改装箱明细信息模块")
    public R edit(@RequestBody DelOutboundPackingDetail delOutboundPackingDetail) {
        return toOk(delOutboundPackingDetailService.updateDelOutboundPackingDetail(delOutboundPackingDetail));
    }

    /**
     * 删除装箱明细信息模块
     */
    @PreAuthorize("@ss.hasPermi('DelOutboundPackingDetail:DelOutboundPackingDetail:remove')")
    @Log(title = "装箱明细信息模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除装箱明细信息模块", notes = "删除装箱明细信息模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delOutboundPackingDetailService.deleteDelOutboundPackingDetailByIds(ids));
    }

}
