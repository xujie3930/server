package com.szmsd.finance.factory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.ISysDictDataService;
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
    private IAccountBalanceService accountBalanceService;

    @Resource
    private AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Transactional
    @Override
    public boolean updateBalance(CustPayDTO dto) {
        String key = "cky-test-fss-freeze-balance-all:" + dto.getCusId();
        RLock lock = redissonClient.getLock(key);
        try {
            BalanceDTO balance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
            if (!checkBalance(balance, dto)) {
                return false;
            }
            setBalance(dto.getCusCode(), dto.getCurrencyCode(), balance);
            recordOpLog(dto, balance.getCurrentBalance());
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
        return accountBalanceChange;
    }

    private boolean checkBalance(BalanceDTO balance, CustPayDTO dto) {
        BigDecimal changeAmount = dto.getAmount();
        if (BillEnum.PayMethod.BALANCE_FREEZE == dto.getPayMethod()) {
            List<AccountBalanceChange> accountBalanceChanges = getRecordList(dto);
            if (accountBalanceChanges.size() > 0) {
                log.error("该单已有冻结额，单号： {}", dto.getNo());
                return false;
            }
            balance.setCurrentBalance(balance.getCurrentBalance().subtract(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().add(changeAmount));
            return BigDecimal.ZERO.compareTo(balance.getCurrentBalance()) <= 0;
        }
        if (BillEnum.PayMethod.BALANCE_THAW == dto.getPayMethod()) {
            List<AccountBalanceChange> accountBalanceChanges = getRecordList(dto);
            if (accountBalanceChanges.size() > 0) {
                //查询出此单冻结的金额
                BigDecimal amountChange = accountBalanceChanges.stream().map(AccountBalanceChange::getAmountChange).reduce(BigDecimal.ZERO, BigDecimal::add);
                balance.setCurrentBalance(balance.getCurrentBalance().add(amountChange));
                balance.setFreezeBalance(balance.getFreezeBalance().subtract(amountChange));
                dto.setAmount(amountChange);
                boolean b = BigDecimal.ZERO.compareTo(balance.getFreezeBalance()) <= 0;
                if(b) {
                    setHasFreeze(dto);
                    return true;
                }
                log.error("解冻金额不足 单号: {} 金额：{}",dto.getNo(),amountChange);
            }
            log.error("没有找到该单的冻结额。 单号： {}", dto.getNo());
            return false;
        }
        return false;
    }

    private List<AccountBalanceChange> getRecordList(CustPayDTO dto) {
        LambdaQueryWrapper<AccountBalanceChange> query = Wrappers.lambdaQuery();
        query.eq(AccountBalanceChange::getCurrencyCode, dto.getCurrencyCode());
        query.eq(AccountBalanceChange::getNo, dto.getNo());
        if(StringUtils.isNotBlank(dto.getOrderType())){
            query.eq(AccountBalanceChange::getOrderType,dto.getOrderType());
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
