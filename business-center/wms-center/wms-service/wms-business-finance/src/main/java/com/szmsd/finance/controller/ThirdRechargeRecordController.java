package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.finance.service.IThirdRechargeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = {"第三方充值"})
@RestController
@RequestMapping("/thirdRecharge")
public class ThirdRechargeRecordController extends BaseController {

    @Resource
    private IThirdRechargeRecordService thirdRechargeRecordService;

    @PreAuthorize("@ss.hasPermi('ThirdRecharge:result')")
    @ApiOperation(value = "线上充值结果")
    @GetMapping("/result/{serialNo}")
    public R<String> preOnlineIncomeResult(@PathVariable String serialNo) {
        return R.ok(thirdRechargeRecordService.getRechargeResult(serialNo));
    }

}
