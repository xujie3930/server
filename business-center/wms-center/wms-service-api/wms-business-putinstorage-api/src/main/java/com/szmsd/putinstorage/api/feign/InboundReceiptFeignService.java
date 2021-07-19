package com.szmsd.putinstorage.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.BusinessPutinstorageInterface;
import com.szmsd.putinstorage.api.factory.InboundReceiptFeignFallback;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.dto.ReceivingCompletedRequest;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.PutinstorageFeignService", name = BusinessPutinstorageInterface.SERVICE_NAME, fallbackFactory = InboundReceiptFeignFallback.class)
public interface InboundReceiptFeignService {

    @PostMapping("/inbound/receiving")
    R receiving(@RequestBody ReceivingRequest receivingRequest);

    @PostMapping("/inbound/receiving/completed")
    R completed(@RequestBody ReceivingCompletedRequest receivingCompletedRequest);

    @GetMapping("/inbound/receipt/info/{warehouseNo}")
    R<InboundReceiptInfoVO> info(@PathVariable("warehouseNo") String warehouseNo);

    @PostMapping("/inbound/receipt/saveOrUpdate")
    R<InboundReceiptInfoVO> saveOrUpdate(@RequestBody CreateInboundReceiptDTO createInboundReceiptDTO);

    @GetMapping("/inbound/statistics")
    R<List<InboundCountVO>> statistics(@SpringQueryMap InboundReceiptQueryDTO queryDTO);

    @GetMapping("/inbound/receipt/list")
    @ApiOperation(value = "查询", notes = "入库管理 - 分页查询")
    R<List<InboundReceiptVO>> list(@RequestBody InboundReceiptQueryDTO queryDTO);
}
