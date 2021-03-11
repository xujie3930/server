package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.FssExchangeRate;
import com.szmsd.finance.dto.FssExchangeRateDTO;
import com.szmsd.finance.mapper.ExchangeRateMapper;
import com.szmsd.finance.service.IExchangeRateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liulei
 */
@Service
public class ExchangeRateServiceImpl implements IExchangeRateService {
    @Autowired
    ExchangeRateMapper exchangeRateMapper;
    @Override
    public List<FssExchangeRate> listPage(FssExchangeRateDTO dto) {
        LambdaQueryWrapper<FssExchangeRate> queryWrapper = Wrappers.lambdaQuery();
        if(dto.getExchangeFromId()!=null) {
            queryWrapper.eq(FssExchangeRate::getExchangeFromId, dto.getExchangeFromId());
        }
        if(dto.getExchangeToId()!=null){
            queryWrapper.eq(FssExchangeRate::getExchangeToId,dto.getExchangeToId());
        }
        return exchangeRateMapper.listPage(queryWrapper);
    }

    @Override
    public R save(FssExchangeRateDTO dto) {
        FssExchangeRate domain= new FssExchangeRate();
        if(!checkExchangeRateIsExists(dto)){
            return R.failed("币种为空或者该汇率已维护");
        }
        BeanUtils.copyProperties(dto,domain);
        int insert = exchangeRateMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("保存异常");
    }

    private boolean checkExchangeRateIsExists(FssExchangeRateDTO dto) {
        Long exchangeFromId = dto.getExchangeFromId();
        Long exchangeToId = dto.getExchangeToId();
        if(exchangeFromId==null||exchangeToId==null){
            return false;
        }
        List<FssExchangeRate> list=exchangeRateMapper.checkExchangeRateIsExists(exchangeFromId,exchangeToId);
        return list.size()>0;
    }

    @Override
    public R update(FssExchangeRateDTO dto) {
        return null;
    }
}
