package com.szmsd.finance.factory;

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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author liulei
 */
@Slf4j
@Component
public class ExchangePayFactory extends AbstractPayFactory {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean updateBalance(CustPayDTO dto){
        String key="cky-test-fss-balance-all:"+dto.getCusId();
        RLock lock=redissonClient.getLock(key);
        try{
            if(lock.tryLock(time,unit)){
                BigDecimal substractAmount=dto.getAmount();
                //1.先扣款
                BalanceDTO beforeSubtract=getBalance(dto.getCusCode(),dto.getCurrencyCode());
                //先判断扣款余额是否充足
                if(beforeSubtract.getCurrentBalance().compareTo(substractAmount) < 0){
                    return false;
                }
                BalanceDTO afterSubtract=calculateBalance(beforeSubtract,substractAmount.negate());
                setBalance(dto.getCusCode(),dto.getCurrencyCode(),afterSubtract);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
                recordOpLog(dto,afterSubtract.getCurrentBalance());
                //2.再充值
                BalanceDTO beforeAdd=getBalance(dto.getCusCode(),dto.getCurrencyCode2());
                BigDecimal addAmount=dto.getRate().multiply(substractAmount).setScale(2,BigDecimal.ROUND_FLOOR);
                BalanceDTO afterAdd=calculateBalance(beforeAdd,addAmount);
                setBalance(dto.getCusCode(),dto.getCurrencyCode2(),afterAdd);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
                dto.setAmount(addAmount);
                dto.setCurrencyCode(dto.getCurrencyCode2());
                dto.setCurrencyName(dto.getCurrencyName2());
                recordOpLog(dto,afterAdd.getCurrentBalance());
            }
            return true;
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:"+e.getMessage());
        }finally {
            if(lock.isLocked()){
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        if(accountBalanceChange.getPayMethod()==BillEnum.PayMethod.EXCHANGE_PAYMENT){
            accountBalanceChange.setAmountChange(amount.negate());
        }else{
            accountBalanceChange.setAmountChange(amount);
        }
    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().add(changeAmount));
        oldBalance.setTotalBalance(oldBalance.getTotalBalance().add(changeAmount));
        return oldBalance;
    }
}
