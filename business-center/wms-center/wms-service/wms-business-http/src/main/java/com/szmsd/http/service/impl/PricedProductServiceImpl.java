package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.service.IPricedProductService;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
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
        return JSON.parseObject(httpPost(httpConfig.getPricedProduct().getPageResult(), pricedProductSearchCriteria), new TypeReference<PageVO<PricedProduct>>(){});
    }

}
