package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PayServiceImpl implements IPayService {

    @Resource
    private RechargesFeignService rechargesFeignService;

    @Resource
    private IChargeLogService chargeLogService;

    @Override
    public BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Integer qty) {
        return qty == 1 ? firstPrice : new BigDecimal(qty - 1).multiply(nextPrice).add(firstPrice);
    }

    @Override
    public BigDecimal manySkuCalculate(BigDecimal firstPrice, BigDecimal nextPrice, List<DelOutboundDetailDto> delOutboundDetailList) {
        return delOutboundDetailList.stream().map(value -> this.calculate(firstPrice , nextPrice,
                value.getQty().intValue())).reduce(BigDecimal::add).get();
    }

    @Override
    public R pay(CustPayDTO custPayDTO, ChargeLog chargeLog) {
        chargeLog.setCustomCode(custPayDTO.getCusCode());
        chargeLog.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        chargeLog.setAmount(custPayDTO.getAmount());
        chargeLog.setPayMethod(custPayDTO.getPayMethod().getPaymentName());
        R r = rechargesFeignService.warehouseFeeDeductions(custPayDTO);
        chargeLog.setSuccess(r.getCode() == 200);
        chargeLog.setMessage(r.getMsg());
        int insert = chargeLogService.save(chargeLog);
        if (insert < 1) {
            log.error("pay() failed {}", chargeLog);
        }
        return r;
    }

}
