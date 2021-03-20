package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liulei
 */
@Api(tags = {"recharge callback"})
@RestController
@RequestMapping("/api/recharge")
public class RechargeCallbackController extends BaseController {

    @Resource
    private RechargesFeignService rechargesFeignService;

    @PostMapping("/rechargeCallback")
    @ApiOperation(value = "充值回调", notes = "充值回调")
    public String rechargeCallback(@RequestBody RechargesCallbackRequestDTO requestDTO) {
        R.getDataAndException(rechargesFeignService.rechargeCallback(requestDTO));
        return "0000";
    }
}
