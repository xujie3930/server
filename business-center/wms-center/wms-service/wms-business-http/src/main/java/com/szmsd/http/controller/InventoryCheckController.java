package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IInventoryCheckService;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"基础信息"})
@RestController
@RequestMapping("/api/inventory/check/http")
public class InventoryCheckController extends BaseController {

    @Resource
    private IInventoryCheckService inventoryCheckService;

    @PostMapping("/counting")
    @ApiOperation(value = "I3 创建/修改盘点单")
    public R<ResponseVO> counting(@RequestBody CountingRequest countingRequest) {
        ResponseVO responseVO = inventoryCheckService.counting(countingRequest);
        return responseVO != null ? R.ok(responseVO) : R.failed();
    }
}
