package com.szmsd.putinstorage.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.BusinessPutinstorageInterface;
import com.szmsd.putinstorage.api.factory.InboundReceiptFeignFallback;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.ReceivingCompletedRequest;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping("/receipt/saveOrUpdate")
    @ApiOperation(value = "创建/修改", notes = "入库管理 - 新增/创建")
    R<InboundReceiptInfoVO> saveOrUpdate(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO);
}
