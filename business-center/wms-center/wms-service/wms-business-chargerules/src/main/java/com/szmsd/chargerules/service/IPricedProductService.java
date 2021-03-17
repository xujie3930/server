package com.szmsd.chargerules.service;

import com.msd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;

import java.util.List;

public interface IPricedProductService {

    List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand);

    List<KeyValuePair> keyValuePairs();

    TableDataInfo<PricedProduct> selectList(PricedProductQueryDTO pricedProductQueryDTO);
}
