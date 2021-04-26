package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.service.IInboundService;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = {"Inbound"})
@ApiSort(100)
@RestController
@RequestMapping("/api/inbound/http")
public class InboundController extends BaseController {

    @Resource
    private IInboundService iInboundService;

    @PostMapping("/receipt")
    @ApiOperation(value = "B1 创建入库单")
    public R<CreateReceiptResponse> create(@RequestBody CreateReceiptRequest createReceiptRequestDTO) {
        CreateReceiptResponse createReceiptResponse = iInboundService.create(createReceiptRequestDTO);
        return R.ok(createReceiptResponse);
    }

    @DeleteMapping("/receipt")
    @ApiOperation(value = "B2 取消入库单")
    public R<ResponseVO> cancel(@RequestBody CancelReceiptRequest cancelReceiptRequestDTO) {
        ResponseVO cancel = iInboundService.cancel(cancelReceiptRequestDTO);
        return R.ok(cancel);
    }

}
