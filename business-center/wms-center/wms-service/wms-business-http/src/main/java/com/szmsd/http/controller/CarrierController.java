package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.CreateShipmentOrderCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.service.ICarrierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:47
 */
@Api(tags = {"Carrier"})
@RestController
@RequestMapping("/api/carrier/http")
public class CarrierController extends BaseController {

    @Autowired
    private ICarrierService carrierService;

    @PostMapping("/shipmentOrder")
    @ApiOperation(value = "创建承运商物流订单（客户端）")
    public R<ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails>> shipmentOrder(@RequestBody CreateShipmentOrderCommand command) {
        return R.ok(carrierService.shipmentOrder(command));
    }
}
