package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.mapper.PayLogMapper;
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
    private PayLogMapper payLogMapper;

    @Override
    public R pay(String customCode, BigDecimal amount) {
        CustPayDTO custPayDTO = new CustPayDTO();
        custPayDTO.setCusCode(customCode);
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.SPECIAL_OPERATE);
        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        custPayDTO.setAmount(amount);

        ChargeLog chargeLog = new ChargeLog();
        chargeLog.setCustomCode(customCode);
        chargeLog.setPayMethod(BillEnum.PayMethod.SPECIAL_OPERATE.getPaymentName());
        chargeLog.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
        chargeLog.setAmount(amount);

        R r = rechargesFeignService.warehouseFeeDeductions(custPayDTO);
        chargeLog.setSuccess(r.getCode() == 200);
        int insert = payLogMapper.insert(chargeLog);
        if(insert < 1) {
            log.error("pay() failed {}",chargeLog);
        }
        return r;
    }

}
