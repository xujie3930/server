package com.szmsd.open.controller;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.domain.R;
import com.szmsd.open.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"BaseInfo"})
@RestController
@RequestMapping("/api/base")
public class BaseInfoController extends BaseController {

    @Resource
    private BasWarehouseClientService basWarehouseClientService;

    @Resource
    private BaseProductClientService baseProductClientService;

    @PostMapping("/warehouse")
    @ApiOperation(value = "#A1 创建/更新仓库")
    public ResponseVO add(@RequestBody @Validated AddWarehouseRequest addWarehouseRequest) {
        R.getDataAndException(basWarehouseClientService.saveOrUpdate(addWarehouseRequest));
        return ResponseVO.ok();
    }

    @PostMapping("/product/measuring")
    @ApiOperation(value = "#A2 产品（SKU）测量")
    public ResponseVO measuringProduct(@RequestBody @Validated MeasuringProductRequest measuringProductRequest) {
        R.getDataAndException(baseProductClientService.measuringProduct(measuringProductRequest));
        return ResponseVO.ok();
    }



}
