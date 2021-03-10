package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPricedProductFeignFallback;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.vo.DirectServiceFeeData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.HtpPricedProductFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPricedProductFeignFallback.class)
public interface HtpPricedProductFeignService {

    @PostMapping("/api/products/http/pricedProducts")
    R<List<DirectServiceFeeData>> pricedProducts(@RequestBody GetPricedProductsCommand getPricedProductsCommand);
}
