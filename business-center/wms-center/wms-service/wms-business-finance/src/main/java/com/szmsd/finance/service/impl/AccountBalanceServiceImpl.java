package com.szmsd.finance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.FssRefundRequest;
import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.service.IThirdRechargeRecordService;
import com.szmsd.finance.util.SnowflakeId;
import com.szmsd.finance.vo.PreOnlineIncomeVo;
import com.szmsd.finance.vo.UserCreditInfoVO;
import com.szmsd.finance.ws.WebSocketServer;
import com.szmsd.http.api.feign.HttpRechargeFeignService;
import com.szmsd.http.dto.recharges.RechargesRequestAmountDTO;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.http.vo.RechargesResponseVo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liulei
 */
@Service
@Slf4j
public class AccountBalanceServiceImpl implements IAccountBalanceService {

    @Autowired
    PayFactoryBuilder payFactoryBuilder;

    @Autowired
    AccountBalanceMapper accountBalanceMapper;

    @Autowired
    AccountBalanceChangeMapper accountBalanceChangeMapper;

    @Autowired
    HttpRechargeFeignService httpRechargeFeignService;

    @Autowired
    IThirdRechargeRecordService thirdRechargeRecordService;

    @Autowired
    ISysDictDataService sysDictDataService;

    @Resource
    private WebSocketServer webSocketServer;

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @Override
    public List<AccountBalance> listPage(AccountBalanceDTO dto) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
        }
        if (StringUtils.isNotEmpty(dto.getCurrencyCode())) {
            queryWrapper.eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode());
        }
        List<AccountBalance> accountBalances = accountBalanceMapper.listPage(queryWrapper);
        accountBalances.forEach(AccountBalance::showCredit);
        return accountBalances;
    }

    @Override
    public List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto) {
        LambdaQueryWrapper<AccountBalanceChange> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalanceChange::getCusCode, dto.getCusCode());
        }
        if (dto.getPayMethod() != null) {
            if (dto.getPayMethod() == BillEnum.PayMethod.EXCHANGE_INCOME) {
                queryWrapper.and(wrapper -> wrapper.eq(AccountBalanceChange::getPayMethod, BillEnum.PayMethod.EXCHANGE_INCOME)
                        .or().eq(AccountBalanceChange::getPayMethod, BillEnum.PayMethod.EXCHANGE_PAYMENT));
            } else {
                queryWrapper.eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
            }
        }
        if (StringUtils.isNotEmpty(dto.getBeginTime())) {
            queryWrapper.ge(AccountBalanceChange::getCreateTime, dto.getBeginTime());
        }
        if (StringUtils.isNotEmpty(dto.getEndTime())) {
            queryWrapper.le(AccountBalanceChange::getCreateTime, dto.getEndTime());
        }
        if (StringUtils.isNotEmpty(dto.getNo())) {
            queryWrapper.eq(AccountBalanceChange::getNo, dto.getNo());
        }
        if (dto.getHasFreeze() != null) {
            queryWrapper.eq(AccountBalanceChange::getHasFreeze, dto.getHasFreeze());
        }
        if (StringUtils.isNotEmpty(dto.getOrderType())) {
            queryWrapper.eq(AccountBalanceChange::getOrderType, dto.getOrderType());
        }
        queryWrapper.orderByDesc(AccountBalanceChange::getCreateTime);
        return accountBalanceChangeMapper.recordListPage(queryWrapper);
    }

    /**
     * 线上预充值
     *
     * @param dto
     * @return
     */
    @Override
    public R preOnlineIncome(CustPayDTO dto) {
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        RechargesRequestDTO rechargesRequestDTO = new RechargesRequestDTO();
        //填充rechargesRequestDTO的信息
        fillRechargesRequestDTO(rechargesRequestDTO, dto);
        R<RechargesResponseVo> result = httpRechargeFeignService.onlineRecharge(rechargesRequestDTO);
        RechargesResponseVo vo = result.getData();
        //保存第三方接口调用充值记录
        thirdRechargeRecordService.saveRecord(dto, vo);
        if (result.getCode() != 200 || vo == null || StringUtils.isNotEmpty(vo.getCode())) {
            if (vo != null && StringUtils.isNotEmpty(vo.getCode())) {
                return R.failed(vo.getMessage());
            }
            return R.failed();
        }
        String rechargeUrl = vo.getRechargeUrl();
        if (StringUtils.isEmpty(rechargeUrl)) {
            return R.failed();
        }
        return R.ok(new PreOnlineIncomeVo(rechargesRequestDTO.getSerialNo(), rechargeUrl));
    }

    @Override
    @Transactional
    public R rechargeCallback(RechargesCallbackRequestDTO requestDTO) {
        //更新第三方接口调用记录
        ThirdRechargeRecord thirdRechargeRecord = thirdRechargeRecordService.updateRecordIfSuccess(requestDTO);
        if (thirdRechargeRecord == null) {
            return R.failed("没有找到对应的充值记录");
        }
        String rechargeStatus = HttpRechargeConstants.RechargeStatusCode.Successed.name();
        //如果充值成功进行充值
        if (StringUtils.equals(thirdRechargeRecord.getRechargeStatus(), rechargeStatus)) {
            CustPayDTO dto = new CustPayDTO();
            dto.setAmount(thirdRechargeRecord.getActualAmount());
            dto.setCurrencyCode(thirdRechargeRecord.getActualCurrency());
            dto.setCusCode(thirdRechargeRecord.getCusCode());
            dto.setRemark("手续费为: ".concat(thirdRechargeRecord.getTransactionAmount().toString().concat(thirdRechargeRecord.getTransactionCurrency())));
            dto.setNo(thirdRechargeRecord.getRechargeNo());
            return onlineIncome(dto);
        }
        return R.ok();
    }

    @Override
    public R warehouseFeeDeductions(CustPayDTO dto) {
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        if (dto.getPayType() == null) {
            return R.failed("支付类型为空");
        }
        setCurrencyName(dto);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        if (flag) {
            log.info("仓储费扣除--{}", JSONObject.toJSONString(dto));
            this.addOptLog(dto);
        }
        return flag ? R.ok() : R.failed("余额不足");
    }

    @Transactional
    @Override
    public R feeDeductions(CustPayDTO dto) {
        log.info("feeDeductions -{}", JSONObject.toJSONString(dto));
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
        dto.setPayType(BillEnum.PayType.PAYMENT);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        log.info("feeDeductions#updateBalance -{}", JSONObject.toJSONString(dto));
        boolean flag = abstractPayFactory.updateBalance(dto);
        if (flag) {
            log.info("费用扣除-操作费日志 - {}", JSONObject.toJSONString(dto));
            this.addOptLog(dto);
        }
        return flag ? R.ok() : R.failed("余额不足");
    }

    @Resource
    private ChargeFeignService chargeFeignService;
    @Resource
    private DelOutboundFeignService delOutboundFeignService;
    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    /**
     * 冻结 解冻 需要把费用扣减加到 操作费用表
     *
     * @param dto
     */
    private void addOptLog(CustPayDTO dto) {
        log.info("addOptLog {} ", JSONObject.toJSONString(dto));
        BillEnum.PayMethod payMethod = dto.getPayMethod();
        ChargeLog chargeLog = new ChargeLog();
        BeanUtils.copyProperties(dto, chargeLog);
        chargeLog
                .setCustomCode(dto.getCusCode()).setPayMethod(payMethod.name())
                .setOrderNo(dto.getNo()).setOperationPayMethod("业务操作").setSuccess(true)
                .setOperationType("").setPayMethod(payMethod.name())
        ;

        chargeLog.setRemark("-----------------------------------------");
        log.info("{} -  扣减操作费 {}", payMethod, JSONObject.toJSONString(chargeLog));
        if (null == chargeLog.getQty() || 0 >= chargeLog.getQty()) {
            //现在只有出库单需要补，入库单没有这些数据
            if (StringUtils.isNotBlank(chargeLog.getOrderNo()) && chargeLog.getOrderNo().startsWith("CK")) {
                R<DelOutboundVO> infoByOrderNo = delOutboundFeignService.getInfoByOrderNo(chargeLog.getOrderNo());
                if (null != infoByOrderNo && null != infoByOrderNo.getData()) {
                    DelOutboundVO data = infoByOrderNo.getData();
                    //String trackingNo = data.getTrackingNo();
                    List<DelOutboundDetailVO> details = data.getDetails();
                    if (CollectionUtils.isNotEmpty(details)) {
                        Long qty = details.stream().map(DelOutboundDetailVO::getQty).reduce(Long::sum).orElse(0L);
                        chargeLog.setQty(qty);
                    }
                }
            } else if (StringUtils.isNotBlank(chargeLog.getOrderNo()) && chargeLog.getOrderNo().startsWith("RK")) {
                R<InboundReceiptInfoVO> infoByOrderNo = inboundReceiptFeignService.info(chargeLog.getOrderNo());
                if (null != infoByOrderNo && null != infoByOrderNo.getData()) {
                    InboundReceiptInfoVO data = infoByOrderNo.getData();
                    //String trackingNo = data.getTrackingNo();
                    List<InboundReceiptDetailVO> details = data.getInboundReceiptDetails();
                    if (CollectionUtils.isNotEmpty(details)) {
                        int qty = 0;
                        if (payMethod == BillEnum.PayMethod.BALANCE_FREEZE || payMethod == BillEnum.PayMethod.BALANCE_THAW) {
                            qty = details.stream().map(InboundReceiptDetailVO::getDeclareQty).reduce(Integer::sum).orElse(0);
                        } else if (payMethod == BillEnum.PayMethod.BALANCE_DEDUCTIONS) {
                            qty = details.stream().map(InboundReceiptDetailVO::getPutQty).reduce(Integer::sum).orElse(0);
                        }
                        chargeLog.setQty((long) qty);
                    }
                }
            }
        }
        chargeFeignService.add(chargeLog);
        log.info("{} -  扣减操作费 {}", payMethod, JSONObject.toJSONString(chargeLog));
    }

    @Transactional
    @Override
    public R freezeBalance(CusFreezeBalanceDTO cfbDTO) {
        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());

        boolean flag = abstractPayFactory.updateBalance(dto);
        if (flag && "Freight".equals(dto.getOrderType()))
        // 冻结 解冻 需要把费用扣减加到 操作费用表
        {
            log.info("Freight thawBalance - {}", JSONObject.toJSONString(cfbDTO));
            this.addOptLog(dto);
        }
        return flag ? R.ok() : R.failed("可用余额不足以冻结");
    }

    @Transactional
    @Override
    public R thawBalance(CusFreezeBalanceDTO cfbDTO) {
        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if (BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        dto.setPayType(BillEnum.PayType.FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_THAW);
        setCurrencyName(dto);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        if (flag)
        //冻结 解冻 需要把费用扣减加到 操作费用表
        {

//            LambdaQueryWrapper<AccountSerialBill> wr = Wrappers.<AccountSerialBill>lambdaQuery()
//                    .eq(AccountSerialBill::getNo, dto.getNo())
//                    .orderByDesc(AccountSerialBill::getId);
//            List<AccountSerialBill> accountSerialBills = accountSerialBillService.getBaseMapper().selectList(wr);
//            String s = JSONObject.toJSONString(accountSerialBills);
//            log.info(" 解冻数据-- {}",s);
            //if (integer > 1) {
            // 冻结解冻会产生多笔 物流基础费 实际只扣除一笔，在最外层吧物流基础费删除 物流基础费会先解冻，然后直接扣除
//            int delete = accountSerialBillService.getBaseMapper().delete(Wrappers.<AccountSerialBill>lambdaUpdate()
//                    .eq(AccountSerialBill::getNo, dto.getNo())
//                    .eq(AccountSerialBill::getBusinessCategory, "物流基础费")
//                    .orderByDesc(AccountSerialBill::getId));
//            log.info("删除物流基础费 {}条", delete);
            //}
            log.info("thawBalance - {}", JSONObject.toJSONString(cfbDTO));
            this.addOptLog(dto);
        }
        return flag ? R.ok() : R.failed("冻结金额不足以解冻");
    }

    /**
     * 查询该用户对应币别的余额
     *
     * @param cusCode      客户编码
     * @param currencyCode 币别
     * @return 查询结果
     */
    @Override
    public BalanceDTO getBalance(String cusCode, String currencyCode) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cus_code", cusCode);
        queryWrapper.eq("currency_code", currencyCode);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if (accountBalance != null) {
            BalanceDTO balanceDTO = new BalanceDTO(accountBalance.getCurrentBalance(), accountBalance.getFreezeBalance(), accountBalance.getTotalBalance());
            CreditInfoBO creditInfoBO = balanceDTO.getCreditInfoBO();
            BeanUtils.copyProperties(accountBalance, creditInfoBO);
            balanceDTO.setCreditInfoBO(creditInfoBO);
            return balanceDTO;
        }

        log.info("getBalance() cusCode: {} currencyCode: {}", cusCode, currencyCode);
        String currencyName = getCurrencyName(currencyCode);
        accountBalance = new AccountBalance(cusCode, currencyCode, currencyName);
        //判断是否有启用中的授信信息，有的话需要设置
        List<AccountBalance> accountBalances = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                .eq(AccountBalance::getCreditType, CreditConstant.CreditTypeEnum.TIME_LIMIT.getValue())
                .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                .eq(AccountBalance::getCusCode, cusCode));
        if (CollectionUtils.isNotEmpty(accountBalances)) {
            AccountBalance accountBalanceCredit = accountBalances.get(0);
            BeanUtils.copyProperties(accountBalanceCredit, accountBalance);
            accountBalance.setId(null);
        }
        accountBalance.setCurrencyCode(currencyCode).setCurrencyName(currencyName)
                .setCreditUseAmount(BigDecimal.ZERO)
                .setTotalBalance(BigDecimal.ZERO).setCurrentBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO)
                .setCreateTime(new Date());
        accountBalanceMapper.insert(accountBalance);
        BalanceDTO balanceDTO = new BalanceDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        BeanUtils.copyProperties(accountBalance, balanceDTO);
        CreditInfoBO creditInfoBO = balanceDTO.getCreditInfoBO();
        BeanUtils.copyProperties(accountBalance, creditInfoBO);
        return balanceDTO;
    }

    @Override
    @Transactional
    public void setBalance(String cusCode, String currencyCode, BalanceDTO result, boolean needUpdateCredit) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusCode, cusCode);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyCode, currencyCode);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result.getCurrentBalance());
        lambdaUpdateWrapper.set(AccountBalance::getFreezeBalance, result.getFreezeBalance());
        lambdaUpdateWrapper.set(AccountBalance::getTotalBalance, result.getTotalBalance());
        if (needUpdateCredit && null != result.getCreditInfoBO()) {
            lambdaUpdateWrapper.set(AccountBalance::getCreditUseAmount, result.getCreditInfoBO().getCreditUseAmount());
            lambdaUpdateWrapper.set(AccountBalance::getCreditStatus, result.getCreditInfoBO().getCreditStatus());
            lambdaUpdateWrapper.set(AccountBalance::getCreditBeginTime, result.getCreditInfoBO().getCreditBeginTime());
            lambdaUpdateWrapper.set(AccountBalance::getCreditEndTime, result.getCreditInfoBO().getCreditEndTime());
            lambdaUpdateWrapper.set(AccountBalance::getCreditBufferTime, result.getCreditInfoBO().getCreditBufferTime());
        }
        accountBalanceMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public boolean withDrawBalanceCheck(String cusCode, String currencyCode, BigDecimal amount) {
        BigDecimal currentBalance = getCurrentBalance(cusCode, currencyCode);
        return currentBalance.compareTo(amount) >= 0;
    }

    @Override
    public int updateAccountBalanceChange(AccountBalanceChangeDTO dto) {
        LambdaUpdateWrapper<AccountBalanceChange> update = Wrappers.lambdaUpdate();
        update.set(AccountBalanceChange::getHasFreeze, dto.getHasFreeze())
                .eq(AccountBalanceChange::getCusCode, dto.getCusCode())
                .eq(AccountBalanceChange::getNo, dto.getNo())
                .eq(AccountBalanceChange::getCurrencyCode, dto.getCurrencyCode())
                .eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
        if (StringUtils.isNotBlank(dto.getOrderType())) {
            update.eq(AccountBalanceChange::getOrderType, dto.getOrderType());
        }
        return accountBalanceChangeMapper.update(null, update);
    }

    /**
     * 线上充值
     *
     * @param dto
     * @return
     */
    @Override
    public R onlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.ONLINE_INCOME);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed();
    }

    /**
     * 退费
     *
     * @param dto
     * @return
     */
    @Override
    public R refund(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        /*if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }*/
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.REFUND);
        dto.setPayMethod(BillEnum.PayMethod.REFUND);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed();
    }

    /**
     * 线下充值
     *
     * @param dto
     * @return
     */
    @Override
    public R offlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.OFFLINE_INCOME);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed();
    }

    /**
     * 余额汇率转换
     *
     * @param dto
     * @return
     */
    @Override
    public R balanceExchange(CustPayDTO dto) {
        AssertUtil.notNull(dto.getRate(), "汇率不能为空");
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode2(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        dto.setPayType(BillEnum.PayType.EXCHANGE);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed("余额不足");
    }

    /**
     * 查询币种余额，如果不存在初始化
     *
     * @param cusCode
     * @param currencyCode
     * @return
     */
    @Override
    public BigDecimal getCurrentBalance(String cusCode, String currencyCode) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cus_code", cusCode);
        queryWrapper.eq("currency_code", currencyCode);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if (accountBalance != null) {
            return accountBalance.getCurrentBalance();
        }
        accountBalance = new AccountBalance(cusCode, currencyCode, getCurrencyName(currencyCode));
        accountBalanceMapper.insert(accountBalance);
        return BigDecimal.ZERO;
    }

    /**
     * 更新币种余额
     *
     * @param cusCode
     * @param currencyCode
     * @param result
     */
    @Override
    @Transactional
    public void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusCode, cusCode);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyCode, currencyCode);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result);
        accountBalanceMapper.update(null, lambdaUpdateWrapper);
    }

    /**
     * 提现
     *
     * @param dto
     * @return
     */
    @Override
    public R withdraw(CustPayDTO dto) {
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.PAYMENT_NO_FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.WITHDRAW_PAYMENT);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed("余额不足");
    }

    private String getCurrencyName(String currencyCode) {
        return sysDictDataService.getCurrencyNameByCode(currencyCode);
    }

    private void setCurrencyName(CustPayDTO dto) {
        if (StringUtils.isEmpty(dto.getCurrencyName())) {
            dto.setCurrencyName(getCurrencyName(dto.getCurrencyCode()));
        }
    }

    /**
     * 填充第三方支付请求
     *
     * @param rechargesRequestDTO
     * @param dto
     */
    private void fillRechargesRequestDTO(RechargesRequestDTO rechargesRequestDTO, CustPayDTO dto) {
        rechargesRequestDTO.setSerialNo(SnowflakeId.getNextId12());
        rechargesRequestDTO.setBankCode(dto.getBankCode());
        rechargesRequestDTO.setRemark(dto.getRemark());
        rechargesRequestDTO.setMethod(dto.getMethod());
        //set amount
        RechargesRequestAmountDTO amount = new RechargesRequestAmountDTO();
        amount.setAmount(dto.getAmount());
        amount.setCurrencyCode(dto.getCurrencyCode());
        rechargesRequestDTO.setAmount(amount);
    }

    public boolean checkPayInfo(String cusCode, String currencyCode, BigDecimal amount) {
        boolean b1 = StringUtils.isEmpty(cusCode);
        boolean b2 = StringUtils.isEmpty(currencyCode);
        boolean b3 = amount == null;
        return b1 || b2 || b3 || amount.setScale(2, BigDecimal.ROUND_FLOOR).compareTo(BigDecimal.ZERO) < 1;
    }

    @Override
    public void updateCreditStatus(CustPayDTO dto) {
        int update = accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                .eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode())
                .eq(AccountBalance::getCusCode, dto.getCusCode())
                .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                .set(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ARREARAGE_DEACTIVATION.getValue())
        );
        AssertUtil.isTrue(update <= 1, "更新授信额度状态异常");
        log.info("更新{}条授信额度状态 {}", update, JSONObject.toJSONString(dto));
    }

    private boolean checkAmountIsZero(BigDecimal bigDecimal) {
        return bigDecimal != null && bigDecimal.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 修改账期(A+B)：
     * A 修改需要截断当前账单止于这天，第二天直接按 修改后的A去算周期
     * B修改则直接修改
     *
     * @param userCreditDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserCredit(UserCreditDTO userCreditDTO) {
        log.info("更新用户授信额度信息 {}", userCreditDTO);
        String cusCode = userCreditDTO.getCusCode();
        List<UserCreditDetailDTO> userCreditDetailList = userCreditDTO.getUserCreditDetailList();
        List<String> updateCurrencyCodeList = userCreditDetailList.stream().map(UserCreditDetailDTO::getCurrencyCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        CreditConstant.CreditTypeEnum newCreditTypeEnum = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditType).filter(Objects::nonNull).findAny().orElse(CreditConstant.CreditTypeEnum.DEFAULT);
        List<AccountBalance> accountBalancesOld = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaUpdate().eq(AccountBalance::getCusCode, cusCode));

        Integer creditTimeInterval = userCreditDTO.getUserCreditDetailList().stream().map(UserCreditDetailDTO::getCreditTimeInterval).filter(Objects::nonNull).findAny().orElse(0);
        Integer creditBufferTimeInterval = userCreditDTO.getUserCreditDetailList().stream().map(UserCreditDetailDTO::getCreditBufferTimeInterval).filter(Objects::nonNull).findAny().orElse(0);
        ChronoUnit creditTimeUnit = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditTimeUnit).findAny().orElse(ChronoUnit.DAYS);
        ChronoUnit creditBufferTimeUnit = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditBufferTimeUnit).findAny().orElse(ChronoUnit.DAYS);


        if (CollectionUtils.isEmpty(userCreditDetailList)) {
            // 校验需要还的欠款
            LambdaUpdateWrapper<AccountBalance> accountOldWrapper = Wrappers.<AccountBalance>lambdaUpdate()
                    .eq(AccountBalance::getCusCode, cusCode)
                    .notIn(CollectionUtils.isNotEmpty(updateCurrencyCodeList), AccountBalance::getCurrencyCode, updateCurrencyCodeList).or().nested(x -> x.eq(AccountBalance::getCusCode, cusCode).ne(AccountBalance::getCreditType, newCreditTypeEnum));
            List<AccountBalance> accountBalances = accountBalanceMapper.selectList(accountOldWrapper);

            Map<String, BigDecimal> needToRepayMap = accountBalances.stream().filter(x -> checkAmountIsZero(x.getCreditUseAmount())).collect(Collectors.toMap(AccountBalance::getCurrencyCode, x -> Optional.ofNullable(x.getCreditUseAmount()).orElse(BigDecimal.ZERO)));
            StringBuilder errorMsg = new StringBuilder();
            needToRepayMap.forEach((x, y) -> {
                if (!checkAmountIsZero(y)) {
                    errorMsg.append(String.format("%s ：%s\n", x, y));
                }
            });
            AssertUtil.isTrue(StringUtils.isBlank(errorMsg.toString()), "客户仍有授信额度未还清：\n" + errorMsg);
        }

        if (CollectionUtils.isNotEmpty(userCreditDetailList)) {
            if (newCreditTypeEnum == CreditConstant.CreditTypeEnum.TIME_LIMIT) {
                //新用户授信额度 需要开一个账户显示授信额
                Integer currencyListCount = accountBalanceMapper.selectCount(Wrappers.<AccountBalance>lambdaUpdate()
                        .eq(AccountBalance::getCusCode, cusCode));
                if (currencyListCount == 0) {
                    UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                    BeanUtils.copyProperties(userCreditDTO, userCreditDetailDTO);
                    userCreditDetailDTO.setCurrencyName("人民币");
                    userCreditDetailDTO.setCurrencyCode("CNY");
                    insertNewCreditAccount(userCreditDTO.getCusCode(), Collections.singletonList(userCreditDetailDTO));
                }
            }
        } else {
            // 删除 / 关闭 已启用的授信额度
            int update = accountBalanceMapper.update(new AccountBalance(),
                    Wrappers.<AccountBalance>lambdaUpdate()
                            .eq(AccountBalance::getCusCode, cusCode)
                            .set(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.DISABLED.getValue())
            );
            log.info("禁用用户授信额度{}- {}条", userCreditDTO, update);
        }


        CreditConstant.CreditTypeEnum creditTypeEnum = accountBalancesOld.stream()
                .filter(x -> null != x.getCreditType())
                .findAny()
                .map(AccountBalance::getCreditType)
                .map(CreditConstant.CreditTypeEnum::getThisByTypeCode)
                .orElse(CreditConstant.CreditTypeEnum.DEFAULT);
        LocalDate now = LocalDate.now();
        LocalDateTime newStart = now.atTime(0, 0, 0);
        LocalDateTime newEnd = newStart.plus(creditTimeInterval, creditTimeUnit).minusSeconds(1);
        LocalDateTime newBufferEnd = newEnd.plus(creditBufferTimeInterval, creditBufferTimeUnit);
        Map<String, AccountBalance> oldAccountInfo = accountBalancesOld.stream().collect(Collectors.toMap(AccountBalance::getCurrencyCode, x -> x));

        switch (creditTypeEnum) {
            case QUOTA:
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        List<String> oldCodeList = new ArrayList<>(oldAccountInfo.keySet());
                        List<UserCreditDetailDTO> updateList = userCreditDetailList.stream().filter(x -> oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(updateList)) {
                            log.info("更新授信额信息,用户：{} , {}", cusCode, JSONObject.toJSONString(updateList));
                            this.updateCreditBatch(updateList, cusCode);
                        }
                        List<UserCreditDetailDTO> insertList = userCreditDetailList.stream().filter(x -> !oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(insertList)) {
                            insertNewCreditAccount(cusCode, insertList);
                        }
                        return;
                    case TIME_LIMIT:
                        // 把之前所有的钱包账户更新成期限
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO.setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT);
                            userCreditDetailDTO
                                    .setCreditTimeInterval(creditTimeInterval).setCreditTimeUnit(creditTimeUnit).setCreditBeginTime(newStart).setCreditEndTime(newEnd)
                                    .setCreditBufferTimeInterval(creditBufferTimeInterval).setCreditBufferTime(newBufferEnd).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        this.updateCreditBatch(updateCreditList, cusCode);
                    case DEFAULT:
                    default:
                        return;
                }
            case TIME_LIMIT:
                // 判断是否修改A的值 启用中 时间类型 A变化 账期改账期 或者账期改额度 需要截断账单
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        // 更新限额也直接更新 但是未归还的也会查询出来，但是金额是0 需要更新授信类型
                        accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                                .set(AccountBalance::getCreditTimeFlag, true)
                                .set(AccountBalance::getCreditType, CreditConstant.CreditTypeEnum.QUOTA.getValue()));
                        this.updateCreditBatch(userCreditDetailList, cusCode);
                        return;
                    case TIME_LIMIT:
                        // 更新账期直接更新
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO
                                    .setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT)
                                    .setCreditTimeInterval(creditTimeInterval).setCreditBufferTimeInterval(creditBufferTimeInterval)
                                    .setCreditTimeUnit(creditTimeUnit).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            // 判断是否修改A的值 启用中 时间类型 A变化 账期改账期 或者账期改额度 需要截断账单
                            Integer oldCreditTimeInterval = Optional.ofNullable(x.getCreditTimeInterval()).orElse(0);
                            // A改大 不更新开始时间 更新结束时间 不截取
                            if (creditTimeInterval.compareTo(oldCreditTimeInterval) >= 0) {
                                LocalDateTime creditBeginTime = x.getCreditBeginTime();
                                LocalDateTime creditEndTime = creditBeginTime.plus(creditTimeInterval, creditTimeUnit).minusSeconds(1);
                                LocalDateTime creditBufferTime = creditEndTime.plus(creditBufferTimeInterval, creditBufferTimeUnit);
                                userCreditDetailDTO.setCreditBeginTime(creditBeginTime).setCreditEndTime(creditEndTime).setCreditBufferTime(creditBufferTime);
                            } else {
                                // A改小 更新开始时间 更新结束时间及缓存期 截取订单
                                userCreditDetailDTO.setCreditBeginTime(newStart).setCreditEndTime(newEnd).setCreditBufferTime(newBufferEnd).setCreditTimeFlag(true);
                            }
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        this.updateCreditBatch(updateCreditList, cusCode);
                        return;
                    case DEFAULT:
                    default:
                        return;
                }
            case DEFAULT:
                // 新增
                switch (newCreditTypeEnum) {
                    case QUOTA:
                        // 更新限额也直接更新
                        List<String> oldCodeList = new ArrayList<>(oldAccountInfo.keySet());
                        List<UserCreditDetailDTO> updateList = userCreditDetailList.stream().filter(x -> oldCodeList.contains(x.getCurrencyCode())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(updateList)) {
                            log.info("更新授信额信息,用户：{} , {}", cusCode, JSONObject.toJSONString(updateList));
                            this.updateCreditBatch(updateList, cusCode);
                        } else {
                            insertNewCreditAccount(cusCode, userCreditDetailList);
                        }
                        return;
                    case TIME_LIMIT:
                        List<UserCreditDetailDTO> updateCreditList = accountBalancesOld.stream().map(x -> {
                            UserCreditDetailDTO userCreditDetailDTO = new UserCreditDetailDTO();
                            userCreditDetailDTO.setCurrencyCode(x.getCurrencyCode()).setCreditType(CreditConstant.CreditTypeEnum.TIME_LIMIT);
                            userCreditDetailDTO
                                    .setCreditTimeInterval(creditTimeInterval).setCreditTimeUnit(creditTimeUnit).setCreditBeginTime(newStart).setCreditEndTime(newEnd)
                                    .setCreditBufferTimeInterval(creditBufferTimeInterval).setCreditBufferTime(newBufferEnd).setCreditBufferTimeUnit(creditBufferTimeUnit);
                            return userCreditDetailDTO;
                        }).collect(Collectors.toList());
                        // 更新账期直接更新
                        this.updateCreditBatch(updateCreditList, cusCode);
                        return;
                    case DEFAULT:
                    default:
                        return;
                }
            default:
                return;
        }

    }

    private void insertNewCreditAccount(String cusCode, List<UserCreditDetailDTO> insertList) {
        List<AccountBalance> insertAccountList = insertList.stream().map(x -> {
            AccountBalance accountBalance = new AccountBalance();
            BeanUtils.copyProperties(x, accountBalance);
            accountBalance.setCreditStatus(CreditConstant.CreditStatusEnum.ACTIVE.getValue()).setCreditType(CreditConstant.CreditTypeEnum.QUOTA.getValue() + "");
            accountBalance.setCurrentBalance(BigDecimal.ZERO).setFreezeBalance(BigDecimal.ZERO).setTotalBalance(BigDecimal.ZERO);
            accountBalance.setCusCode(cusCode);
            return accountBalance;
        }).collect(Collectors.toList());
        log.info("新增授信额信息,用户：{} , {}", cusCode, JSONObject.toJSONString(insertAccountList));
        insertAccountList.forEach(accountBalanceMapper::insert);
    }

    private void updateCreditBatch(List<UserCreditDetailDTO> updateList, String cusCode) {
        List<String> currencyCode = updateList.stream().map(UserCreditDetailDTO::getCurrencyCode).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        int i = accountBalanceMapper.updateCreditBatch(updateList, cusCode, currencyCode);
    }

    @Override
    public List<UserCreditInfoVO> queryUserCredit(String cusCode) {
        List<AccountBalance> accountBalances = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                .eq(AccountBalance::getCusCode, cusCode)
                .eq(AccountBalance::getCreditStatus, CreditConstant.CreditStatusEnum.ACTIVE.getValue())
                .isNotNull(AccountBalance::getCreditType)
        );
        List<UserCreditInfoVO> collect = accountBalances.stream().map(x -> {
            UserCreditInfoVO userCreditInfoVO = new UserCreditInfoVO();
            BeanUtils.copyProperties(x, userCreditInfoVO);
            userCreditInfoVO.setCreditType(CreditConstant.CreditTypeEnum.getThisByTypeCode(x.getCreditType()).name());
            return userCreditInfoVO;
        }).collect(Collectors.toList());
        boolean present = collect.stream().anyMatch(x -> null != x.getCreditType() && (CreditConstant.CreditTypeEnum.TIME_LIMIT.name() + "").equals(x.getCreditType()));
        if (CollectionUtils.isNotEmpty(collect)) {
            if (present) {
                UserCreditInfoVO userCreditInfoVO = collect.get(0);
                userCreditInfoVO.setCreditLine(null);
                userCreditInfoVO.setCurrencyCode(null);
                userCreditInfoVO.setCurrencyName(null);
                collect = Collections.singletonList(userCreditInfoVO);
            } else {
                collect.forEach(x -> x.setCreditTimeInterval(null));
            }
        }
        return collect;
    }

    @Override
    public List<AccountBalance> queryAndUpdateUserCreditTimeFlag() {
        List<AccountBalance> accountBalanceList = accountBalanceMapper.selectList(Wrappers.<AccountBalance>lambdaQuery()
                .eq(AccountBalance::getCreditTimeFlag, true)
                .groupBy(AccountBalance::getCusCode));
        int update = accountBalanceMapper.update(new AccountBalance(), Wrappers.<AccountBalance>lambdaUpdate()
                .set(AccountBalance::getCreditTimeFlag, false)
                .eq(AccountBalance::getCreditTimeFlag, true));
        return accountBalanceList;
    }

    @Override
    public List<AccountBalance> queryThePreTermBill() {
        return accountBalanceMapper.queryThePreTermBill();
    }

    @Override
    public int reloadCreditTime(List<String> cusCodeList) {
        LocalDate now = LocalDate.now();
        return accountBalanceMapper.update(new AccountBalance(),
                Wrappers.<AccountBalance>lambdaUpdate().in(AccountBalance::getCusCode, cusCodeList)
                        .set(AccountBalance::getCreditBeginTime, now)
                        .setSql("credit_begin_time = DATE_FORMAT( NOW(), '%Y-%m-%d 00:00:00' ) ")
                        .setSql("credit_end_time = DATE_ADD( DATE_FORMAT( NOW(), '%Y-%m-%d 23:59:59' ), INTERVAL credit_time_interval - 1 DAY ) ")
                        .setSql("credit_buffer_time = DATE_ADD( DATE_FORMAT( NOW(), '%Y-%m-%d 23:59:59' ), INTERVAL credit_time_interval + credit_buffer_time_interval - 1 DAY ) ")
        );
    }
}
