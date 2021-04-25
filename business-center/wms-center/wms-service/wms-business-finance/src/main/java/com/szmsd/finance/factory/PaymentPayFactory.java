package com.szmsd.finance.factory;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 付款
 */
@Slf4j
@Component
public class PaymentPayFactory extends AbstractPayFactory {

    @Autowired
    SerialNumberClientService serialNumberClientService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IAccountBalanceService accountBalanceService;

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Transactional
    @Override
    public boolean updateBalance(CustPayDTO dto) {
        String key = "cky-test-fss-balance-paymentPay" + dto.getCurrencyCode() + ":" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock(time, unit)) {
                BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                BigDecimal changeAmount = dto.getAmount();

                Map<String, BigDecimal> balanceChange = getBalanceChange(dto);

                BigDecimal freeze = balanceChange.get(BillEnum.PayMethod.BALANCE_FREEZE.name());

                if (Objects.isNull(freeze)) { // 此单没有冻结金额 直接扣除余额
                    log.info("no freeze, customCode: {}, getCurrencyCode: {}, no: {}", dto.getCusCode(), dto.getCurrencyCode(), dto.getNo());
                    //余额不足
                    if (oldBalance.getCurrentBalance().compareTo(changeAmount) < 0) {
                        return false;
                    }
                    this.calculateBalance(oldBalance, changeAmount);
                }

                if (Objects.nonNull(freeze)) {// 此单有冻结金额 扣除冻结金额
                    log.info("freeze, customCode: {}, getCurrencyCode: {}, no: {}", dto.getCusCode(), dto.getCurrencyCode(), dto.getNo());
                    BigDecimal thaw = balanceChange.get(BillEnum.PayMethod.BALANCE_THAW.name());
                    BigDecimal freezeTotal = freeze;
                    if (Objects.nonNull(thaw)) {
                        log.info("no thaw, customCode: {}, getCurrencyCode: {}, no: {}", dto.getCusCode(), dto.getCurrencyCode(), dto.getNo());
                        freezeTotal = freeze.subtract(thaw);
                    }
                    if (!calculateBalance(oldBalance, changeAmount, freezeTotal)) return false;
                }
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), oldBalance);
                recordOpLog(dto, oldBalance.getCurrentBalance());
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
        accountBalanceChange.setAmountChange(amount.negate());

    }

    @Override
    public BalanceDTO calculateBalance(BalanceDTO oldBalance, BigDecimal changeAmount) {
        // 可用
        oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().subtract(changeAmount));
        // 总余额
        oldBalance.setTotalBalance(oldBalance.getTotalBalance().subtract(changeAmount));
        return oldBalance;
    }

    /**
     * 按条件获取余额变动表
     *
     * @param custPayDTO custPayDTO
     * @return k:支付类型 v:该单支付类型金额总和
     */
    private Map<String, BigDecimal> getBalanceChange(CustPayDTO custPayDTO) {
        AccountBalanceChangeDTO dto = new AccountBalanceChangeDTO();
        dto.setCurrencyCode(custPayDTO.getCurrencyCode());
        dto.setCusCode(custPayDTO.getCusCode());
        dto.setNo(custPayDTO.getNo());
        List<AccountBalanceChange> accountBalanceChanges = accountBalanceService.recordListPage(dto);
        log.info("accountBalanceChanges() no: {}, size: {}", dto.getNo(), accountBalanceChanges.size());
        return accountBalanceChanges
                .stream().collect(Collectors.groupingBy(v -> v.getPayMethod().name())).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue()
                        .stream().map(AccountBalanceChange::getAmountChange).reduce(BigDecimal.ZERO, BigDecimal::add)));
    }

    /**
     * @param oldBalance  当前数据库账号金额详情
     * @param amount      实际费用
     * @param freezeTotal 此单的冻结总额
     * @return result
     */
    private boolean calculateBalance(BalanceDTO oldBalance, BigDecimal amount, BigDecimal freezeTotal) {
        int compare = amount.compareTo(freezeTotal);
        if (compare == 0) { // 实际费用等于冻结额
            // 冻结金额扣除 总金额扣除
            oldBalance.setFreezeBalance(oldBalance.getFreezeBalance().subtract(amount));
            oldBalance.setTotalBalance(oldBalance.getTotalBalance().subtract(amount));
        }
        if (compare < 0) { // 实际费用小于冻结额 费用从冻结额扣除 并将冻结额还原到可用余额
            BigDecimal difference = freezeTotal.subtract(amount);
            oldBalance.setFreezeBalance(oldBalance.getFreezeBalance().subtract(amount).subtract(difference)); //冻结金额 - 实际产生费用 - 此单多冻结的费用
            oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().add(difference)); //此单多冻结的费用加到可用余额
            oldBalance.setTotalBalance(oldBalance.getTotalBalance().subtract(amount)); //总金额减去实际产生费用
        }
        if (compare > 0) { // 实际费用大于冻结额 费用从冻结额扣除 不够的部分从可用余额继续扣除
            BigDecimal difference = amount.subtract(freezeTotal);
            oldBalance.setFreezeBalance(oldBalance.getFreezeBalance().subtract(freezeTotal)); //扣掉这个单的全部冻结额
            // 余额也不够扣
            if (oldBalance.getCurrentBalance().compareTo(difference) < 0) {
                return false;
            }
            oldBalance.setCurrentBalance(oldBalance.getCurrentBalance().subtract(difference)); // 可用余额扣除冻结金额不够扣的部分
            oldBalance.setTotalBalance(oldBalance.getTotalBalance().subtract(amount)); //总金额扣掉实际产生费用
        }
        return true;
    }

    public void setSerialBillLog(CustPayDTO dto) {
        if (CollectionUtils.isEmpty(dto.getSerialBillInfoList())) {
            log.info("setSerialBillLog() list is empty :{} ", dto);
            AccountSerialBillDTO accountSerialBillDTO = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
            accountSerialBillService.add(accountSerialBillDTO);
            return;
        }
        List<AccountSerialBillDTO> collect = dto.getSerialBillInfoList()
                .stream().map(value -> new AccountSerialBillDTO(dto, value)).collect(Collectors.toList());
        accountSerialBillService.saveBatch(collect);
    }

}
