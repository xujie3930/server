package com.szmsd.finance.factory;

import cn.hutool.core.date.StopWatch;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountBalanceService;
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
    @Resource
    private IAccountBalanceService iAccountBalanceService;

    @Override
    @Transactional
    public Boolean updateBalance(CustPayDTO dto) {
        log.info("RefundPayFactory {}", JSONObject.toJSONString(dto));
        String key = "cky-test-fss-balance-" + dto.getCurrencyCode() + ":" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        try {
            boolean success = false;
            if (lock.tryLock(time, unit)) {
                log.info("【退费】RefundPayFactory--");
                StopWatch stopWatch = new StopWatch("退费");
                stopWatch.start("查询余额");
                BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                stopWatch.stop();
                stopWatch.start("业务逻辑");
                BigDecimal changeAmount = dto.getAmount();
                if (changeAmount.compareTo(BigDecimal.ZERO) >= 0) {
                    // 充值
                    success = oldBalance.rechargeAndSetAmount(changeAmount);
                    super.addForCreditBillAsync(oldBalance.getCreditInfoBO().getRepaymentAmount(), dto.getCusCode(), dto.getCurrencyCode());
                } else {
                    // 退费强制扣钱
                    success = oldBalance.payAnyWay(changeAmount.negate());
                }
                stopWatch.stop();
                if (!success) return false;
                BalanceDTO result = oldBalance;
                stopWatch.start("更新余额");
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), result, true);
                stopWatch.stop();
                stopWatch.start("记录日志");
                recordOpLogAsync(dto, result.getCurrentBalance());
                recordDetailLogAsync(dto, oldBalance);
                setSerialBillLog(dto);
                //iAccountBalanceService.reloadCreditTime(Arrays.asList(dto.getCusCode()), dto.getCurrencyCode());
                log.info("处理退费完成:{}", stopWatch.prettyPrint());
                return true;
            } else {
                log.error("退费业务处理超时,请稍候重试{}", JSONObject.toJSONString(dto));
                throw new RuntimeException("退费业务处理超时,请稍候重试");
            }
        } catch (InterruptedException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("RefundPayFactory异常:", e);
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
            throw new RuntimeException("退费业务处理超时,请稍候重试!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("【退费】RefundPayFactory 解锁结束--");
            }
            log.info("【退费】RefundPayFactory --结束--");
        }
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
        financeThreadTaskPool.execute(() -> {
            log.info("setSerialBillLog {}", JSONObject.toJSONString(dto));
            List<AccountSerialBillDTO> serialBillInfoList = dto.getSerialBillInfoList();
            AccountSerialBillDTO serialBill = serialBillInfoList.get(0);
            serialBill.setNo(dto.getNo());
            serialBill.setRemark(dto.getRemark());
            serialBill.setWarehouseCode(dto.getWarehouseCode());
            serialBill.setWarehouseName(dto.getWarehouseName());
            serialBill.setPaymentTime(new Date());
            AccountSerialBill accountSerialBill = new AccountSerialBill();
            BeanUtils.copyProperties(serialBill, accountSerialBill);
            accountSerialBillService.save(accountSerialBill);
            //accountSerialBillService.add(serialBill);
        });
    }

}
