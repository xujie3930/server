package com.szmsd.finance.factory.abstractFactory;

import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.util.SnowflakeId;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author liulei
 */
@Slf4j
public abstract class AbstractPayFactory {

    public static final TimeUnit unit=TimeUnit.MILLISECONDS;

    public static final long time=1000l;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    IAccountBalanceService accountBalanceService;

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Transactional
    public boolean updateBalance(CustPayDTO dto){
        String key="cky-test-fss-balance-"+dto.getCurrencyCode()+":"+dto.getCusCode();
        RLock lock=redissonClient.getLock(key);
        try{
            if(lock.tryLock(time,unit)){
                BalanceDTO oldBalance=getBalance(dto.getCusCode(),dto.getCurrencyCode());
                BigDecimal changeAmount=dto.getAmount();
                //余额不足
                if(dto.getPayType() == BillEnum.PayType.PAYMENT && oldBalance.getCurrentBalance().compareTo(changeAmount) < 0){
                    return false;
                }
                BalanceDTO result=calculateBalance(oldBalance,changeAmount);
                setBalance(dto.getCusCode(),dto.getCurrencyCode(),result);
                recordOpLog(dto,result.getCurrentBalance());
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

    public void recordOpLog(CustPayDTO dto,BigDecimal result){
        AccountBalanceChange accountBalanceChange=new AccountBalanceChange();
        BeanUtils.copyProperties(dto,accountBalanceChange);
        accountBalanceChange.setSerialNum(SnowflakeId.getNextId12());
        setOpLogAmount(accountBalanceChange,dto.getAmount());
        accountBalanceChange.setCurrentBalance(result);
        accountBalanceChangeMapper.insert(accountBalanceChange);
    }

    protected abstract void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount);

    protected BigDecimal getCurrentBalance(String cusCode,String currencyCode){
        return accountBalanceService.getCurrentBalance(cusCode,currencyCode);
    }

    protected void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result){
        accountBalanceService.setCurrentBalance(cusCode,currencyCode,result);
    }

    protected BalanceDTO getBalance(String cusCode, String currencyCode){
        return accountBalanceService.getBalance(cusCode,currencyCode);
    }

    protected void setBalance(String cusCode, String currencyCode, BalanceDTO result){
        accountBalanceService.setBalance(cusCode,currencyCode,result);
    }

    public abstract BalanceDTO calculateBalance(BalanceDTO oldBalance,BigDecimal changeAmount);

}
