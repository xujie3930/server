package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IBasService;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"基础信息"})
@RestController
@RequestMapping("/api/bas/http")
public class BasController extends BaseController {
    @Resource
    private IBasService iBasService;

    @PostMapping("/createPacking")
    @ApiOperation(value = "新增/修改物料")
    public R<ResponseVO> createPacking(@RequestBody PackingRequest packingRequest) {
        ResponseVO responseVO = iBasService.createPacking(packingRequest);
        return R.ok(responseVO);
    }

    @PostMapping("/createProduct")
    @ApiOperation(value = "新增/修改sku")
    public R<ResponseVO> createProduct(@RequestBody ProductRequest productRequest) {
        ResponseVO responseVO = iBasService.createProduct(productRequest);
        return R.ok(responseVO);
    }

    @PostMapping("/createMaterial")
    @ApiOperation(value = "新增/包材")
    public R<ResponseVO> createMaterial(@RequestBody MaterialRequest materialRequest) {
        ResponseVO responseVO = iBasService.createMaterial(materialRequest);
        return R.ok(responseVO);
    }

    @PostMapping("/createSeller")
    @ApiOperation(value = "新增/修改卖家")
    public R<ResponseVO> createSeller(@RequestBody SellerRequest sellerRequest) {
        ResponseVO responseVO = iBasService.createSeller(sellerRequest);
        return R.ok(responseVO);
    }

    @PostMapping("/specialOperation/type")
    @ApiOperation(value = "A6 新增特殊操作类型")
    public R<ResponseVO> create(@RequestBody SpecialOperationRequest specialOperationRequest) {
        ResponseVO responseVO = iBasService.save(specialOperationRequest);
        return responseVO != null ? R.ok(responseVO) : R.failed();
    }

    @PostMapping("/specialOperation/result")
    @ApiOperation(value = "A7 更新特殊操作结果")
    public R<ResponseVO> update(@RequestBody SpecialOperationResultRequest specialOperationResultRequest) {
        ResponseVO responseVO = iBasService.update(specialOperationResultRequest);
        return responseVO != null ? R.ok(responseVO) : R.failed();
    }
}
