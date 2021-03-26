package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.*;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricedProductServiceImpl extends AbstractPricedProductHttpRequest implements IPricedProductService {

    public PricedProductServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand) {
        return JSON.parseArray(httpPost(httpConfig.getPricedProduct().getPricedProducts(), getPricedProductsCommand), DirectServiceFeeData.class);
    }

    @Override
    public List<KeyValuePair> keyValuePairs() {
        return JSON.parseArray(httpPost(httpConfig.getPricedProduct().getKeyValuePairs(), null), KeyValuePair.class);
    }

    @Override
    public PageVO<PricedProduct> pageResult(PricedProductSearchCriteria pricedProductSearchCriteria) {
        return JSON.parseObject(httpPost(httpConfig.getPricedProduct().getPageResult(), pricedProductSearchCriteria), new TypeReference<PageVO<PricedProduct>>() {
        });
    }

    @Override
    public ResponseVO create(CreatePricedProductCommand createPricedProductCommand) {
        return JSON.parseObject(httpPost(httpConfig.getPricedProduct().getProducts(), createPricedProductCommand), ResponseVO.class);
    }

    @Override
    public PricedProductInfo getInfo(String productCode) {
        return JSON.parseObject(httpGet(httpConfig.getPricedProduct().getProducts(), null, productCode), PricedProductInfo.class);
    }

    @Override
    public ResponseVO update(UpdatePricedProductCommand updatePricedProductCommand) {
        return JSON.parseObject(httpPut(httpConfig.getPricedProduct().getProducts(), updatePricedProductCommand, updatePricedProductCommand.getCode()), ResponseVO.class);
    }

    @Override
    public FileStream exportFile(PricedProductCodesCriteria pricedProductCodesCriteria) {
        return httpPostFile(httpConfig.getPricedProduct().getExportFile(), pricedProductCodesCriteria);
    }

    @Override
    public ResponseObject<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command) {
        HttpResponseBody responseBody = httpPostBody(httpConfig.getPricedProduct().getPricing(), command);
        ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> responseObject = new ResponseObject.ResponseObjectWrapper<>();
        if (HttpStatus.SC_OK == responseBody.getStatus()) {
            responseObject.setSuccess(true);
            responseObject.setObject(JSON.parseObject(responseBody.getBody(), ChargeWrapper.class));
        } else {
            responseObject.setError(JSON.parseObject(responseBody.getBody(), ProblemDetails.class));
        }
        return responseObject;
    }

    @Override
    public ResponseVO grade(ChangeSheetGradeCommand changeSheetGradeCommand) {
        String text = httpPut(httpConfig.getPricedProduct().getProducts(), changeSheetGradeCommand, changeSheetGradeCommand.getProductCode(), changeSheetGradeCommand.getSheetCode(), httpConfig.getPricedProduct().getGrade());
        if ("true".equalsIgnoreCase(text)) {
            return null;
        }
        return JSON.parseObject(text, ResponseVO.class);
    }

    @Override
    public List<PricedProduct> inService(PricedProductInServiceCriteria criteria) {
        return JSONArray.parseArray(httpPost(httpConfig.getPricedProduct().getInService(), criteria), PricedProduct.class);
    }
}
