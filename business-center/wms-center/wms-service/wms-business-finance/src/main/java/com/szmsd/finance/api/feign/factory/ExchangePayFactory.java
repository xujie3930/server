package com.szmsd.finance.api.feign.factory;

import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.api.feign.factory.abstractFactory.AbstractPayFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author liulei
 */
@Slf4j
@Component
public class ExchangePayFactory extends AbstractPayFactory {

    public static final TimeUnit unit=TimeUnit.MILLISECONDS;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean updateBalance(CustPayDTO dto){
        boolean flag=true;
        String key="cky-test-fss-balance-all:"+dto.getCusId();
        RLock lock=redissonClient.getLock(key);
        try{
            if(lock.tryLock(time,unit)){
                //1.先扣款
                BigDecimal oldBalance1=getCurrentBalance(dto.getCusCode(),dto.getCurrencyCode());
                BigDecimal substractAmount=dto.getAmount();
                //先判断扣款余额是否充足
                if(oldBalance1.compareTo(substractAmount) < 0){
                    return false;
                }
                BigDecimal result1=oldBalance1.subtract(substractAmount);
                setCurrentBalance(dto.getCusCode(),dto.getCurrencyCode(),result1);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
                recordOpLog(dto,result1);
                //2.再充值
                BigDecimal oldBalance2=getCurrentBalance(dto.getCusCode(),dto.getCurrencyCode2());
                BigDecimal addAmount=dto.getRate().multiply(substractAmount).setScale(2,BigDecimal.ROUND_FLOOR);
                BigDecimal result2=oldBalance2.add(addAmount);
                setCurrentBalance(dto.getCusCode(),dto.getCurrencyCode(),result2);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
                recordOpLog(dto,result2);

            }
        }catch(Exception e){
            log.info("获取余额异常，加锁失败");
        }finally {
            if(lock.isLocked()){
                lock.unlock();
            }
        }
        return flag;
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
    public BigDecimal calculateBalance(BigDecimal oldBalance, BigDecimal changeAmount) {
        return null;
    }
}
