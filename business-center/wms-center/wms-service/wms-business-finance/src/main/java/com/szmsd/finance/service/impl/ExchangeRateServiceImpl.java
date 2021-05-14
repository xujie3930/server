package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import com.szmsd.finance.mapper.ExchangeRateMapper;
import com.szmsd.finance.service.IExchangeRateService;
import com.szmsd.finance.util.RateCalculateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class ExchangeRateServiceImpl implements IExchangeRateService {

    @Autowired
    ExchangeRateMapper exchangeRateMapper;

    @Override
    public List<ExchangeRate> listPage(ExchangeRateDTO dto) {
        LambdaQueryWrapper<ExchangeRate> queryWrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(dto.getExchangeFromCode())) {
            queryWrapper.eq(ExchangeRate::getExchangeFromCode, dto.getExchangeFromCode());
        }
        if(StringUtils.isNotEmpty(dto.getExchangeToCode())) {
            queryWrapper.eq(ExchangeRate::getExchangeToCode,dto.getExchangeToCode());
        }
//        queryWrapper.orderByDesc(ExchangeRate::getCreateTime);
        return exchangeRateMapper.listPage(queryWrapper);
    }

    @Override
    public R save(ExchangeRateDTO dto) {
        if(dto.getRate()==null){
            return R.failed("汇率不能为空");
        }
        if(!checkExchangeRateIsExists(dto,0)){
            return R.failed("币种为空或者该汇率已维护");
        }
        ExchangeRate domain= new ExchangeRate();
        BeanUtils.copyProperties(dto,domain);
        int insert = exchangeRateMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("保存异常");
    }

    private boolean checkExchangeRateIsExists(ExchangeRateDTO dto, int count) {
        String currencyFromCode = dto.getExchangeFromCode();
        String currencyToCode = dto.getExchangeToCode();
        if(currencyFromCode==null||currencyToCode==null){
            return false;
        }
        List<ExchangeRate> list=exchangeRateMapper.checkExchangeRateIsExists(dto);
        return list.size()<=count;
    }

    @Override
    public R update(ExchangeRateDTO dto) {
        if(!checkExchangeRateIsExists(dto,1)){
            return R.failed("币种为空或者该汇率已维护");
        }
        ExchangeRate fer=new ExchangeRate();
        BeanUtils.copyProperties(dto,fer);
        exchangeRateMapper.updateById(fer);
        return R.ok();
    }

    @Override
    public R delete(Long id) {
        exchangeRateMapper.deleteById(id);
        return R.ok();
    }

    @Override
    public R selectRate(String currencyFromCode, String currencyToCode) {
        ExchangeRate rate=exchangeRateMapper.selectOne(new QueryWrapper<ExchangeRate>().lambda()
                .eq(ExchangeRate::getExchangeFromCode,currencyFromCode)
                .eq(ExchangeRate::getExchangeToCode,currencyToCode)
                .and(v -> v.gt(ExchangeRate::getExpireTime,new Date()).or().isNull(ExchangeRate::getExpireTime)));
        if(rate!=null){
            return R.ok(rate.getRate());
        }
        rate=exchangeRateMapper.selectOne(new QueryWrapper<ExchangeRate>().lambda()
                .eq(ExchangeRate::getExchangeFromCode,currencyToCode)
                .eq(ExchangeRate::getExchangeToCode,currencyFromCode)
                .and(v -> v.gt(ExchangeRate::getExpireTime,new Date()).or().isNull(ExchangeRate::getExpireTime)));
        if(rate!=null){
            return R.ok(BigDecimal.ONE.divide(rate.getRate(),4,BigDecimal.ROUND_FLOOR));
        }
        //尝试递归查询汇率
        List<ExchangeRate> exchangeRates=exchangeRateMapper.selectList(new QueryWrapper<ExchangeRate>().lambda().gt(ExchangeRate::getExpireTime,new Date()).or().isNull(ExchangeRate::getExpireTime));
        RateCalculateUtil rateCalculateUtil = RateCalculateUtil.buildRateTree(currencyFromCode, currencyToCode, exchangeRates);
        BigDecimal fromToRate = rateCalculateUtil.getFromToRate();
        if(fromToRate!=null){
            return R.ok(fromToRate);
        }
        return R.failed("未查询到对应币种的汇率交换");
    }

}
