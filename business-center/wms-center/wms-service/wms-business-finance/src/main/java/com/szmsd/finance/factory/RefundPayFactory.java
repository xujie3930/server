package com.szmsd.finance.factory;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private SerialNumberClientService serialNumberClientService;

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

        List<String> isPrcStateList = new ArrayList();
        isPrcStateList.add("操作费");
        isPrcStateList.add("仓租");
        isPrcStateList.add("增值消费");
        isPrcStateList.add("物料费");

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
                log.info("setSerialBillLog selectDelOutbound :{}",dto.getNo());
                DelOutbound delOutbound = accountSerialBillMapper.selectDelOutbound(dto.getNo());
                log.info("setSerialBillLog selectDelOutbound 返回:{}",JSON.toJSONString(delOutbound));
                if (delOutbound!=null) {
                    accountSerialBill.setRefNo(delOutbound.getRefNo());
                    accountSerialBill.setShipmentService(delOutbound.getShipmentService());
                    accountSerialBill.setWeight(delOutbound.getWeight());
                    accountSerialBill.setCalcWeight(delOutbound.getCalcWeight());
                    accountSerialBill.setSpecifications(delOutbound.getSpecifications());
                    accountSerialBill.setTrackingNo(delOutbound.getTrackingNo());
                    accountSerialBill.setWarehouseCode(delOutbound.getWarehouseCode());
                    accountSerialBill.setWarehouseName(delOutbound.getWarehouseCode());
                    accountSerialBill.setShipmentService(delOutbound.getShipmentService());
                    accountSerialBill.setShipmentRule(delOutbound.getShipmentRule());
                    accountSerialBill.setProductCode(delOutbound.getPrcProductCode());
                        //accountSerialBill.setCurrencyCode(delOutbound.getCurrencyCode());
                }
            }

            if(StringUtils.isBlank(dto.getSerialNumber())) {
                String serialNumber = createSerialNumber();
                accountSerialBill.setSerialNumber(serialNumber);
            }else{
                accountSerialBill.setSerialNumber(dto.getSerialNumber());
            }

            String bcategory = accountSerialBill.getBusinessCategory();
            Integer prcstate = accountSerialBill.getPrcState();

            if(prcstate == null) {
                if (bcategory != null) {
                    boolean prcState = isPrcStateList.contains(bcategory);
                    if (!prcState) {
                        accountSerialBill.setPrcState(1);
                    }
                }
            }
            log.info("save accountSerialBill :{}", JSON.toJSONString(accountSerialBill));
            accountSerialBillService.save(accountSerialBill);
        });

    }

    private String createSerialNumber(){
        String randomNums = serialNumberClientService.generatorNumber(SerialNumberConstant.FSS_ACCOUNT_SERIAL_NUMBER);
        return randomNums;
    }

}
