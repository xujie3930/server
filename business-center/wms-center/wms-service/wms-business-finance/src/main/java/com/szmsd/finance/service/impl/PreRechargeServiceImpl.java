package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.PreRecharge;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreRechargeDTO;
import com.szmsd.finance.mapper.PreRechargeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IPreRechargeService;
import com.szmsd.finance.util.SnowflakeId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class PreRechargeServiceImpl implements IPreRechargeService {

    @Autowired
    PreRechargeMapper preRechargeMapper;

    @Autowired
    IAccountBalanceService accountBalanceService;

    @Override
    public List<PreRecharge> listPage(PreRechargeDTO dto) {
        LambdaQueryWrapper<PreRecharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.isNotNull(PreRecharge::getCusCode);
        if(dto.getCusId()!=null) {
            queryWrapper.eq(PreRecharge::getCusId, dto.getCusId());
        }
        if(StringUtils.isNotEmpty(dto.getCusCode())){
            queryWrapper.eq(PreRecharge::getCusCode,dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getVerifyStatus())){
            queryWrapper.eq(PreRecharge::getVerifyStatus,dto.getVerifyStatus());
        }
        if(StringUtils.isNotEmpty(dto.getRemittanceMethod())){
            queryWrapper.eq(PreRecharge::getRemittanceMethod,dto.getRemittanceMethod());
        }
        if(StringUtils.isNotEmpty(dto.getCurrencyCode())){
            queryWrapper.eq(PreRecharge::getCurrencyCode,dto.getCurrencyCode());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(PreRecharge::getRemittanceTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(PreRecharge::getRemittanceTime,dto.getEndTime());
        }
        queryWrapper.orderByDesc(PreRecharge::getCreateTime);
        return preRechargeMapper.listPage(queryWrapper);
    }

    @Override
    public R save(PreRechargeDTO dto) {
        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("客户编码不能为空");
        }
        dto.setSerialNo(SnowflakeId.getNextId12());
        PreRecharge domain= new PreRecharge();
        BeanUtils.copyProperties(dto,domain);
        int insert = preRechargeMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("保存异常");
    }

    @Override
    public R audit(PreRechargeAuditDTO dto) {
        PreRecharge preRecharge = preRechargeMapper.selectById(dto.getId());
        preRecharge.setVerifyStatus(dto.getVerifyStatus());
        if("1".equals(dto.getVerifyStatus())){
            CustPayDTO custPayDTO=new CustPayDTO();
            custPayDTO.setAmount(preRecharge.getAmount());
            custPayDTO.setCusCode(preRecharge.getCusCode());
            custPayDTO.setCusName(preRecharge.getCusName());
            custPayDTO.setCurrencyCode(preRecharge.getCurrencyCode());
            custPayDTO.setCurrencyName(preRecharge.getCurrencyName());
            accountBalanceService.offlineIncome(custPayDTO);
        }
        preRecharge.setVerifyRemark(dto.getVerifyRemark());
        preRecharge.setVerifyDate(new Date());
        preRechargeMapper.updateById(preRecharge);
        return R.ok();
    }
}
