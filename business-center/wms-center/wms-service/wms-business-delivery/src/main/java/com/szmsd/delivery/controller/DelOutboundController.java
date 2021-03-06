package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.service.IDelOutboundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理"})
@RestController
@RequestMapping("/api/outbound")
public class DelOutboundController extends BaseController {

    @Resource
    private IDelOutboundService delOutboundService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询出库单模块列表", notes = "查询出库单模块列表")
    public TableDataInfo list(DelOutbound delOutbound) {
        startPage();
        List<DelOutbound> list = delOutboundService.selectDelOutboundList(delOutbound);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:export')")
    @Log(title = "出库单模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出出库单模块列表", notes = "导出出库单模块列表")
    public void export(HttpServletResponse response, DelOutbound delOutbound) throws IOException {
        List<DelOutbound> list = delOutboundService.selectDelOutboundList(delOutbound);
        ExcelUtil<DelOutbound> util = new ExcelUtil<DelOutbound>(DelOutbound.class);
        util.exportExcel(response, list, "DelOutbound");
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取出库单模块详细信息", notes = "获取出库单模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundService.selectDelOutboundById(id));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:add')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - 创建出库单")
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> add(@RequestBody @Validated DelOutboundDto dto) {
        return R.ok(delOutboundService.insertDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:edit')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PutMapping("/shipment")
    @ApiOperation(value = "出库管理 - 修改出库单")
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> edit(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundDto dto) {
        return R.ok(delOutboundService.updateDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:remove')")
    @Log(title = "出库单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/shipment")
    @ApiOperation(value = "出库管理 - 删除出库单")
    public R<Integer> remove(@RequestBody List<String> ids) {
        return R.ok(delOutboundService.deleteDelOutboundByIds(ids));
    }

}
