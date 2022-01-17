package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.util.LogUtil;
import com.szmsd.finance.util.SnowflakeId;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 冻结
 */
@Slf4j
@Component
public class BalanceFreezeFactory extends AbstractPayFactory {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Transactional
    @Override
    public Boolean updateBalance(CustPayDTO dto) {
        log.info(LogUtil.format(dto, "冻结/解冻"));
        String key = "cky-fss-freeze-balance-all:" + dto.getCusId();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                log.info("【updateBalance】 1");
                BalanceDTO balance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                log.info("【updateBalance】 2");
                Boolean checkFlag = checkAndSetBalance(balance, dto);
                log.info("【updateBalance】 3");
                if (checkFlag == null) return null;
                if (!checkFlag) {
                    return false;
                }
                log.info("【updateBalance】 4");
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), balance);
                log.info("【updateBalance】 5");
                recordOpLogAsync(dto, balance.getCurrentBalance());
                recordDetailLogAsync(dto, balance);
                log.info("【updateBalance】 6");
                return true;
            } else {
                log.error("冻结/解冻操作超时,请稍候重试{}", JSONObject.toJSONString(dto));
                throw new RuntimeException("冻结/解冻操作超时,请稍候重试");
            }
        } catch (InterruptedException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("BalanceFreezeFactory异常：", e);
            log.error("获取余额异常，加锁失败");
            log.error("异常信息:" + e.getMessage());
            throw new RuntimeException("冻结/解冻操作超时,请稍候重试!");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public AccountBalanceChange recordOpLog(CustPayDTO dto, BigDecimal result) {
        AccountBalanceChange accountBalanceChange = new AccountBalanceChange();
        BeanUtils.copyProperties(dto, accountBalanceChange);
        if (StringUtils.isEmpty(accountBalanceChange.getCurrencyName())) {
            String currencyName = sysDictDataService.getCurrencyNameByCode(accountBalanceChange.getCurrencyCode());
            accountBalanceChange.setCurrencyName(currencyName);
        }
        if (BillEnum.PayMethod.BALANCE_FREEZE == accountBalanceChange.getPayMethod()) {
            accountBalanceChange.setHasFreeze(true);
        }
        accountBalanceChange.setSerialNum(SnowflakeId.getNextId12());
        setOpLogAmount(accountBalanceChange, dto.getAmount());
        accountBalanceChange.setCurrentBalance(result);
        accountBalanceChangeMapper.insert(accountBalanceChange);
        log.info("recordOpLog= {}   === {}", JSONObject.toJSONString(dto), JSONObject.toJSONString(accountBalanceChange));
        return accountBalanceChange;
    }

    /**
     * 校验越是否够扣除，不够使用授信额，再不够返回false
     *
     * @param balance
     * @param dto
     * @return
     */
    private Boolean checkAndSetBalance(BalanceDTO balance, CustPayDTO dto) {
        BigDecimal changeAmount = dto.getAmount();
        if (BillEnum.PayMethod.BALANCE_FREEZE == dto.getPayMethod()) {
            List<AccountBalanceChange> accountBalanceChanges = getRecordList(dto);
            if (accountBalanceChanges.size() > 0) {
                log.info("该单已存在冻结额，单号:{}", dto.getNo());
                // throw new CommonException("999", "该单已存在冻结额，单号：" + dto.getNo());
                return null;
            }
            /*balance.setCurrentBalance(balance.getCurrentBalance().subtract(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().add(changeAmount));
            boolean result = BigDecimal.ZERO.compareTo(balance.getCurrentBalance()) <= 0;
            if(!result) {
                throw new CommonException("999", "可用余额不足以冻结，费用：" + changeAmount);
            }
            return true;*/
            if (DelOutboundOrderEnum.FREEZE_IN_STORAGE.getCode().equals(dto.getOrderType())) {
                log.info("入库冻结没钱也可以扣--扣除{}", changeAmount);
                balance.freeze(changeAmount);
            } else {
                if (!balance.checkAndSetAmountAndCreditAnd(changeAmount, false, BalanceDTO::freeze)) {
                    throw new RuntimeException("可用余额不足以冻结，费用：" + changeAmount);
                }
                return true;
            }

        }
        if (BillEnum.PayMethod.BALANCE_THAW == dto.getPayMethod()) {
            log.info("解冻参数：{}", JSONObject.toJSONString(dto));
            List<AccountBalanceChange> accountBalanceChanges = getRecordList(dto);
            if (accountBalanceChanges.size() > 0) {
                //查询出此单冻结的金额
                BigDecimal amountChange = accountBalanceChanges.stream().map(AccountBalanceChange::getAmountChange).reduce(BigDecimal.ZERO, BigDecimal::add);
                balance.setCurrentBalance(balance.getCurrentBalance().add(amountChange));
                balance.setFreezeBalance(balance.getFreezeBalance().subtract(amountChange));
                dto.setAmount(amountChange);
                boolean b = BigDecimal.ZERO.compareTo(balance.getFreezeBalance()) <= 0;
                if (b) {
                    setHasFreeze(dto);
                    return true;
                }
                throw new CommonException("999", "解冻金额不足 单号: " + dto.getNo() + " 金额：" + amountChange);
            }
        }
        return null;
    }

    private List<AccountBalanceChange> getRecordList(CustPayDTO dto) {
        log.info("getRecordList：{}", JSONObject.toJSONString(dto));
        LambdaQueryWrapper<AccountBalanceChange> query = Wrappers.lambdaQuery();
        query.eq(AccountBalanceChange::getCurrencyCode, dto.getCurrencyCode());
        query.eq(AccountBalanceChange::getNo, dto.getNo());
        if (StringUtils.isNotBlank(dto.getOrderType())) {
            query.eq(AccountBalanceChange::getOrderType, dto.getOrderType());
        }
        query.eq(AccountBalanceChange::getPayMethod, BillEnum.PayMethod.BALANCE_FREEZE);
        query.eq(AccountBalanceChange::getHasFreeze, true);
        return accountBalanceChangeMapper.recordListPage(query);
    }

    @Override
    protected void setOpLogAmount(AccountBalanceChange accountBalanceChange, BigDecimal amount) {
        accountBalanceChange.setAmountChange(amount);
    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        return null;
    }

}
