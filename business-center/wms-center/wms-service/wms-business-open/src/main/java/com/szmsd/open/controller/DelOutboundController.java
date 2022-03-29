package com.szmsd.open.controller;

import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import com.szmsd.open.vo.ResponseVO;
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
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DelOutboundController extends BaseController {

    @Resource
    private DelOutboundClientService delOutboundClientService;

    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - #D1 接收出库单状态", position = 100)
    @ApiImplicitParam(name = "dto", value = "ShipmentRequestDto", dataType = "ShipmentRequestDto")
    public ResponseVO shipment(@RequestBody @Validated ShipmentRequestDto dto) {
        delOutboundClientService.shipment(dto);
        return ResponseVO.ok();
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/package")
    @ApiOperation(value = "出库管理 - #D2 接收出库包裹信息", position = 200)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public ResponseVO shipmentPacking(@RequestBody @Validated ShipmentPackingMaterialRequestDto dto) {
        delOutboundClientService.shipmentPacking(dto);
        return ResponseVO.ok();
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/package/material")
    @ApiOperation(value = "出库管理 - #D21 接收出库包裹包材信息", position = 210)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public ResponseVO shipmentPackingMaterial(@RequestBody @Validated ShipmentPackingMaterialRequestDto dto) {
        delOutboundClientService.shipmentPackingMaterial(dto);
        return ResponseVO.ok();
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/package/measure")
    @ApiOperation(value = "出库管理 - #D22 接收出库包裹测量信息", position = 220)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public ResponseVO shipmentPackingMeasure(@RequestBody @Validated ShipmentPackingMaterialRequestDto dto) {
        delOutboundClientService.shipmentPackingMeasure(dto);
        return ResponseVO.ok();
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/containers")
    @ApiOperation(value = "出库管理 - #D3 接收批量出库单类型装箱信息", position = 300)
    @ApiImplicitParam(name = "dto", value = "ShipmentContainersRequestDto", dataType = "ShipmentContainersRequestDto")
    public ResponseVO shipmentContainers(@RequestBody @Validated ShipmentContainersRequestDto dto) {
        delOutboundClientService.shipmentContainers(dto);
        return ResponseVO.ok();
    }

}
