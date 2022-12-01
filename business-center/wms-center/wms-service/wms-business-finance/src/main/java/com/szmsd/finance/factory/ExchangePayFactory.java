package com.szmsd.finance.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.IExchangeRateService;
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

    @Resource
    private IExchangeRateService iExchangeRateService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBalance(final CustPayDTO dto) {
        log.info("ExchangePayFactory {}", JSONObject.toJSONString(dto));
        try {

            //step 1. 检查是否存在汇率配置
            String currencyCode = dto.getCurrencyCode();
            String currencyCode2 =  dto.getCurrencyCode2();
            String cusCode = dto.getCusCode();

            R<ExchangeRate> rateR = iExchangeRateService.selectRate(currencyCode,currencyCode2);

            if(rateR.getCode() != 200){
                throw new RuntimeException(rateR.getMsg());
            }

            ExchangeRate rate = rateR.getData();

            if(rate == null){
                throw new RuntimeException("无法获取配置汇率配置信息");
            }

            BigDecimal substractAmount = dto.getAmount();
            //先判断扣款余额是否充足
            BalanceDTO beforeSubtract = getBalance(cusCode, currencyCode);
            if (beforeSubtract.getCurrentBalance().compareTo(substractAmount) < 0) {
                return false;
            }

            log.info("获取账户当前余额---当前余额{}",beforeSubtract.getCurrentBalance());

            //转换后金额
            BalanceDTO afterSubtract = calculateBalance(beforeSubtract, substractAmount.negate());
            setBalance(cusCode, currencyCode, afterSubtract);

            log.info("完成转换扣款---{}");

            dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_PAYMENT);
            AccountBalanceChange accountBalanceChange = recordOpLog(dto, afterSubtract.getCurrentBalance());
            //2.再充值
            BalanceDTO beforeAdd = getBalance(cusCode, currencyCode2);
            BigDecimal addAmount = rate.getRate().multiply(substractAmount).setScale(2, BigDecimal.ROUND_FLOOR);
            // BalanceDTO afterAdd = calculateBalance(beforeAdd, addAmount);
            // 计算还款额，并销账（还账单）
            beforeAdd.rechargeAndSetAmount(addAmount);
            super.addForCreditBillAsync(beforeAdd.getCreditInfoBO().getRepaymentAmount(), cusCode, currencyCode2);

            setBalance(cusCode, currencyCode2, beforeAdd);
            setSerialBillLogAsync(dto, accountBalanceChange);
            dto.setPayMethod(BillEnum.PayMethod.EXCHANGE_INCOME);
            dto.setAmount(addAmount);
            dto.setCurrencyCode(currencyCode2);
            dto.setCurrencyName(dto.getCurrencyName2());
            AccountBalanceChange afterBalanceChange = recordOpLog(dto, beforeAdd.getCurrentBalance());
            //设置流水账单
            dto.setCurrencyCode(accountBalanceChange.getCurrencyCode());
            dto.setCurrencyName(accountBalanceChange.getCurrencyName());
            setSerialBillLogAsync(dto, afterBalanceChange);
            recordDetailLogAsync(dto, beforeSubtract);

            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("ExchangePayFactory异常:", e);
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
            throw new RuntimeException("汇率转换,请稍候重试!");
        } finally {
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

        BigDecimal currentBalance = oldBalance.getCurrentBalance().add(changeAmount);
        BigDecimal totalBalance = oldBalance.getTotalBalance().add(changeAmount);

        oldBalance.setCurrentBalance(currentBalance);
        oldBalance.setTotalBalance(totalBalance);
        return oldBalance;
    }

    public void setSerialBillLogAsync(CustPayDTO dto, AccountBalanceChange accountBalanceChange) {
        financeThreadTaskPool.execute(() -> {
            AccountSerialBillDTO serialBill = BeanMapperUtil.map(dto, AccountSerialBillDTO.class);
            serialBill.setCurrencyCode(accountBalanceChange.getCurrencyCode());
            serialBill.setCurrencyName(accountBalanceChange.getCurrencyName());
            serialBill.setAmount(accountBalanceChange.getAmountChange());
            serialBill.setChargeCategory(dto.getCurrencyName().concat("转").concat(dto.getCurrencyName2()));
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
