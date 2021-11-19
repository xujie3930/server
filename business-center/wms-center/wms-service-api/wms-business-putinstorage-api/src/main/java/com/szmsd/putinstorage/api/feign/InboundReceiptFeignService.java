package com.szmsd.putinstorage.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.api.BusinessPutinstorageInterface;
import com.szmsd.putinstorage.api.factory.InboundReceiptFeignFallback;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.domain.vo.SkuInventoryStockRangeVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "FeignClient.PutinstorageFeignService", name = BusinessPutinstorageInterface.SERVICE_NAME, fallbackFactory = InboundReceiptFeignFallback.class)
public interface InboundReceiptFeignService {

    @PostMapping("/inbound/receiving")
    R receiving(@RequestBody ReceivingRequest receivingRequest);

    @PostMapping("/inbound/receiving/completed")
    R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest);

    @GetMapping("/inbound/receipt/info/{warehouseNo}")
    @ApiOperation(value = "详情", notes = "入库管理 - 详情（包含明细）")
    R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo);

    @PostMapping("/inbound/receipt/saveOrUpdate")
    R<InboundReceiptInfoVO> saveOrUpdate(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO);

    @GetMapping("/inbound/statistics")
    R<List<InboundCountVO>> statistics(@SpringQueryMap InboundReceiptQueryDTO queryDTO);

    @GetMapping("/inbound/receipt/list")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    R<List<InboundReceiptVO>> list(@RequestBody InboundReceiptQueryDTO queryDTO);

    @PostMapping("/inbound/receipt/saveOrUpdate/batch")
    @ApiOperation(value = "创建/修改-批量", notes = "批量 入库管理 - 新增/创建")
    R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(@RequestBody List<CreateInboundReceiptDTO> createInboundReceiptDTOList);

    @DeleteMapping("/inbound/receipt/cancel/{warehouseNo}")
    @ApiOperation(value = "取消", notes = "入库管理 - 取消")
    R cancel(@PathVariable("warehouseNo") String warehouseNo);

    @PostMapping("/inbound/receiving/tracking")
    @ApiOperation(value = "#B5 物流到货接收确认", notes = "#B5 物流到货接收确认")
    R tracking(ReceivingTrackingRequest receivingCompletedRequest);

    @PostMapping("/inbound/open/receipt/page")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    TableDataInfo<InboundReceiptVO> postPage(@RequestBody InboundReceiptQueryDTO queryDTO);

    @PostMapping("/inbound/querySkuStockByRange")
    @ApiOperation(value = "查询sku的入库状况", notes = "查询sku的入库状况-指定范围内")
    R<List<SkuInventoryStockRangeVo>> querySkuStockByRange(@RequestBody InventoryStockByRangeDTO inventoryStockByRangeDTO);

    @PostMapping("/inbound/updateTrackingNo")
    @ApiOperation(value = "修改快递单号信息", notes = "修改快递单号信息")
    R<Integer> updateTracking(@Validated @RequestBody UpdateTrackingNoRequest updateTrackingNoRequest);
}
