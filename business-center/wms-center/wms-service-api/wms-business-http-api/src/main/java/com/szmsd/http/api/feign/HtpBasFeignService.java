package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpInboundFeignFallback;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.dto.PackingRequest;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.dto.SellerRequest;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpBasFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpInboundFeignFallback.class)
public interface HtpBasFeignService {
    @PostMapping("/api/bas/http/createPacking")
    R<ResponseVO> createPacking(@RequestBody PackingRequest packingRequest);
    @PostMapping("/api/bas/http/createProduct")
    R<ResponseVO> createProduct(@RequestBody ProductRequest productRequest);
    @PostMapping("/api/bas/http/createSeller")
    R<ResponseVO> createSeller(@RequestBody SellerRequest sellerRequest);

}
