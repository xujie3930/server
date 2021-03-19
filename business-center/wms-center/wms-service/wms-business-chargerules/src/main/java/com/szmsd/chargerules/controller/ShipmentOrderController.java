package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.service.IShipmentOrderService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.vo.CarrierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"ShipmentOrder"})
@RestController
@RequestMapping("/carrierService")
public class ShipmentOrderController extends BaseController {

    @Resource
    private IShipmentOrderService iShipmentOrderService;

    @PreAuthorize("@ss.hasPermi('products:services')")
    @GetMapping("/services")
    @ApiOperation(value = "挂号服务")
    public R<List<CarrierService>> services() {
        List<CarrierService> directServiceFeeData = iShipmentOrderService.services();
        return R.ok(directServiceFeeData);
    }

}
