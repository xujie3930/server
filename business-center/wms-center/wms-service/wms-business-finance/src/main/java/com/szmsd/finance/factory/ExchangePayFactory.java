package com.szmsd.finance.factory;

import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private RedissonClient redissonClient;

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Transactional
    @Override
    public boolean updateBalance(CustPayDTO dto) {
        String key = "cky-test-fss-balance-all:" + dto.getCusId();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                BigDecimal substractAmount = dto.getAmount();
                //1.先扣款
                BalanceDTO beforeSubtract = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                //先判断扣款余额是否充足
                if (beforeSubtract.getCurrentBalance().compareTo(substractAmount) < 0) {
                    return false;
                }
                BalanceDTO afterSubtract = calculateBalance(beforeSubtract, substractAmount.negate());
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), afterSubtract);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
                AccountBalanceChange accountBalanceChange = recordOpLog(dto, afterSubtract.getCurrentBalance());
                //2.再充值
                BalanceDTO beforeAdd = getBalance(dto.getCusCode(), dto.getCurrencyCode2());
                BigDecimal addAmount = dto.getRate().multiply(substractAmount).setScale(2, BigDecimal.ROUND_FLOOR);
                BalanceDTO afterAdd = calculateBalance(beforeAdd, addAmount);
                setBalance(dto.getCusCode(), dto.getCurrencyCode2(), afterAdd);
                setSerialBillLog(dto,accountBalanceChange);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
                dto.setAmount(addAmount);
                dto.setCurrencyCode(dto.getCurrencyCode2());
                dto.setCurrencyName(dto.getCurrencyName2());
                AccountBalanceChange afterBalanceChange = recordOpLog(dto, afterAdd.getCurrentBalance());
                //设置流水账单
                dto.setCurrencyCode(accountBalanceChange.getCurrencyCode());
                dto.setCurrencyName(accountBalanceChange.getCurrencyName());
                setSerialBillLog(dto,afterBalanceChange);
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

    public void setSerialBillLog(CustPayDTO dto,AccountBalanceChange accountBalanceChange) {
        AccountSerialBillDTO serialBill = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
        serialBill.setCurrencyCode(accountBalanceChange.getCurrencyCode());
        serialBill.setCurrencyName(accountBalanceChange.getCurrencyName());
        serialBill.setAmount(accountBalanceChange.getAmountChange());
        serialBill.setChargeCategory(dto.getCurrencyName().concat("转").concat(dto.getCurrencyName2()));
        serialBill.setChargeType(dto.getPayMethod().getPaymentName());
        serialBill.setBusinessCategory(BillEnum.PayMethod.BALANCE_EXCHANGE.getPaymentName());
        serialBill.setProductCategory(serialBill.getBusinessCategory());
        serialBill.setRemark("汇率为: ".concat(dto.getRate().toString()));
        serialBill.setNo(accountBalanceChange.getSerialNum());
        accountSerialBillService.add(serialBill);
    }

}
