package com.szmsd.doc.api.warehouse;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.doc.api.warehouse.req.InventoryAvailableQueryReq;
import com.szmsd.doc.api.warehouse.resp.InventoryAvailableListResp;
import com.szmsd.doc.api.warehouse.resp.SkuInventoryAgeResp;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Api(tags = {"库存信息"})
@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController extends BaseController {

    @Resource
    private InventoryFeignClientService inventoryFeignService;

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/inbound/queryAvailableList")
    @ApiOperation(value = "查询可用库存-根据仓库编码，SKU - 不分页")
    public R<List<InventoryAvailableListResp>> queryAvailableList(@RequestBody InventoryAvailableQueryReq queryDTO) {
        List<InventoryAvailableListVO> inventoryAvailableListVOS = inventoryFeignService.queryAvailableList(queryDTO.convertThis());

        List<InventoryAvailableListResp> returnList = inventoryAvailableListVOS
                .stream().filter(Objects::nonNull).map(InventoryAvailableListResp::convertThis).collect(Collectors.toList());

        return R.ok(returnList);
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("/queryInventoryAge/weeks/bySku/{warehouseCode}/{sku}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "warehouseCode", value = "仓库code", example = "NJ"),
            @ApiImplicitParam(name = "sku", value = "sku", example = "CN20210601006"),
    })
    @ApiOperation(value = "库龄-查询sku的库龄", notes = "查询sku的库龄-周")
    public R<List<SkuInventoryAgeResp>> queryInventoryAgeBySku(@PathVariable("warehouseCode") String warehouseCode, @PathVariable("sku") String sku) {
        List<SkuInventoryAgeVo> skuInventoryAgeVos = inventoryFeignService.queryInventoryAgeBySku(warehouseCode, sku);
        return R.ok(SkuInventoryAgeResp.convert(skuInventoryAgeVos, warehouseCode, sku));
    }
}
