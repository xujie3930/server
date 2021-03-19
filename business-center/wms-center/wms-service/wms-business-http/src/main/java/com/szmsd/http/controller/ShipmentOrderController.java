package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.service.IShipmentOrderService;
import com.szmsd.http.vo.CarrierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"ShipmentOrder"})
@RestController
@RequestMapping("/api/carrierService/shipmentOrders/http")
public class ShipmentOrderController extends BaseController {

    @Resource
    private IShipmentOrderService iShipmentOrderService;

    @GetMapping("/services")
    @ApiOperation(value = "获取可用的承运商服务名称(管理端)")
    public R<List<CarrierService>> services() {
        List<CarrierService> directServiceFeeData = iShipmentOrderService.services();
        return R.ok(directServiceFeeData);
    }

}
