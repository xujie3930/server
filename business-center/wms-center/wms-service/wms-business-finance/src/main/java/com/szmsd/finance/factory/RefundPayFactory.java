package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 退费
 */
@Slf4j
@Component
public class RefundPayFactory extends AbstractPayFactory {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IAccountSerialBillService accountSerialBillService;
    @Override
    @Transactional
    public boolean updateBalance(CustPayDTO dto) {
        log.info("updateBalance {}", JSONObject.toJSONString(dto));
        String key = "cky-test-fss-balance-" + dto.getCurrencyCode() + ":" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                BigDecimal changeAmount = dto.getAmount();
                if (changeAmount.compareTo(BigDecimal.ZERO)>=0){
                    oldBalance.rechargeAndSetAmount(changeAmount);
                } else {
                    oldBalance.pay(changeAmount.negate());
                }
                BalanceDTO result = oldBalance;
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), result,true);
                recordOpLog(dto, result.getCurrentBalance());
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
        accountBalanceChange.setAmountChange(amount);
    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().add(changeAmount));
        oldBalance.setTotalBalance(oldBalance.getTotalBalance().add(changeAmount));
        return oldBalance;
    }
    @Override
    public void setSerialBillLog(CustPayDTO dto) {
        log.info("setSerialBillLog {}", JSONObject.toJSONString(dto));
        List<AccountSerialBillDTO> serialBillInfoList = dto.getSerialBillInfoList();
        AccountSerialBillDTO serialBill = serialBillInfoList.get(0);
        serialBill.setNo(dto.getNo());
        serialBill.setRemark(dto.getRemark());
        serialBill.setPaymentTime(new Date());
        AccountSerialBill accountSerialBill = new AccountSerialBill();
        BeanUtils.copyProperties(serialBill,accountSerialBill);
        accountSerialBillService.save(accountSerialBill);
        //accountSerialBillService.add(serialBill);
    }

}
