package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
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
    public BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Long qty) {
        return qty == 1 ? firstPrice : new BigDecimal(qty - 1).multiply(nextPrice).add(firstPrice);
    }

    @Override
    public BigDecimal manySkuCalculate(BigDecimal firstPrice, BigDecimal nextPrice, List<DelOutboundOperationDetailVO> details) {
        return details.stream().map(value -> this.calculate(firstPrice , nextPrice,
                value.getQty())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public R pay(CustPayDTO custPayDTO, ChargeLog chargeLog) {
        chargeLog.setCustomCode(custPayDTO.getCusCode());
        chargeLog.setCurrencyCode(custPayDTO.getCurrencyCode());
        chargeLog.setAmount(custPayDTO.getAmount());
        chargeLog.setOperationPayMethod(custPayDTO.getPayMethod().getPaymentName());
        chargeLog.setHasFreeze(false);
        R r = rechargesFeignService.warehouseFeeDeductions(custPayDTO);
        updateAndSave(chargeLog, r);
        return r;
    }

    @Override
    public R freezeBalance(CusFreezeBalanceDTO dto, ChargeLog chargeLog) {
        chargeLog.setCustomCode(dto.getCusCode());
        chargeLog.setCurrencyCode(dto.getCurrencyCode());
        chargeLog.setAmount(dto.getAmount());
        R r = rechargesFeignService.freezeBalance(dto);
        if (r.getCode() != 200)
            log.error("freezeBalance() pay failed.. msg: {},data: {}", r.getMsg(), r.getData());
        chargeLog.setSuccess(r.getCode() == 200);
        chargeLog.setMessage(r.getMsg());
        int insert = chargeLogService.save(chargeLog);
        if (insert < 1) {
            log.error("pay() failed {}", chargeLog);
        }
        return r;
    }

    @Override
    public R thawBalance(CusFreezeBalanceDTO dto, ChargeLog chargeLog) {
        chargeLog.setCustomCode(dto.getCusCode());
        chargeLog.setCurrencyCode(dto.getCurrencyCode());
        chargeLog.setAmount(dto.getAmount());
        chargeLog.setHasFreeze(false);
        R r = rechargesFeignService.thawBalance(dto);
        updateAndSave(chargeLog, r);
        return r;
    }

    private void updateAndSave(ChargeLog chargeLog, R r) {
        if (r.getCode() == 200) {
            chargeLogService.update(chargeLog.getId()); // 解冻 把之前冻结记录的hasFreeze修改为false
        }
        chargeLog.setSuccess(r.getCode() == 200);
        chargeLog.setMessage(r.getMsg());
        int insert = chargeLogService.save(chargeLog);
        if (insert < 1) {
            log.error("pay() failed {}", chargeLog);
        }
    }

}
