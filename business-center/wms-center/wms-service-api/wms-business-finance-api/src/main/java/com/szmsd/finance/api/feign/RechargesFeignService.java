package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.factory.RechargeFeignFallback;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BusinessFssInterface;
import com.szmsd.finance.vo.UserCreditInfoVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liulei
 */
@FeignClient(contextId = "FeignClient.RechargesFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = RechargeFeignFallback.class)
public interface RechargesFeignService {

    @ApiOperation(value = "第三方充值接口回调")
    @PostMapping("/accountBalance/rechargeCallback")
    R rechargeCallback(@RequestBody RechargesCallbackRequestDTO requestDTO);

    @ApiOperation(value = "仓储费用扣除")
    @PostMapping("/accountBalance/warehouseFeeDeduct")
    R warehouseFeeDeductions(@RequestBody CustPayDTO dto);

    @ApiOperation(value = "费用扣除")
    @PostMapping("/accountBalance/feeDeductions")
    R feeDeductions(@RequestBody CustPayDTO dto);

    @ApiOperation(value = "冻结余额")
    @PostMapping("/accountBalance/freezeBalance")
    public R freezeBalance(@RequestBody CusFreezeBalanceDTO dto);

    @ApiOperation(value = "解冻余额")
    @PostMapping("/accountBalance/thawBalance")
    public R thawBalance(@RequestBody CusFreezeBalanceDTO dto);

    @ApiOperation(value = "查询账户余额信息")
    @PostMapping("/accountBalance/list")
    R<List<AccountBalance>> accountList(@RequestBody AccountBalanceDTO dto);

    @ApiOperation(value = "修改用户信用额信息")
    @PostMapping("/accountBalance/updateUserCredit")
    R updateUserCredit(@Validated @RequestBody UserCreditDTO userCreditDTO);

    @ApiOperation(value = "查询用户信用额信息")
    @GetMapping("/accountBalance/queryUserCredit/{cusCode}")
    R<UserCreditInfoVO> queryUserCredit(@PathVariable("cusCode") String cusCode);

    @ApiOperation(value = "在线充值")
    @PostMapping("/accountBalance/onlineIncome")
    R onlineIncome(@RequestBody CustPayDTO dto);
}
