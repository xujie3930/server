package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import com.szmsd.inventory.service.IInventoryInspectionService;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.IInventoryWrapperService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"库存"})
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController {

    @Resource
    private IInventoryService inventoryService;
    @Resource
    private IInventoryRecordService iInventoryRecordService;
    @Autowired
    private IInventoryWrapperService inventoryWrapperService;

    @Resource
    private IInventoryInspectionService inventoryInspectionService;

    @PreAuthorize("@ss.hasPermi('inventory:inbound')")
    @PostMapping("/inbound")
    @ApiOperation(value = "#入库上架", notes = "库存管理 - Inbound - /api/inbound/receiving #B1 接收入库上架 - 修改库存")
    public R inbound(@RequestBody InboundInventoryDTO inboundInventoryDTO) {
        inventoryService.inbound(inboundInventoryDTO);
        try {
            inventoryInspectionService.inboundInventory(inboundInventoryDTO);
        } catch (Exception e) {
            log.error("入库验货失败", e);
        }
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inventory:page')")
    @GetMapping("/page")
    @ApiOperation(value = "查询", notes = "库存管理 - 分页查询")
    public TableDataInfo<InventorySkuVO> page(InventorySkuQueryDTO inventorySkuQueryDTO) {
        startPage();
        List<InventorySkuVO> list = inventoryService.selectList(inventorySkuQueryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:page')")
    @GetMapping("/export")
    @ApiOperation(value = "导出", notes = "库存管理 - 导出")
    public TableDataInfo<InventorySkuVO> export(InventorySkuQueryDTO inventorySkuQueryDTO, HttpServletResponse response) {
        List<InventorySkuVO> list = inventoryService.selectList(inventorySkuQueryDTO);
        ExcelUtil<InventorySkuVO> util = new ExcelUtil<>(InventorySkuVO.class);
        util.exportExcel(response, list, "产品库存_" + DateUtils.dateTimeNow());
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/record/page")
    @ApiOperation(value = "日志记录", notes = "库存日志")
    public TableDataInfo<InventoryRecordVO> logsPage(InventoryRecordQueryDTO inventoryRecordQueryDTO) {
        startPage();
        List<InventoryRecordVO> inventoryRecordVOS = iInventoryRecordService.selectList(inventoryRecordQueryDTO);
        return getDataTable(inventoryRecordVOS);
    }

    @PreAuthorize("@ss.hasPermi('inbound:skuvolume')")
    @PostMapping("/skuVolume")
    @ApiOperation(value = "获取库存SKU体积", notes = "获取库存SKU体积 - 按入库单")
    public R<List<InventorySkuVolumeVO>> querySkuVolume(@RequestBody InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
        startPage();
        List<InventorySkuVolumeVO> inventorySkuVolumeVOS = iInventoryRecordService.selectSkuVolume(inventorySkuVolumeQueryDTO);
        return R.ok(inventorySkuVolumeVOS);
    }

    @PreAuthorize("@ss.hasPermi('inbound:queryAvailableList')")
    @PostMapping("/queryAvailableList")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 分页")
    public TableDataInfo<InventoryAvailableListVO> queryAvailableList(@RequestBody InventoryAvailableQueryDto queryDto) {
        startPage();
        return getDataTable(this.inventoryService.queryAvailableList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:queryAvailableList2')")
    @PostMapping("/queryAvailableList2")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 不分页")
    public R<List<InventoryAvailableListVO>> queryAvailableList2(@RequestBody InventoryAvailableQueryDto queryDto) {
        return R.ok(this.inventoryService.queryAvailableList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:queryOnlyAvailable')")
    @PostMapping("/queryOnlyAvailable")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 单条")
    public R<InventoryAvailableListVO> queryOnlyAvailable(@RequestBody InventoryAvailableQueryDto queryDto) {
        return R.ok(this.inventoryService.queryOnlyAvailable(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:querySku')")
    @PostMapping("/querySku")
    @ApiOperation(value = "查询SKU信息")
    public R<List<InventoryVO>> querySku(@RequestBody InventoryAvailableQueryDto queryDto) {
        return R.ok(this.inventoryService.querySku(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:queryOnlySku')")
    @PostMapping("/queryOnlySku")
    @ApiOperation(value = "查询SKU信息")
    public R<InventoryVO> queryOnlySku(@RequestBody InventoryAvailableQueryDto queryDto) {
        return R.ok(this.inventoryService.queryOnlySku(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:freeze')")
    @PostMapping("/freeze")
    @ApiOperation(value = "库存管理 - 冻结库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> freeze(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.freeze(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:unFreeze')")
    @PostMapping("/unFreeze")
    @ApiOperation(value = "库存管理 - 取消冻结库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> unFreeze(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.unFreeze(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:unFreezeAndFreeze')")
    @PostMapping("/unFreezeAndFreeze")
    @ApiOperation(value = "库存管理 - 重置冻结库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> unFreezeAndFreeze(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.unFreezeAndFreeze(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:deduction')")
    @PostMapping("/deduction")
    @ApiOperation(value = "库存管理 - 扣减库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> deduction(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.deduction(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:unDeduction')")
    @PostMapping("/unDeduction")
    @ApiOperation(value = "库存管理 - 取消扣减库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> unDeduction(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.unDeduction(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:unDeductionAndDeduction')")
    @PostMapping("/unDeductionAndDeduction")
    @ApiOperation(value = "库存管理 - 重置扣减库存")
    @ApiImplicitParam(name = "operateListDto", value = "参数", dataType = "InventoryOperateListDto")
    public R<Integer> unDeductionAndDeduction(@RequestBody InventoryOperateListDto operateListDto) {
        return R.ok(this.inventoryWrapperService.unDeductionAndDeduction(operateListDto));
    }

    @PreAuthorize("@ss.hasPermi('inbound:adjustment')")
    @PostMapping("/adjustment")
    @ApiOperation(value = "库存管理 - 调整")
    public R adjustment(@RequestBody InventoryAdjustmentDTO inventoryAdjustmentDTO) {
        inventoryService.adjustment(inventoryAdjustmentDTO);
        return R.ok();
    }
}
