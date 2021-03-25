package com.szmsd.http.service;

import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.*;

import java.util.List;

public interface IPricedProductService {

    List<DirectServiceFeeData> pricedProducts(GetPricedProductsCommand getPricedProductsCommand);

    List<KeyValuePair> keyValuePairs();

    PageVO<PricedProduct> pageResult(PricedProductSearchCriteria pricedProductSearchCriteria);

    ResponseVO create(CreatePricedProductCommand createPricedProductCommand);

    PricedProductInfo getInfo(String productCode);

    ResponseVO update(UpdatePricedProductCommand updatePricedProductCommand);

    FileStream exportFile(PricedProductCodesCriteria pricedProductCodesCriteria);

    /**
     * 计算包裹的费用
     *
     * @param command command
     * @return ResponseObject<ChargeWrapper, ProblemDetails>
     */
    ResponseObject<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command);

    ResponseVO grade(ChangeSheetGradeCommand changeSheetGradeCommand);
}
