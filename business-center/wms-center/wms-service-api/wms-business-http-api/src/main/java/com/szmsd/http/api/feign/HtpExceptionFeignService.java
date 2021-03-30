package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpExceptionFeignFallback;
import com.szmsd.http.api.feign.fallback.HtpInboundFeignFallback;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.dto.PackingRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpExceptionFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpExceptionFeignFallback.class)
public interface HtpExceptionFeignService {
    @PostMapping("/api/exception/http/processing")
    R<ResponseVO> processing(@RequestBody ExceptionProcessRequest exceptionProcessRequest);
}
