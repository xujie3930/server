package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/products")
    public class PricedProductController extends BaseController {

    @Resource
    private IPricedProductService pricedProductService;

    @PostMapping("/pricedProducts")
    @ApiOperation(value = "根据包裹基本信息获取可下单报价产品")
    public R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand) {
        List<DirectServiceFeeData> directServiceFeeData = pricedProductService.pricedProducts(getPricedProductsCommand);
        return R.ok(directServiceFeeData);
    }

    @GetMapping("/page")
    @ApiOperation(value = "根据包裹基本信息获取可下单报价产品")
    public TableDataInfo<PricedProduct> page(PricedProductQueryDTO pricedProductQueryDTO) {
        TableDataInfo<PricedProduct> pricedProductList = pricedProductService.selectList(pricedProductQueryDTO);
        return pricedProductList;
    }

    @GetMapping("/keyValuePairs")
    @ApiOperation(value = "查询产品下拉列表，返回list数据")
    public R<List<KeyValuePair>> keyValuePairs() {
        List<KeyValuePair> directServiceFeeData = pricedProductService.keyValuePairs();
        return R.ok(directServiceFeeData);
    }
}
