package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.finance.factory.abstractFactory.AbstractPayFactory;
import com.szmsd.finance.factory.abstractFactory.PayFactoryBuilder;
import com.szmsd.finance.mapper.AccountBalanceChangeMapper;
import com.szmsd.finance.mapper.AccountBalanceMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IThirdRechargeRecordService;
import com.szmsd.finance.util.SnowflakeId;
import com.szmsd.http.api.feign.HttpRechargeFeignService;
import com.szmsd.http.dto.recharges.RechargesRequestAmountDTO;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.http.vo.RechargesResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liulei
 */
@Service
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

    @Override
    public List<AccountBalance> listPage(AccountBalanceDTO dto) {
        LambdaQueryWrapper<AccountBalance> queryWrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(dto.getCusCode())) {
            queryWrapper.eq(AccountBalance::getCusCode, dto.getCusCode());
        }
        return accountBalanceMapper.listPage(queryWrapper);
    }

    @Override
    public List<AccountBalanceChange> recordListPage(AccountBalanceChangeDTO dto) {
        LambdaQueryWrapper<AccountBalanceChange> queryWrapper = Wrappers.lambdaQuery();
        if(dto.getCusId()!=null) {
            queryWrapper.eq(AccountBalanceChange::getCusId, dto.getCusId());
        }
        if(dto.getPayMethod()!=null) {
            queryWrapper.eq(AccountBalanceChange::getPayMethod, dto.getPayMethod());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(AccountBalanceChange::getCreateTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(AccountBalanceChange::getCreateTime,dto.getEndTime());
        }
        return accountBalanceChangeMapper.recordListPage(queryWrapper);
    }

    /**
     * 线上预充值
     * @param dto
     * @return
     */
    @Override
    public R preOnlineIncome(CustPayDTO dto) {
        RechargesRequestDTO rechargesRequestDTO=new RechargesRequestDTO();
        //填充rechargesRequestDTO的信息
        fillRechargesRequestDTO(rechargesRequestDTO,dto);
        R<RechargesResponseVo> result = httpRechargeFeignService.onlineRecharge(rechargesRequestDTO);
        RechargesResponseVo vo = result.getData();
        //保存第三方接口调用充值记录
        thirdRechargeRecordService.saveRecord(dto,vo);
        if(result.getCode()!=200||vo==null||StringUtils.isNotEmpty(vo.getCode())){
            if(vo!=null&&StringUtils.isNotEmpty(vo.getCode())){
                return R.failed(vo.getCode());
            }
            return R.failed();
        }
        return R.ok();
    }

    @Override
    @Transactional
    public R rechargeCallback(RechargesCallbackRequestDTO requestDTO) {
        //更新第三方接口调用记录
        ThirdRechargeRecord thirdRechargeRecord = thirdRechargeRecordService.updateRecordIfSuccess(requestDTO);
        if(thirdRechargeRecord==null){
            return R.failed();
        }
        String rechargeStatus= HttpRechargeConstants.RechargeStatusCode.Successed.name();
        //如果充值成功进行充值
        if(StringUtils.equals(thirdRechargeRecord.getRechargeStatus(),rechargeStatus)){
            CustPayDTO dto=new CustPayDTO();
            dto.setAmount(thirdRechargeRecord.getActualAmount());
            dto.setCurrencyCode(thirdRechargeRecord.getActualCurrency());
            dto.setCusCode(thirdRechargeRecord.getCusCode());
            return onlineIncome(dto);
        }
        return R.ok();
    }

    @Override
    public R warehouseFeeDeductions(CustPayDTO dto) {
        if(dto.getPayType()==null){
            return R.failed("支付类型为空");
        }
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 线上充值
     * @param dto
     * @return
     */
    @Override
    public R onlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.ONLINE_INCOME);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 线下充值
     * @param dto
     * @return
     */
    @Override
    public R offlineIncome(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.INCOME);
        dto.setPayMethod(BillEnum.PayMethod.OFFLINE_INCOME);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 余额汇率转换
     * @param dto
     * @return
     */
    @Override
    public R balanceExchange(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.EXCHANGE);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    /**
     * 查询币种余额，如果不存在初始化
     * @param cusCode
     * @param currencyCode
     * @return
     */
    @Override
    public BigDecimal getCurrentBalance(String cusCode,String currencyCode) {
        QueryWrapper<AccountBalance> queryWrapper = new QueryWrapper();
        queryWrapper.eq("cus_code",cusCode);
        queryWrapper.eq("currency_code",currencyCode);
        AccountBalance accountBalance = accountBalanceMapper.selectOne(queryWrapper);
        if(accountBalance!=null){
            return accountBalance.getCurrentBalance();
        }
        accountBalance=new AccountBalance(cusCode,currencyCode,getCurrencyName(currencyCode),BigDecimal.ZERO);
        accountBalanceMapper.insert(accountBalance);
        return BigDecimal.ZERO;
    }

    /**
     * 更新币种余额
     * @param cusCode
     * @param currencyCode
     * @param result
     */
    @Override
    public void setCurrentBalance(String cusCode, String currencyCode, BigDecimal result) {
        LambdaUpdateWrapper<AccountBalance> lambdaUpdateWrapper=Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.eq(AccountBalance::getCusCode,cusCode);
        lambdaUpdateWrapper.eq(AccountBalance::getCurrencyCode,currencyCode);
        lambdaUpdateWrapper.set(AccountBalance::getCurrentBalance,result);
        accountBalanceMapper.update(null,lambdaUpdateWrapper);
    }

    /**
     * 提现
     * @param dto
     * @return
     */
    @Override
    public R withdraw(CustPayDTO dto) {
//        fillCustInfo(loginUser,dto);
        dto.setPayType(BillEnum.PayType.PAYMENT);
        dto.setPayMethod(BillEnum.PayMethod.WITHDRAW_PAYMENT);
        AbstractPayFactory abstractPayFactory=payFactoryBuilder.build(dto.getPayType());
        boolean flag=abstractPayFactory.updateBalance(dto);
        return flag?R.ok():R.failed();
    }

    private String getCurrencyName(String currencyCode) {
        //for test
        return "人民币";
    }

    /**
     * 填充第三方支付请求
     * @param rechargesRequestDTO
     * @param dto
     */
    private void fillRechargesRequestDTO(RechargesRequestDTO rechargesRequestDTO, CustPayDTO dto) {
        rechargesRequestDTO.setSerialNo(SnowflakeId.getNextId12());
        rechargesRequestDTO.setBankCode(dto.getBankCode());
        rechargesRequestDTO.setRemark(dto.getRemark());
        rechargesRequestDTO.setMethod(dto.getMethod());
        //set amount
        RechargesRequestAmountDTO amount= new RechargesRequestAmountDTO();
        amount.setAmount(dto.getAmount());
        amount.setCurrencyCode(dto.getCurrencyCode());
        rechargesRequestDTO.setAmount(amount);
    }
}
