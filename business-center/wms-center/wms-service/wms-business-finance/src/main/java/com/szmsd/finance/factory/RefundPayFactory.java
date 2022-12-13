package com.szmsd.finance.factory;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.BalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.service.IAccountSerialBillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 退费
 */
@Slf4j
@Component
public class RefundPayFactory extends AbstractPayFactory {

    @Resource
    private IAccountSerialBillService accountSerialBillService;
    @Autowired
    private AccountSerialBillMapper accountSerialBillMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean updateBalance(final CustPayDTO dto) {
        log.info("RefundPayFactory {}", JSONObject.toJSONString(dto));
        try {

            String currencyCode = dto.getCurrencyCode();

            log.info("【退费】dto-- {}",JSONObject.toJSONString(dto));

            BalanceDTO oldBalance = getBalance(dto.getCusCode(), dto.getCurrencyCode());

            log.info("【退费】dto-- 1 {} 可用余额：{}，冻结余额：{}，总余额：{},余额剩余：{},授信额度:{} ",currencyCode,oldBalance.getCurrentBalance(),oldBalance.getFreezeBalance(),oldBalance.getTotalBalance(),oldBalance.getCreditInfoBO().getCreditUseAmount(),JSONObject.toJSONString(oldBalance));

            boolean success = false;

            log.info("【退费】RefundPayFactory-- {}",JSONObject.toJSONString(oldBalance));
            BigDecimal changeAmount = dto.getAmount();
            if (changeAmount.compareTo(BigDecimal.ZERO) >= 0) {

                log.info("balance mKey version 1.1 充值{}",dto.getNo());
                // 充值
                success = oldBalance.rechargeAndSetAmount(changeAmount);
                super.addForCreditBillAsync(oldBalance.getCreditInfoBO().getRepaymentAmount(), dto.getCusCode(), dto.getCurrencyCode());
            } else {
                log.info("balance mKey version 1.1 退费强制扣钱{}",dto.getNo());
                // 退费强制扣钱
                success = oldBalance.payAnyWay(changeAmount.negate());
            }
            if (!success){
                return false;
            }

            log.info("balance mKey version 2{}",dto.getNo());

            log.info("【退费】dto-- 2 {} 可用余额：{}，冻结余额：{}，总余额：{},余额剩余：{},授信额度：{} ",currencyCode,oldBalance.getCurrentBalance(),oldBalance.getFreezeBalance(),oldBalance.getTotalBalance(),oldBalance.getCreditInfoBO().getCreditUseAmount(),JSONObject.toJSONString(oldBalance));

            BalanceDTO result = oldBalance;
            setBalance(dto.getCusCode(), dto.getCurrencyCode(), result, true);

            log.info("【退费】setBalance-- 3 {} 可用余额：{}，冻结余额：{}，总余额：{},余额剩余：{},授信额度：{} ",currencyCode,oldBalance.getCurrentBalance(),oldBalance.getFreezeBalance(),oldBalance.getTotalBalance(),oldBalance.getCreditInfoBO().getCreditUseAmount(),JSONObject.toJSONString(oldBalance));

            recordOpLogAsync(dto, result.getCurrentBalance());
            recordDetailLogAsync(dto, oldBalance);
            log.info("【退费】setSerialBillLog-- {}",JSONObject.toJSONString(dto));
            setSerialBillLog(dto);

            log.info("balance mKey version 3{}",dto.getNo());

            return true;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动回滚事务
            e.printStackTrace();
            log.error("RefundPayFactory异常:", e);
            log.info("获取余额异常，加锁失败");
            log.info("异常信息:" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
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
            if (StringUtils.isNotBlank(dto.getNo())) {
                DelOutbound delOutbound = accountSerialBillMapper.selectDelOutbound(dto.getNo());
                if (delOutbound!=null) {
                    if (delOutbound.getId() != null) {
                        accountSerialBill.setRefNo(delOutbound.getRefNo());
                        accountSerialBill.setShipmentService(delOutbound.getShipmentService());
                        accountSerialBill.setWeight(delOutbound.getWeight());
                        accountSerialBill.setCalcWeight(delOutbound.getCalcWeight());
                        accountSerialBill.setSpecifications(delOutbound.getSpecifications());
                    }
                }
            }

                String serialNumber = createSerialNumber();
                accountSerialBill.setSerialNumber(serialNumber);

                accountSerialBillService.save(accountSerialBill);
        });
    }

    private String createSerialNumber(){

        String s = DateUtils.dateTime();
        String randomNums = RandomUtil.randomNumbers(8);
        return s + randomNums;
    }

}
