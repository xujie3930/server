package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.enums.HttpRechargeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

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
    public R pay(String customCode, BigDecimal amount, BillEnum.PayMethod payMethod, ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        custPayDTO.setCusCode(customCode);
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(payMethod);
        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        custPayDTO.setAmount(amount);

        chargeLog.setCustomCode(customCode);
        chargeLog.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        chargeLog.setAmount(amount);
        chargeLog.setPayMethod(payMethod.getPaymentName());

        R r = rechargesFeignService.warehouseFeeDeductions(custPayDTO);
        chargeLog.setSuccess(r.getCode() == 200);
        int insert = chargeLogService.save(chargeLog);
        if (insert < 1) {
            log.error("pay() failed {}", chargeLog);
        }
        return r;
    }

}
