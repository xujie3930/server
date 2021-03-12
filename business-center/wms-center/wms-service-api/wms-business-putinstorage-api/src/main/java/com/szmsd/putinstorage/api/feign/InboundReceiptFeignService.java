package com.szmsd.putinstorage.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.BusinessPutinstorageInterface;
import com.szmsd.putinstorage.api.factory.InboundReceiptFeignFallback;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.PutinstorageFeignService", name = BusinessPutinstorageInterface.SERVICE_NAME, fallbackFactory = InboundReceiptFeignFallback.class)
public interface InboundReceiptFeignService {

    @PostMapping("/inbound/receiving")
    R receiving(@RequestBody ReceivingRequest receivingRequest);
}
