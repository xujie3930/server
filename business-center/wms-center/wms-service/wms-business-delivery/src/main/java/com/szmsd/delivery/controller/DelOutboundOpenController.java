package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundOpenService;
import com.szmsd.delivery.timer.DelOutboundThirdPartyTimer;
import com.szmsd.delivery.timer.DelOutboundTimer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private IDelOutboundOpenService delOutboundOpenService;

    @Resource
    private DelOutboundTimer delOutboundTimer;

    @Resource
    private DelOutboundThirdPartyTimer delOutboundThirdPartyTimer;

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - Open - #D1 接收出库单状态", position = 100)
    @ApiImplicitParam(name = "dto", value = "ShipmentRequestDto", dataType = "ShipmentRequestDto")
    public R<Integer> shipmentOperationType(@RequestBody @Validated ShipmentRequestDto dto) {
        return R.ok(this.delOutboundService.shipmentOperationType(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/packing")
    @ApiOperation(value = "出库管理 - Open - #D2 接收出库包裹信息(拆分为D21和D22)", position = 200)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public R<Integer> shipmentPacking(@RequestBody @Validated ShipmentPackingMaterialRequestDto dto) {
        return R.ok(this.delOutboundOpenService.shipmentPacking(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/packing/material")
    @ApiOperation(value = "出库管理 - Open - #D21 接收出库包裹包材信息", position = 210)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public R<Integer> shipmentPackingMaterial(@RequestBody ShipmentPackingMaterialRequestDto dto) {
        return R.ok(this.delOutboundOpenService.shipmentPackingMaterial(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/packing/measure")
    @ApiOperation(value = "出库管理 - Open - #D22 接收出库包裹测量信息", position = 220)
    @ApiImplicitParam(name = "dto", value = "ShipmentPackingMaterialRequestDto", dataType = "ShipmentPackingMaterialRequestDto")
    public R<Integer> shipmentPackingMeasure(@RequestBody ShipmentPackingMaterialRequestDto dto) {
        return R.ok(this.delOutboundOpenService.shipmentPackingMeasure(dto));
    }

    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PostMapping("/shipment/containers")
    @ApiOperation(value = "出库管理 - Open - #D3 接收批量出库单类型装箱信息", position = 300)
    @ApiImplicitParam(name = "dto", value = "ShipmentContainersRequestDto", dataType = "ShipmentContainersRequestDto")
    public R<Integer> shipmentContainers(@RequestBody @Validated ShipmentContainersRequestDto dto) {
        return R.ok(this.delOutboundOpenService.shipmentContainers(dto));
    }

    @PostMapping(value = "/notifyBringVerify")
    public R<String> notifyBringVerify() {
        delOutboundTimer.bringVerify();
        return R.ok("通知完成");
    }

    @PostMapping(value = "/notifyAmazonLogisticsRouteIdy")
    public R<String> notifyAmazonLogisticsRouteIdy() {
        delOutboundThirdPartyTimer.notifyAmazonLogisticsRouteIdy();
        return R.ok("通知获取亚马逊挂号定时任务执行任务完成");
    }
}
