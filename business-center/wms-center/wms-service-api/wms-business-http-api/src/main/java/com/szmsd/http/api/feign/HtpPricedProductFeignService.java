package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPricedProductFeignFallback;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.HtpPricedProductFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPricedProductFeignFallback.class)
public interface HtpPricedProductFeignService {

    @PostMapping("/api/products/http/pricedProducts")
    R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand);

    @GetMapping("/api/products/http/keyValuePairs")
    R<List<KeyValuePair>> keyValuePairs();

    @PostMapping("/api/products/http/pageResult")
    PageVO pageResult(@RequestBody PricedProductSearchCriteria pricedProductSearchCriteria);

    @PostMapping("/api/products/http/create")
    R<ResponseVO> create(@RequestBody CreatePricedProductCommand createPricedProductCommand);

    @GetMapping("/api/products/http/info/{productCode}")
    R<PricedProductInfo> info(@PathVariable("productCode") String productCode);

    @PostMapping("/api/products/http/update")
    R<ResponseVO> update(@RequestBody UpdatePricedProductCommand updatePricedProductCommand);

    @PostMapping("/api/products/http/exportFile")
    R<FileStream> exportFile(@RequestBody PricedProductCodesCriteria pricedProductCodesCriteria);

    @PostMapping("/api/products/http/pricing")
    R<ResponseObject<ChargeWrapper, ProblemDetails>> pricing(@RequestBody CalcShipmentFeeCommand command);
}
