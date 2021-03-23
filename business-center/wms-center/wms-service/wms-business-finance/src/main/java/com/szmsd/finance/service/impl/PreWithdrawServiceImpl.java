package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.PreWithdraw;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreWithdrawDTO;
import com.szmsd.finance.mapper.PreWithdrawMapper;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IPreWithdrawService;
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
public class PreWithdrawServiceImpl implements IPreWithdrawService {
    @Autowired
    IAccountBalanceService accountBalanceService;

    @Autowired
    PreWithdrawMapper preWithdrawMapper;

    @Override
    public List<PreWithdraw> listPage(PreWithdrawDTO dto) {
        LambdaQueryWrapper<PreWithdraw> queryWrapper = Wrappers.lambdaQuery();
        if(dto.getCusCode()!=null) {
            queryWrapper.eq(PreWithdraw::getCusCode, dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getCusCode())){
            queryWrapper.ge(PreWithdraw::getCusCode,dto.getCusCode());
        }
        if(StringUtils.isNotEmpty(dto.getBeginTime())){
            queryWrapper.ge(PreWithdraw::getCreateTime,dto.getBeginTime());
        }
        if(StringUtils.isNotEmpty(dto.getEndTime())){
            queryWrapper.le(PreWithdraw::getCreateTime,dto.getEndTime());
        }
        return preWithdrawMapper.listPage(queryWrapper);
    }

    @Override
    public R save(PreWithdrawDTO dto) {
        dto.setSerialNo(SnowflakeId.getNextId12());
        PreWithdraw domain= new PreWithdraw();
        BeanUtils.copyProperties(dto,domain);
        int insert = preWithdrawMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("保存异常");
    }

    @Override
    public R audit(PreRechargeAuditDTO dto) {
        PreWithdraw preWithdraw = preWithdrawMapper.selectById(dto.getId());
        preWithdraw.setVerifyStatus(dto.getVerifyStatus());
        if("1".equals(dto.getVerifyStatus())){
            CustPayDTO custPayDTO=new CustPayDTO();
            custPayDTO.setAmount(preWithdraw.getAmount());
            custPayDTO.setCusCode(preWithdraw.getCusCode());
            custPayDTO.setCusName(preWithdraw.getCusName());
            custPayDTO.setCurrencyCode(preWithdraw.getCurrencyCode());
            custPayDTO.setCurrencyName(preWithdraw.getCurrencyName());
            accountBalanceService.withdraw(custPayDTO);
        }
        preWithdraw.setVerifyRemark(dto.getVerifyRemark());
        preWithdraw.setVerifyDate(new Date());
        preWithdrawMapper.updateById(preWithdraw);
        return R.ok();
    }
}
