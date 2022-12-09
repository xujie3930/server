package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 汇率转换
 */
@Slf4j
@Component
public class ExchangePayFactory extends AbstractPayFactory {

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Transactional
    @Override
    public Boolean updateBalance(final CustPayDTO dto) {
        log.info("ExchangePayFactory {}", JSONObject.toJSONString(dto));
        try {
            BigDecimal substractAmount = dto.getAmount();
            String currencyCode = dto.getCurrencyCode();
            String currencyCodeTo = dto.getCurrencyCode2();
            String cusCode = dto.getCusCode();
            //1.先扣款
            BalanceDTO beforeSubtract = getBalance(cusCode, currencyCode);

            log.info("币别转换当前剩余，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCode,beforeSubtract.getTotalBalance(),beforeSubtract.getCurrentBalance(),beforeSubtract.getFreezeBalance(),beforeSubtract.getCreditUseAmount());
            //先判断扣款余额是否充足
            if (beforeSubtract.getCurrentBalance().compareTo(substractAmount) < 0) {
                return false;
            }

            String chargeType = dto.getCurrencyName() + "转" + dto.getCurrencyName2();
            String chargeType2 = dto.getCurrencyName2() + "转" + dto.getCurrencyName();

            BalanceDTO afterSubtract = calculateBalance(beforeSubtract, substractAmount.negate());
            log.info("币别转换calculateBalance剩余，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCode,beforeSubtract.getTotalBalance(),beforeSubtract.getCurrentBalance(),beforeSubtract.getFreezeBalance(),beforeSubtract.getCreditUseAmount());
            setBalance(cusCode, currencyCode, afterSubtract);

            log.info("币别转换setBalance 1 更新转换，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCode,beforeSubtract.getTotalBalance(),beforeSubtract.getCurrentBalance(),beforeSubtract.getFreezeBalance(),beforeSubtract.getCreditUseAmount());
            dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
            AccountBalanceChange accountBalanceChange = recordOpLog(dto, afterSubtract.getCurrentBalance());
            //2.再充值
            BalanceDTO beforeAdd = getBalance(cusCode, currencyCodeTo);

            log.info("币别转换获取转换后，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCodeTo,beforeAdd.getTotalBalance(),beforeAdd.getCurrentBalance(),beforeAdd.getFreezeBalance(),beforeAdd.getCreditUseAmount());

            BigDecimal addAmount = dto.getRate().multiply(substractAmount).setScale(2, BigDecimal.ROUND_FLOOR);
            // BalanceDTO afterAdd = calculateBalance(beforeAdd, addAmount);
            // 计算还款额，并销账（还账单）
            beforeAdd.rechargeAndSetAmount(addAmount);
            log.info("rechargeAndSetAmount  {}",addAmount);
            BigDecimal repaymentAmount = beforeAdd.getCreditInfoBO().getRepaymentAmount();
            log.info("repaymentAmount  {}",addAmount);
            super.addForCreditBillAsync(repaymentAmount, cusCode, currencyCodeTo);

            log.info("币别转换计算还款额，并销账后，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCodeTo,beforeAdd.getTotalBalance(),beforeAdd.getCurrentBalance(),beforeAdd.getFreezeBalance(),beforeAdd.getCreditUseAmount());

            setBalance(cusCode, currencyCodeTo, beforeAdd,true);

            log.info("币别转换setBalance 2 更新转换后，币别：{},总余额：{},当前余额：{},冻结余额：{},授信额度:{}",currencyCodeTo,beforeAdd.getTotalBalance(),beforeAdd.getCurrentBalance(),beforeAdd.getFreezeBalance(),beforeAdd.getCreditUseAmount());
            dto.setChargeCategoryChange(chargeType);
            setSerialBillLogAsync(dto, accountBalanceChange);
            dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
            dto.setAmount(addAmount);
            dto.setCurrencyCode(currencyCodeTo);
            dto.setCurrencyName(dto.getCurrencyName2());
            AccountBalanceChange afterBalanceChange = recordOpLog(dto, beforeAdd.getCurrentBalance());
            //设置流水账单
            dto.setCurrencyCode(accountBalanceChange.getCurrencyCode());
            dto.setCurrencyName(accountBalanceChange.getCurrencyName());
            dto.setChargeCategoryChange(chargeType2);
            setSerialBillLogAsync(dto, afterBalanceChange);
            recordDetailLogAsync(dto, beforeSubtract);

            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("ExchangePayFactory异常:", e);
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
            throw new RuntimeException("汇率转换,请稍候重试!");
        } finally {
        }
    }

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        if (accountBalanceChange.getPayMethod() == BillEnum.PayMethod.EXCHANGE_PAYMENT) {
            accountBalanceChange.setAmountChange(amount.negate());
        } else {
            accountBalanceChange.setAmountChange(amount);
        }
    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().add(changeAmount));
        oldBalance.setTotalBalance(oldBalance.getTotalBalance().add(changeAmount));
        return oldBalance;
    }

    public void setSerialBillLogAsync(CustPayDTO dto, AccountBalanceChange accountBalanceChange) {
        financeThreadTaskPool.execute(() -> {
            AccountSerialBillDTO serialBill = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
            serialBill.setCurrencyCode(accountBalanceChange.getCurrencyCode());
            serialBill.setCurrencyName(accountBalanceChange.getCurrencyName());
            serialBill.setAmount(accountBalanceChange.getAmountChange());
            serialBill.setChargeCategory(dto.getChargeCategoryChange());
            serialBill.setChargeType(dto.getPayMethod().getPaymentName());
            serialBill.setBusinessCategory(BillEnum.PayMethod.BALANCE_EXCHANGE.getPaymentName());
            serialBill.setProductCategory(serialBill.getProductCategory());
            serialBill.setRemark("汇率为: ".concat(dto.getRate().toString()));
            serialBill.setNo(accountBalanceChange.getSerialNum());

            BigDecimal amountChange = accountBalanceChange.getAmountChange();
            //小于0算支出
            if(amountChange != null && amountChange.compareTo(BigDecimal.ZERO)  == -1){
                serialBill.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
            }else{
                serialBill.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
            }

            accountSerialBillService.add(serialBill);
        });
    }

}
