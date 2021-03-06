package com.szmsd.delivery.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.BusinessOpenInterface;
import com.szmsd.delivery.api.feign.factory.DelOutboundFeignFallback;
import com.szmsd.delivery.dto.DelOutboundDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:32
 */
@FeignClient(contextId = "FeignClient.DelOutboundFeignService", name = BusinessOpenInterface.SERVICE_NAME, fallbackFactory = DelOutboundFeignFallback.class)
public interface DelOutboundFeignService {

    /**
     * 出库管理 - 创建
     *
     * @param dto dto
     * @return Integer
     */
    @PostMapping("/api/outbound/shipment")
    R<Integer> add(@RequestBody @Validated DelOutboundDto dto);
}
