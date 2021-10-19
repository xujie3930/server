package com.szmsd.finance.factory;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 直接扣费不涉及冻结
 */
@Slf4j
@Component
public class PaymentNoFreezePayFactory extends AbstractPayFactory {

    @Autowired
    SerialNumberClientService serialNumberClientService;

    @Resource
    private RedissonClient redissonClient;

    @Transactional
    @Override
    public boolean updateBalance(CustPayDTO dto) {
        String key = "cky-test-fss-balance-paymentNoFreezePay" + dto.getCurrencyCode() + ":" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                BigDecimal changeAmount = dto.getAmount();
                //余额不足
                /*if (dto.getPayType() == BillEnum.PayType.PAYMENT_NO_FREEZE && oldBalance.getCurrentBalance().compareTo(changeAmount) < 0) {
                    return false;
                }
                BalanceDTO result = calculateBalance(oldBalance, changeAmount);*/
                if (dto.getPayType() == BillEnum.PayType.PAYMENT_NO_FREEZE &&!oldBalance.checkAndSetAmountAndCreditAnd(changeAmount,true,BalanceDTO::pay)){
                    return false;
                }

                setBalance(dto.getCusCode(), dto.getCurrencyCode(), oldBalance,true);
                recordOpLog(dto, oldBalance.getCurrentBalance());
                recordDetailLog(dto, oldBalance);
                setSerialBillLog(dto);

            }
            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        accountBalanceChange.setAmountChange(amount.negate());
    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        // 可用
        oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().subtract(changeAmount));
        // 总余额
        oldBalance.setTotalBalance(oldBalance.getTotalBalance().subtract(changeAmount));
        return oldBalance;
    }

}
