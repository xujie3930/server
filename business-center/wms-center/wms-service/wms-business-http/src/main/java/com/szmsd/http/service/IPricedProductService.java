package com.szmsd.http.service;

import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;

import java.util.List;

public interface IPricedProductService {

    List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand);

    List<KeyValuePair> keyValuePairs();

    PageVO<PricedProduct> pageResult(PricedProductSearchCriteria pricedProductSearchCriteria);
}
