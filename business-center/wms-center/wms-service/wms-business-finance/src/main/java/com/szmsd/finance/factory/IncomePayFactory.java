package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.IDeductionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

/**
 * 充值
 */
@Slf4j
@Component
public class IncomePayFactory extends AbstractPayFactory {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IAccountSerialBillService accountSerialBillService;
    @Resource
    private IAccountBalanceService iAccountBalanceService;
    @Transactional
    public boolean updateBalance(CustPayDTO dto) {
        log.info("updateBalance {}", JSONObject.toJSONString(dto));
        String key = "cky-test-fss-balance-" + dto.getCurrencyCode() + ":" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                BigDecimal changeAmount = dto.getAmount();
                //余额不足
                if (dto.getPayType() == BillEnum.PayType.PAYMENT && oldBalance.getCurrentBalance().compareTo(changeAmount) < 0) {
                    return false;
                }
                // BalanceDTO result = calculateBalance(oldBalance, changeAmount);
                oldBalance.rechargeAndSetAmount(changeAmount);
                super.addForCreditBill(oldBalance.getCreditInfoBO().getRepaymentAmount(), dto.getCusCode(), dto.getCurrencyCode());
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), oldBalance,true);
                recordOpLog(dto, oldBalance.getCurrentBalance());
                setSerialBillLog(dto);
                recordDetailLog(dto, oldBalance);
                iAccountBalanceService.reloadCreditTime(Arrays.asList(dto.getCusCode()), dto.getCurrencyCode());
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

    public void setSerialBillLog(CustPayDTO dto) {
        log.info("setSerialBillLog {}", JSONObject.toJSONString(dto));
        AccountSerialBillDTO serialBill = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
        serialBill.setNo(dto.getNo());
        serialBill.setChargeCategory(dto.getPayMethod().getPaymentName());
        serialBill.setChargeType(dto.getPayMethod().getPaymentName());
        serialBill.setProductCategory("充值成功");
        serialBill.setRemark(dto.getRemark());
        serialBill.setPaymentTime(new Date());
        accountSerialBillService.add(serialBill);
    }

}
