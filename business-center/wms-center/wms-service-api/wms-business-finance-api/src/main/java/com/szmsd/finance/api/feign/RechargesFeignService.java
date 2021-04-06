package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.factory.RechargeFeignFallback;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import com.szmsd.finance.enums.BusinessFssInterface;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
