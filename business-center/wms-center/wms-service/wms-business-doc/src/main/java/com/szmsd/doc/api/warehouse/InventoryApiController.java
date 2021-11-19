package com.szmsd.doc.api.warehouse;

import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.RUtils;
import com.szmsd.doc.api.sku.resp.BaseProductResp;
import com.szmsd.doc.api.warehouse.req.InventoryAvailableQueryReq;
import com.szmsd.doc.api.warehouse.resp.InventoryAvailableListResp;
import com.szmsd.doc.api.warehouse.resp.SkuInventoryAgeResp;
import com.szmsd.doc.utils.AuthenticationUtil;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.InventoryStockByRangeDTO;
import com.szmsd.putinstorage.domain.vo.SkuInventoryStockRangeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Validated
@Api(tags = {"库存信息"})
@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    @Resource
    private InventoryFeignClientService inventoryFeignService;
    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/inbound/queryAvailableList")
    @ApiOperation(value = "查询可用库存-根据仓库编码，SKU - 分页", notes = "根据客户代码、所在仓库、sku查询SKU库存")
    public TableDataInfo<InventoryAvailableListResp> queryAvailableList(@Valid @RequestBody InventoryAvailableQueryReq queryDTO) {
        queryDTO.setCusCode(AuthenticationUtil.getSellerCode());
        TableDataInfo<InventoryAvailableListVO> dataInfo = inventoryFeignService.queryAvailableList(queryDTO.convertThis());
        TableDataInfo<InventoryAvailableListResp> inventoryAvailableListResp = new TableDataInfo<>();
        BeanUtils.copyProperties(dataInfo, inventoryAvailableListResp);
        List<InventoryAvailableListVO> rows = dataInfo.getRows();
        List<InventoryAvailableListResp> collect = rows.stream().map(x -> {
            InventoryAvailableListResp availableListResp = new InventoryAvailableListResp();
            BeanUtils.copyProperties(x, availableListResp);
            return availableListResp;
        }).collect(Collectors.toList());
        inventoryAvailableListResp.setRows(collect);
        return inventoryAvailableListResp;
//        List<InventoryAvailableListResp> returnList = inventoryAvailableListVOS
//                .stream().filter(Objects::nonNull).map(InventoryAvailableListResp::convertThis).collect(Collectors.toList());
//
//        return R.ok(returnList);
    }

    @PreAuthorize("hasAuthority('client')")
    @GetMapping("/queryInventoryAge/weeks/bySku/{warehouseCode}/{sku}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", required = true, value = "仓库code", example = "NJ"),
            @ApiImplicitParam(name = "sku", required = true, value = "sku", example = "CN20210601006"),
    })
    @ApiOperation(value = "库龄-查询sku的库龄", notes = "按周返回SKU的库龄")
    public R<List<SkuInventoryAgeResp>> queryInventoryAgeBySku(@PathVariable("warehouseCode") String warehouseCode, @PathVariable("sku") String sku) {
        List<SkuInventoryAgeVo> skuInventoryAgeVos = inventoryFeignService.queryInventoryAgeBySku(warehouseCode, sku);
        return R.ok(SkuInventoryAgeResp.convert(skuInventoryAgeVos, warehouseCode, sku));
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("/querySkuStockByRange")
    @ApiOperation(value = "查询sku的入库状况", notes = "查询sku的入库状况-指定范围内")
    public R<List<SkuInventoryStockRangeVo>> querySkuStockByRange(@RequestBody @Validated InventoryStockByRangeDTO inventoryStockByRangeDTO) {
        inventoryStockByRangeDTO.valid();
        R<List<SkuInventoryStockRangeVo>> listR = inboundReceiptFeignService.querySkuStockByRange(inventoryStockByRangeDTO);
        List<SkuInventoryStockRangeVo> dataAndException = RUtils.getDataAndException(listR);
        return R.ok(dataAndException);
    }

}
