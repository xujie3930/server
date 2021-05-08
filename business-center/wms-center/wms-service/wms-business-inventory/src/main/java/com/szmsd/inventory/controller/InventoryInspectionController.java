package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.InventoryInspectionVo;
import com.szmsd.inventory.service.IInventoryInspectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"库存验货"})
@RestController
@RequestMapping("/inventory/inspection")
public class InventoryInspectionController extends BaseController {

    @Resource
    private IInventoryInspectionService inventoryInspectionService;

    @PreAuthorize("@ss.hasPermi('inventory:inspection:add')")
    @PostMapping("/add")
    @ApiOperation(value = "库存验货 - 创建验货单")
    public R<Boolean> add(@RequestBody List<InventoryInspectionDetailsDTO> dto) {
        return R.ok(this.inventoryInspectionService.add(dto));
    }

    @PreAuthorize("@ss.hasPermi('inventory:inspection:findList')")
    @GetMapping("/list")
    @ApiOperation(value = "库存验货 - 查询列表")
    public TableDataInfo<InventoryInspectionVo> findList(InventoryInspectionQueryDTO dto) {
        startPage();
        List<InventoryInspectionVo> list = inventoryInspectionService.findList(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:inspection:details')")
    @GetMapping("/details/{inspectionNo}")
    @ApiOperation(value = "库存验货 - 查询详情")
    public R<InventoryInspectionVo> details(@PathVariable String inspectionNo) {
        return R.ok(this.inventoryInspectionService.details(inspectionNo));
    }

    @PreAuthorize("@ss.hasPermi('inventory:inspection:audit')")
    @PutMapping("/audit")
    @ApiOperation(value = "库存验货 - 审核")
    public R<Integer> audit(@RequestBody InventoryInspectionDTO dto) {
        return R.ok(this.inventoryInspectionService.audit(dto));
    }

    @PreAuthorize("@ss.hasPermi('inventory:inspection:inboundInventory')")
    @PostMapping("/inboundInventory")
    @ApiOperation(value = "库存验货 - 入库验货")
    public R<Boolean> inboundInventory(@RequestBody InboundInventoryInspectionDTO dto) {
        return R.ok(this.inventoryInspectionService.inboundInventory(dto));
    }

}
