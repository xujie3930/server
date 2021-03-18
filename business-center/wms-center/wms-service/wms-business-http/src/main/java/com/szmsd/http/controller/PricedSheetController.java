package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"PricedSheet"})
@RestController
@RequestMapping("/api/sheets/http")
public class PricedSheetController {

    @Resource
    private IPricedProductService pricedProductService;

    @PostMapping("/pricedProducts")
    @ApiOperation(value = "根据包裹基本信息获取可下单报价产品")
    public R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand) {
        List<DirectServiceFeeData> directServiceFeeData = pricedProductService.pricedProducts(getPricedProductsCommand);
        return R.ok(directServiceFeeData);
    }

    @GetMapping("/keyValuePairs")
    @ApiOperation(value = "查询产品下拉列表，返回list数据")
    public R<List<KeyValuePair>> keyValuePairs() {
        List<KeyValuePair> directServiceFeeData = pricedProductService.keyValuePairs();
        return R.ok(directServiceFeeData);
    }
}
