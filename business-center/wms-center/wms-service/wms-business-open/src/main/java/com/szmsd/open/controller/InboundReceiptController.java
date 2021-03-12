package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = {"Inbound"})
@RestController
@RequestMapping("/api/inbound")
public class InboundReceiptController extends BaseController {

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @PreAuthorize("@ss.hasPermi('inbound:receiving')")
    @PostMapping("/receiving")
    @ApiOperation(value = "#B1 接收入库上架", notes = "#B1 接收入库上架")
    public R receiving(@RequestBody ReceivingRequest receivingRequest) {
        return inboundReceiptFeignService.receiving(receivingRequest);
    }
    
}
