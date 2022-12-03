package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.factory.RefundRequestFeignFallback;
import com.szmsd.finance.dto.RefundRequestListDTO;
import com.szmsd.finance.dto.RefundReviewDTO;
import com.szmsd.finance.enums.BusinessFssInterface;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.RefundRequestFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = RefundRequestFeignFallback.class)
public interface RefundRequestFeignService {

    @ApiOperation(value = "添加退费信息")
    @PostMapping("/refundRequest/add")
    R add(@RequestBody RefundRequestListDTO addDTO);

    @ApiOperation(value = "审核退费")
    @PostMapping("/refundRequest/approve")
    R approve(@RequestBody RefundReviewDTO refundReviewDTO);
}
