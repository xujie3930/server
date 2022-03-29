package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpPickupPackageFeignFallback;
import com.szmsd.http.dto.CreatePickupPackageCommand;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.HttpPickupPackageFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpPickupPackageFeignFallback.class)
public interface HttpPickupPackageFeignService {

    @GetMapping("/api/pickup/http/services")
    R<List<PickupPackageService>> services();

    @PostMapping("/api/pickup/http/create")
    R<ResponseVO> create(@RequestBody CreatePickupPackageCommand createPickupPackageCommand);

}
