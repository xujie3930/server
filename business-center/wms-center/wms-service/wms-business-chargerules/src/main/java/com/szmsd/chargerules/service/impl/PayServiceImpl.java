package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
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
        if (qty <= 0) return BigDecimal.ZERO;
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
        String code = DelOutboundOrderEnum.getCode(custPayDTO.getOrderType());
        if(code != null) custPayDTO.setOrderType(code);
        if(BigDecimal.ZERO.compareTo(custPayDTO.getAmount()) >= 0) {
            log.info("扣款金额为: {} 不执行操作", custPayDTO.getAmount());
            chargeLog.setSuccess(true);
            chargeLog.setMessage("成功");
            chargeLogService.save(chargeLog);
            return R.ok();
        }
        R r = rechargesFeignService.warehouseFeeDeductions(custPayDTO);
        updateAndSave(chargeLog, r);
        return r;
    }

    @Override
    public R freezeBalance(CusFreezeBalanceDTO dto, ChargeLog chargeLog) {
        chargeLog.setCustomCode(dto.getCusCode());
        chargeLog.setCurrencyCode(dto.getCurrencyCode());
        chargeLog.setAmount(dto.getAmount());
        String code = DelOutboundOrderEnum.getCode(dto.getOrderType());
        if(code != null) dto.setOrderType(code);
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) >= 0) {
            log.info("冻结金额为: {} 不执行操作", dto.getAmount());
            chargeLog.setSuccess(true);
            chargeLog.setMessage("成功");
            chargeLogService.save(chargeLog);
            return R.ok();
        }
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
        String code = DelOutboundOrderEnum.getCode(dto.getOrderType());
        if(code != null) dto.setOrderType(code);
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) >= 0) {
            log.info("解冻金额为: {} 不执行操作", dto.getAmount());
            chargeLog.setSuccess(true);
            chargeLog.setMessage("成功");
            chargeLogService.save(chargeLog);
            return R.ok();
        }
        R r = rechargesFeignService.thawBalance(dto);
        updateAndSave(chargeLog, r);
        return r;
    }

    /**
     * 解冻或者扣款成功后 把之前冻结记录的hasFreeze修改为false
     * @param chargeLog chargeLog
     * @param r result
     */
    private void updateAndSave(ChargeLog chargeLog, R r) {
        if (r.getCode() == 200) {
            chargeLogService.update(chargeLog.getId());
        }
//        chargeLog.setSuccess(r.getCode() == 200);
//        chargeLog.setMessage(r.getMsg());
//        int insert = chargeLogService.save(chargeLog);
//        if (insert < 1) {
//            log.error("pay() failed {}", chargeLog);
//        }
    }

}
