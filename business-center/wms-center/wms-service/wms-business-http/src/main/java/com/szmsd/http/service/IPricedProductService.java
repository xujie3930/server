package com.szmsd.http.service;

import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.vo.DirectServiceFeeData;

import java.util.List;

public interface IPricedProductService {

    List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand);

}
