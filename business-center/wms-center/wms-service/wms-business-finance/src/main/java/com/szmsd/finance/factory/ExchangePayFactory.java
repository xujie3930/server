package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
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
    private IAccountSerialBillService accountSerialBillService;

    @Transactional
    @Override
    public Boolean updateBalance(final CustPayDTO dto) {
        log.info("ExchangePayFactory {}", JSONObject.toJSONString(dto));
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode();
        //RLock lock = redissonClient.getLock(key);
        try {
            //if (lock.tryLock(time,leaseTime, unit)) {
                BigDecimal substractAmount = dto.getAmount();
                //1.先扣款
                BalanceDTO beforeSubtract = getBalance(dto.getCusCode(), dto.getCurrencyCode());
                //先判断扣款余额是否充足
                if (beforeSubtract.getCurrentBalance().compareTo(substractAmount) < 0) {
                    return false;
                }

                String chargeType = dto.getCurrencyName() + "转" + dto.getCurrencyName2();
                String chargeType2 = dto.getCurrencyName2() + "转" + dto.getCurrencyName();

                BalanceDTO afterSubtract = calculateBalance(beforeSubtract, substractAmount.negate());
                setBalance(dto.getCusCode(), dto.getCurrencyCode(), afterSubtract);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
                AccountBalanceChange accountBalanceChange = recordOpLog(dto, afterSubtract.getCurrentBalance());
                //2.再充值
                BalanceDTO beforeAdd = getBalance(dto.getCusCode(), dto.getCurrencyCode2());
                BigDecimal addAmount = dto.getRate().multiply(substractAmount).setScale(2, BigDecimal.ROUND_FLOOR);
                // BalanceDTO afterAdd = calculateBalance(beforeAdd, addAmount);
                // 计算还款额，并销账（还账单）
                beforeAdd.rechargeAndSetAmount(addAmount);
                super.addForCreditBillAsync(beforeAdd.getCreditInfoBO().getRepaymentAmount(), dto.getCusCode(), dto.getCurrencyCode2());

                setBalance(dto.getCusCode(), dto.getCurrencyCode2(), beforeAdd);
                dto.setChargeCategoryChange(chargeType);
                setSerialBillLogAsync(dto, accountBalanceChange);
                dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
                dto.setAmount(addAmount);
                dto.setCurrencyCode(dto.getCurrencyCode2());
                dto.setCurrencyName(dto.getCurrencyName2());
                AccountBalanceChange afterBalanceChange = recordOpLog(dto, beforeAdd.getCurrentBalance());
                //设置流水账单
                dto.setCurrencyCode(accountBalanceChange.getCurrencyCode());
                dto.setCurrencyName(accountBalanceChange.getCurrencyName());
                dto.setChargeCategoryChange(chargeType2);
                setSerialBillLogAsync(dto, afterBalanceChange);
                recordDetailLogAsync(dto, beforeSubtract);
                //iAccountBalanceService.reloadCreditTime(Arrays.asList(dto.getCusCode()), dto.getCurrencyCode());


                return true;
//            } else {
//                log.error("汇率转换,请稍候重试{}", JSONObject.toJSONString(dto));
//                throw new RuntimeException("汇率转换操作超时,请稍候重试");
//            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("ExchangePayFactory异常:", e);
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
            throw new RuntimeException("汇率转换,请稍候重试!");
        } finally {
//            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                lock.unlock();
//            }
        }
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

    public void setSerialBillLogAsync(CustPayDTO dto, AccountBalanceChange accountBalanceChange) {
        financeThreadTaskPool.execute(() -> {
            AccountSerialBillDTO serialBill = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
            serialBill.setCurrencyCode(accountBalanceChange.getCurrencyCode());
            serialBill.setCurrencyName(accountBalanceChange.getCurrencyName());
            serialBill.setAmount(accountBalanceChange.getAmountChange());
            serialBill.setChargeCategory(dto.getChargeCategoryChange());
            serialBill.setChargeType(dto.getPayMethod().getPaymentName());
            serialBill.setBusinessCategory(BillEnum.PayMethod.BALANCE_EXCHANGE.getPaymentName());
            serialBill.setProductCategory(serialBill.getProductCategory());
            serialBill.setRemark("汇率为: ".concat(dto.getRate().toString()));
            serialBill.setNo(accountBalanceChange.getSerialNum());

            BigDecimal amountChange = accountBalanceChange.getAmountChange();
            //小于0算支出
            if(amountChange != null && amountChange.compareTo(BigDecimal.ZERO)  == -1){
                serialBill.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
            }else{
                serialBill.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
            }

            accountSerialBillService.add(serialBill);
        });
    }

}
