package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpRmiFeignFallback;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpRmiFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpRmiFeignFallback.class)
public interface HtpRmiFeignService {

    @PostMapping("/api/rmi")
    R<HttpResponseVO> rmi(@RequestBody HttpRequestDto dto);

    @PostMapping("/api/rmi/sync")
    R<HttpResponseVO> rmiSync(@RequestBody HttpRequestDto dto);
}
