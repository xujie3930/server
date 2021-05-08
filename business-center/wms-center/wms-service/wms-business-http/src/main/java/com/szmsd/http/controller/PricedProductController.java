package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/api/products/http")
public class PricedProductController extends BaseController {

    @Resource
    private IPricedProductService iPricedProductService;

    @PostMapping("/pricedProducts")
    @ApiOperation(value = "根据包裹基本信息获取可下单报价产品")
    public R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand) {
        List<DirectServiceFeeData> directServiceFeeData = iPricedProductService.pricedProducts(getPricedProductsCommand);
        return R.ok(directServiceFeeData);
    }

    @PostMapping("/pageResult")
    @ApiOperation(value = "分页查询产品列表，返回指定页面的数据，以及统计总记录数")
    public R<PageVO<PricedProduct>> pageResult(@RequestBody PricedProductSearchCriteria pricedProductSearchCriteria) {
        PageVO<PricedProduct> pageResult = iPricedProductService.pageResult(pricedProductSearchCriteria);
        return pageResult == null ? R.ok(PageVO.empty()) : R.ok(pageResult);
    }

    @GetMapping("/keyValuePairs")
    @ApiOperation(value = "查询产品下拉列表，返回list数据")
    public R<List<KeyValuePair>> keyValuePairs() {
        List<KeyValuePair> directServiceFeeData = iPricedProductService.keyValuePairs();
        return R.ok(directServiceFeeData);
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建报价产品信息")
    public R<ResponseVO> create(@RequestBody CreatePricedProductCommand createPricedProductCommand) {
        ResponseVO create = iPricedProductService.create(createPricedProductCommand);
        if (StringUtils.isEmpty(create.getErrors())) {
            create.setSuccess(true);
        } else {
            create.setSuccess(false);
        }
        return R.ok(create);
    }

    @GetMapping("/info/{productCode}")
    @ApiOperation(value = "根据产品代码获取计价产品信息")
    public R<PricedProductInfo> info(@PathVariable("productCode") String productCode) {
        PricedProductInfo pricedProductInfo = iPricedProductService.getInfo(productCode);
        return R.ok(pricedProductInfo);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改报价产品信息")
    public R<ResponseVO> update(@RequestBody UpdatePricedProductCommand updatePricedProductCommand) {
        ResponseVO update = iPricedProductService.update(updatePricedProductCommand);
        return R.ok(update);
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出产品信息列表")
    public R<FileStream> exportFile(@RequestBody PricedProductCodesCriteria pricedProductCodesCriteria) {
        FileStream fileStream = iPricedProductService.exportFile(pricedProductCodesCriteria);
        return R.ok(fileStream);
    }

    @PostMapping("/pricing")
    @ApiOperation(value = "计算包裹的费用")
    public R<ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails>> pricing(@RequestBody CalcShipmentFeeCommand command) {
        return R.ok(iPricedProductService.pricing(command));
    }

    @PostMapping("/grade")
    @ApiOperation(value = "修改一个计价产品信息的报价表对应的等级和生效时间段")
    public R<ResponseVO> grade(@RequestBody ChangeSheetGradeCommand changeSheetGradeCommand) {
        ResponseVO grade = iPricedProductService.grade(changeSheetGradeCommand);
        return R.ok(grade);
    }

    @PostMapping("/inService")
    @ApiOperation(value = "根据客户代码国家等信息获取可下单产品")
    public R<List<PricedProduct>> inService(@RequestBody PricedProductInServiceCriteria criteria) {
        return R.ok(iPricedProductService.inService(criteria));
    }

}
