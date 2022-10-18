package com.szmsd.finance.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.PreRecharge;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreRechargeDTO;
import com.szmsd.finance.mapper.PreRechargeMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IPreRechargeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        queryWrapper.orderByAsc(PreRecharge::getVerifyStatus).orderByDesc(PreRecharge::getCreateTime);
        return preRechargeMapper.listPage(queryWrapper);
    }

    @Override
    public R save(PreRechargeDTO dto) {
        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("Customer code cannot be empty");
        }

        String serialNo = generatorSerialNo();
        dto.setSerialNo(serialNo);
        PreRecharge domain= new PreRecharge();
        BeanUtils.copyProperties(dto,domain);
        String trimCode = domain.getCurrencyCode().trim();
        domain.setCurrencyCode(trimCode);
        int insert = preRechargeMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("Save Exception");
    }

    private String generatorSerialNo(){

        String currentTime = DateUtils.dateTime();
        String rNum = RandomUtil.randomNumbers(8);
        return currentTime + rNum;
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
            custPayDTO.setCurrencyCode(preRecharge.getCurrencyCode().trim());
            custPayDTO.setCurrencyName(preRecharge.getCurrencyName().trim());
            custPayDTO.setOrderTime(preRecharge.getRemittanceTime());
            custPayDTO.setNo(preRecharge.getSerialNo());
            R r = accountBalanceService.offlineIncome(custPayDTO);
            if (Constants.SUCCESS != r.getCode()) {
                return r;
            }
        }
        preRecharge.setVerifyRemark(dto.getVerifyRemark());
        preRecharge.setVerifyDate(new Date());
        preRechargeMapper.updateById(preRecharge);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R pay(PreRechargeDTO dto) {

        if(StringUtils.isEmpty(dto.getCusCode())){
            return R.failed("Customer code cannot be empty");
        }

        PreRecharge domain= new PreRecharge();
        BeanUtils.copyProperties(dto,domain);
        int insert = preRechargeMapper.insert(domain);

        if(insert > 0) {
            CustPayDTO custPayDTO = new CustPayDTO();
            custPayDTO.setAmount(dto.getAmount());
            custPayDTO.setCusCode(dto.getCusCode());
            custPayDTO.setCusName(dto.getCusName());
            custPayDTO.setCurrencyCode(dto.getCurrencyCode());
            custPayDTO.setCurrencyName(dto.getCurrencyName());
            custPayDTO.setOrderTime(dto.getRemittanceTime());
            custPayDTO.setNo(dto.getSerialNo());
            R r = accountBalanceService.offlineIncome(custPayDTO);
            if (Constants.SUCCESS != r.getCode()) {
                return r;
            }
        }

        return R.failed("异常");
    }
}
