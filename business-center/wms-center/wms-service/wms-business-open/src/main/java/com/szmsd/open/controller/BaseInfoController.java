package com.szmsd.open.controller;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BasePackingClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.CreatePackingRequest;
import com.szmsd.bas.dto.EtSkuAttributeRequest;
import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.chargerules.api.service.SpecialOperationClientService;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
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

    @Resource
    private SpecialOperationClientService specialOperationClientService;

    @Resource
    private BasePackingClientService basePackingClientService;

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

    @PostMapping("/product/attribute")
    @ApiOperation(value = "et验收sku属性")
    public ResponseVO attribute(@RequestBody @Validated EtSkuAttributeRequest etSkuAttributeRequest) {
        R.getDataAndException(baseProductClientService.attribute(etSkuAttributeRequest));
        return ResponseVO.ok();
    }

    @PostMapping("/specialOperation")
    @ApiOperation(value = "#A3 创建/更新特殊操作")
    public ResponseVO specialOperation(@RequestBody @Validated BasSpecialOperationRequestDTO basSpecialOperationRequestDTO) {
        R.getDataAndException(specialOperationClientService.add(basSpecialOperationRequestDTO));
        return ResponseVO.ok();
    }


    @PostMapping("/packings")
    @ApiOperation(value = "#A4 新增/修改包材信息")
    public ResponseVO createPackings(@RequestBody @Validated CreatePackingRequest createPackingRequest) {
        R.getDataAndException(basePackingClientService.createPackings(createPackingRequest));
        return ResponseVO.ok();
    }



}
