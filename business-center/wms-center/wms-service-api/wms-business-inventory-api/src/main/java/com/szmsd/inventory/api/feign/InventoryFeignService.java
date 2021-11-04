package com.szmsd.inventory.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.BusinessInventoryInterface;
import com.szmsd.inventory.api.factory.InventoryFeignFallback;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "FeignClient.InventoryFeignService", name = BusinessInventoryInterface.SERVICE_NAME, fallbackFactory = InventoryFeignFallback.class)
public interface InventoryFeignService {

    @PostMapping("/inventory/inbound")
    R inbound(@RequestBody InboundInventoryDTO receivingRequest);

    @PostMapping("/inventory/skuVolume")
    R<List<InventorySkuVolumeVO>> querySkuVolume(@RequestBody InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO);

    @PostMapping("/inventory/queryAvailableList2")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 不分页")
    R<List<InventoryAvailableListVO>> queryAvailableList(@RequestBody InventoryAvailableQueryDto queryDto);

    @PostMapping("/inventory/queryOnlyAvailable")
    @ApiOperation(value = "根据仓库编码，SKU查询可用库存 - 单条")
    R<InventoryAvailableListVO> queryOnlyAvailable(@RequestBody InventoryAvailableQueryDto queryDto);

    @PostMapping("/inventory/querySku")
    R<List<InventoryVO>> querySku(@RequestBody InventoryAvailableQueryDto queryDto);

    @PostMapping("/inventory/queryOnlySku")
    R<InventoryVO> queryOnlySku(@RequestBody InventoryAvailableQueryDto queryDto);

    @PostMapping("/inventory/freeze")
    R<Integer> freeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unFreeze")
    R<Integer> unFreeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unFreezeAndFreeze")
    R<Integer> unFreezeAndFreeze(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/deduction")
    R<Integer> deduction(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unDeduction")
    R<Integer> unDeduction(@RequestBody InventoryOperateListDto operateListDto);

    @PostMapping("/inventory/unDeductionAndDeduction")
    R<Integer> unDeductionAndDeduction(@RequestBody InventoryOperateListDto operateListDto);

    @GetMapping("/inventory/page")
    TableDataInfo<InventorySkuVO> page(@RequestParam(value = "warehouseCode") String warehouseCode, @RequestParam(value = "sku") String sku, @RequestParam(value = "cusCode") String cusCode, @RequestParam(value = "pageSize") Integer pageSize);

    @PostMapping("/inventory/adjustment")
    R adjustment(@RequestBody InventoryAdjustmentDTO inventoryAdjustmentDTO);

    @GetMapping("/inventory/getWarehouseSku")
    R<List<Inventory>> getWarehouseSku();

    @GetMapping("/inventory/queryInventoryAge/weeks/bySku/{warehouseCode}/{sku}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", value = "仓库code", example = "NJ"),
            @ApiImplicitParam(name = "sku", value = "sku", example = "CN20210601006"),
    })
    @ApiOperation(value = "查询sku的库龄", notes = "查询sku的库龄-周")
    R<List<SkuInventoryAgeVo>> queryInventoryAgeBySku(@PathVariable("warehouseCode") String warehouseCode, @PathVariable("sku") String sku);

    @PostMapping("/inventory/queryFinishList")
    @ApiOperation(value = "查询已完成的单号", notes = "查询已完成的单号")
    TableDataInfo<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO);
}
