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

/**
 * @author liulei
 */
@Slf4j
@Component
public class BalanceFreezeFactory extends AbstractPayFactory {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean updateBalance(CustPayDTO dto){
        String key="cky-test-fss-balance-all:"+dto.getCusId();
        RLock lock=redissonClient.getLock(key);
        try{
            BalanceDTO balance=getBalance(dto.getCusCode(),dto.getCurrencyCode());
            if(!checkBalance(balance,dto)){
                return false;
            }
            setBalance(dto.getCusCode(),dto.getCurrencyCode(),balance);
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

    private boolean checkBalance(BalanceDTO balance,CustPayDTO dto) {
        BigDecimal changeAmount=dto.getAmount();
        if(BillEnum.PayMethod.BALANCE_FREEZE == dto.getPayMethod()){
            balance.setCurrentBalance(balance.getCurrentBalance().subtract(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().add(changeAmount));
            return BigDecimal.ZERO.compareTo(balance.getCurrentBalance())>0?false:true;
        }
        if(BillEnum.PayMethod.BALANCE_THAW == dto.getPayMethod()){
            balance.setCurrentBalance(balance.getCurrentBalance().add(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().subtract(changeAmount));
            return BigDecimal.ZERO.compareTo(balance.getFreezeBalance())>0?false:true;
        }
        return false;
    }

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {

    }

    @Override
    public BigDecimal calculateBalance(BigDecimal oldBalance, BigDecimal changeAmount) {
        return null;
    }
}
