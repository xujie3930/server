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
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.service.IThirdRechargeRecordService;
import com.szmsd.finance.util.SnowflakeId;
import com.szmsd.finance.vo.PreOnlineIncomeVo;
import com.szmsd.finance.ws.WebSocketServer;
import com.szmsd.http.api.feign.HttpRechargeFeignService;
import com.szmsd.http.dto.recharges.RechargesRequestAmountDTO;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.http.vo.RechargesResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public List<AccountBalance> listPage(AccountBalanceDTO dto) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
        }
        if (StringUtils.isNotEmpty(dto.getCurrencyCode())) {
            queryWrapper.eq(AccountBalance::getCurrencyCode, dto.getCurrencyCode());
        }
        return accountBalanceMapper.listPage(queryWrapper);
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
        return R.ok(new PreOnlineIncomeVo(rechargesRequestDTO.getSerialNo(),rechargeUrl));
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
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        if (dto.getPayType() == null) {
            return R.failed("支付类型为空");
        }
        setCurrencyName(dto);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed("余额不足");
    }

    @Transactional
    @Override
    public R feeDeductions(CustPayDTO dto) {
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
        dto.setPayType(BillEnum.PayType.PAYMENT);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());
        boolean flag = abstractPayFactory.updateBalance(dto);
        return flag ? R.ok() : R.failed("余额不足");
    }

    @Resource
    private ChargeFeignService chargeFeignService;

    /**
     *  冻结 解冻 需要把费用扣减加到 操作费用表
     * @param dto
     */
    private void addOptLog(CustPayDTO dto) {
        log.info("addOptLog {} ", JSONObject.toJSONString(dto));
        BillEnum.PayMethod payType = dto.getPayMethod();
        boolean b = !(payType == BillEnum.PayMethod.BALANCE_FREEZE || payType == BillEnum.PayMethod.BALANCE_THAW);
        if (b) return;
        ChargeLog chargeLog = new ChargeLog();
        BeanUtils.copyProperties(dto,chargeLog);
        chargeLog
                .setCustomCode(dto.getCusCode()).setPayMethod(payType.name())
                .setOrderNo(dto.getNo()).setOperationPayMethod("业务操作").setSuccess(true)
        ;
        if (payType == BillEnum.PayMethod.BALANCE_FREEZE) {
            chargeLog.setOperationType("").setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE.name());
        } else if (payType == BillEnum.PayMethod.BALANCE_THAW) {
            chargeLog.setOperationType("").setPayMethod(BillEnum.PayMethod.BALANCE_THAW.name());
        }
        chargeFeignService.add(chargeLog);
        log.info("{} -  扣减操作费 {}", payType, JSONObject.toJSONString(chargeLog));
    }

    @Transactional
    @Override
    public R freezeBalance(CusFreezeBalanceDTO cfbDTO) {
        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
        if (checkPayInfo(dto.getCusCode(), dto.getCurrencyCode(), dto.getAmount())) {
            return R.failed("客户编码/币种不能为空且金额必须大于0.01");
        }
        setCurrencyName(dto);
        dto.setPayType(BillEnum.PayType.FREEZE);
        dto.setPayMethod(BillEnum.PayMethod.BALANCE_FREEZE);
        AbstractPayFactory abstractPayFactory = payFactoryBuilder.build(dto.getPayType());

        boolean flag = abstractPayFactory.updateBalance(dto);
        if (flag)
            //冻结 解冻 需要把费用扣减加到 操作费用表
            this.addOptLog(dto);
        return flag ? R.ok() : R.failed("可用余额不足以冻结");
    }

    @Transactional
    @Override
    public R thawBalance(CusFreezeBalanceDTO cfbDTO) {
        CustPayDTO dto = new CustPayDTO();
        BeanUtils.copyProperties(cfbDTO, dto);
        if(BigDecimal.ZERO.compareTo(dto.getAmount()) == 0) return R.ok();
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
            this.addOptLog(dto);
        return flag ? R.ok() : R.failed("冻结金额不足以解冻");
    }

    @Override
    public BalanceDTO getBalance(String cusCode, String currencyCode) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cus_code", cusCode);
        queryWrapper.eq("currency_code", currencyCode);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if (accountBalance != null) {
            return new BalanceDTO(accountBalance.getCurrentBalance(), accountBalance.getFreezeBalance(), accountBalance.getTotalBalance());
        }
        log.info("getBalance() cusCode: {} currencyCode: {}",cusCode,currencyCode);
        accountBalance = new AccountBalance(cusCode, currencyCode, getCurrencyName(currencyCode));
        accountBalanceMapper.insert(accountBalance);
        return new BalanceDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public void setBalance(String cusCode, String currencyCode, BalanceDTO result) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusCode, cusCode);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyCode, currencyCode);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance, result.getCurrentBalance());
        lambdaUpdateWrapper.set(AccountBalance::getFreezeBalance, result.getFreezeBalance());
        lambdaUpdateWrapper.set(AccountBalance::getTotalBalance, result.getTotalBalance());
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
        AssertUtil.notNull(dto.getRate(),"汇率不能为空");
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
}
