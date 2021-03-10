package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.DirectServiceFeeData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/api/products/http")
public class PricedProductController {

    @Resource
    private IPricedProductService pricedProductService;

    @PostMapping("/pricedProducts")
    @ApiOperation(value = "根据包裹基本信息获取可下单报价产品")
    public R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand) {
        List<DirectServiceFeeData> directServiceFeeData = pricedProductService.pricedProducts(getPricedProductsCommand);
        return R.ok(directServiceFeeData);
    }
}
