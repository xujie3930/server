package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.DirectServiceFeeData;
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
}
