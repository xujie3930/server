package com.szmsd.putinstorage.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.BusinessPutinstorageInterface;
import com.szmsd.putinstorage.api.factory.InboundReceiptFeignFallback;
import com.szmsd.putinstorage.domain.dto.ReceivingCompletedRequest;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.PutinstorageFeignService", name = BusinessPutinstorageInterface.SERVICE_NAME, fallbackFactory = InboundReceiptFeignFallback.class)
public interface InboundReceiptFeignService {

    @PostMapping("/inbound/receiving")
    R receiving(@RequestBody ReceivingRequest receivingRequest);

    @PostMapping("/inbound/receiving/completed")
    R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest);

    @GetMapping("/inbound/receipt/info/{warehouseNo}")
    R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo);

}
