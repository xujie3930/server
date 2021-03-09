package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.dto.PackageMeasureRequestDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import com.szmsd.delivery.service.IDelOutboundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - Open"})
@ApiSort(110)
@RestController
@RequestMapping("/api/outbound/open")
public class DelOutboundOpenController extends BaseController {

    @Resource
    private IDelOutboundService delOutboundService;

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - Open - #D1 接收出库单状态", position = 100)
    @ApiImplicitParam(name = "dto", value = "ShipmentRequestDto", dataType = "ShipmentRequestDto")
    public R<Integer> shipmentOperationType(@RequestBody @Validated ShipmentRequestDto dto) {
        return R.ok(this.delOutboundService.shipmentOperationType(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/measure")
    @ApiOperation(value = "出库管理 - Open - #D2 接收出库包裹测量信息", position = 200)
    @ApiImplicitParam(name = "dto", value = "PackageMeasureRequestDto", dataType = "PackageMeasureRequestDto")
    public R<Integer> shipmentMeasure(@RequestBody @Validated PackageMeasureRequestDto dto) {
        return R.ok(this.delOutboundService.shipmentMeasure(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/packing")
    @ApiOperation(value = "出库管理 - Open - #D3 接收出库包裹使用包材", position = 300)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public R<Integer> shipmentPacking(@RequestBody @Validated ShipmentPackingMaterialRequestDto dto) {
        return R.ok(this.delOutboundService.shipmentPacking(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/containers")
    @ApiOperation(value = "出库管理 - Open - #D4 接收批量出库单类型装箱信息", position = 400)
    @ApiImplicitParam(name = "dto", value = "ShipmentContainersRequestDto", dataType = "ShipmentContainersRequestDto")
    public R<Integer> shipmentContainers(@RequestBody @Validated ShipmentContainersRequestDto dto) {
        return R.ok(this.delOutboundService.shipmentContainers(dto));
    }
}
