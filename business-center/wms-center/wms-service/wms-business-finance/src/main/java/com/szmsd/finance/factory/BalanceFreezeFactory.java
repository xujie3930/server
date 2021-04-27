package com.szmsd.finance.factory;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
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
        String key = "cky-test-fss-balance-all:" + dto.getCusId();
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

    public void recordOpLog(CustPayDTO dto, BigDecimal result) {
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
    }

    private boolean checkBalance(BalanceDTO balance, CustPayDTO dto) {
        BigDecimal changeAmount = dto.getAmount();
        if (BillEnum.PayMethod.BALANCE_FREEZE == dto.getPayMethod()) {
            balance.setCurrentBalance(balance.getCurrentBalance().subtract(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().add(changeAmount));
            return BigDecimal.ZERO.compareTo(balance.getCurrentBalance()) > 0 ? false : true;
        }
        if (BillEnum.PayMethod.BALANCE_THAW == dto.getPayMethod()) {
            balance.setCurrentBalance(balance.getCurrentBalance().add(changeAmount));
            balance.setFreezeBalance(balance.getFreezeBalance().subtract(changeAmount));
            AccountBalanceChangeDTO map = BeanMapperUtil.map(dto, AccountBalanceChangeDTO.class);
            map.setHasFreeze(false);
            map.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE); //修改冻结的单
            accountBalanceService.updateAccountBalanceChange(map);
            return BigDecimal.ZERO.compareTo(balance.getFreezeBalance()) > 0 ? false : true;
        }
        return false;
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
