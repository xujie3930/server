package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPricedProductFeignFallback;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "FeignClient.HtpPricedProductFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPricedProductFeignFallback.class)
public interface HtpPricedProductFeignService {

    @PostMapping("/api/products/http/pricedProducts")
    R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand);

    @GetMapping("/api/products/http/keyValuePairs")
    R<List<KeyValuePair>> keyValuePairs();

    @PostMapping("/api/products/http/pageResult")
    R<PageVO<PricedProduct>> pageResult(@RequestBody PricedProductSearchCriteria pricedProductSearchCriteria);

    @PostMapping("/api/products/http/create")
    R<ResponseVO> create(@RequestBody CreatePricedProductCommand createPricedProductCommand);

    @GetMapping("/api/products/http/info/{productCode}")
    R<PricedProductInfo> info(@PathVariable("productCode") String productCode);

    @PutMapping("/api/products/http/update")
    R<ResponseVO> update(@RequestBody UpdatePricedProductCommand updatePricedProductCommand);

    @PostMapping("/api/products/http/exportFile")
    R<FileStream> exportFile(@RequestBody PricedProductCodesCriteria pricedProductCodesCriteria);

    @PostMapping("/api/products/http/pricing")
    R<ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails>> pricing(@RequestBody CalcShipmentFeeCommand command);

    @PostMapping("/api/products/http/inService")
    R<List<PricedProduct>> inService(@RequestBody PricedProductInServiceCriteria criteria);

    @PostMapping("/api/products/http/grade")
    R<ResponseVO> grade(@RequestBody ChangeSheetGradeCommand changeSheetGradeCommand);
}
