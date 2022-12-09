package com.szmsd.finance.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.dto.ExchangeRateDTO;
import com.szmsd.finance.handler.ExchangeRateExcelListener;
import com.szmsd.finance.mapper.ExchangeRateLogMapper;
import com.szmsd.finance.mapper.ExchangeRateMapper;
import com.szmsd.finance.service.IExchangeRateService;
import com.szmsd.finance.util.ExcelFile;
import com.szmsd.finance.vo.ExchangeRateExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author liulei
 */
@Service
@Slf4j
public class ExchangeRateServiceImpl implements IExchangeRateService  {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private BasSubFeignPluginService basSubFeignPluginService;

    @Autowired
    private ExchangeRateLogMapper exchangeRateLogMapper;

    @Override
    public List<ExchangeRate> listPage(ExchangeRateDTO dto) {

        LambdaQueryWrapper<ExchangeRate> queryWrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(dto.getExchangeFromCode())) {
            queryWrapper.eq(ExchangeRate::getExchangeFromCode, dto.getExchangeFromCode());
        }
        if(StringUtils.isNotEmpty(dto.getExchangeToCode())) {
            queryWrapper.eq(ExchangeRate::getExchangeToCode,dto.getExchangeToCode());
        }
//        queryWrapper.and(v -> v.gt(ExchangeRate::getExpireTime,new Date()).or().isNull(ExchangeRate::getExpireTime));
//        queryWrapper.orderByDesc(ExchangeRate::getCreateTime);

        List<ExchangeRate> list=exchangeRateMapper.listPage(queryWrapper);
        list.forEach(x-> {
            if (dto.getLen().equals("en")) {
                x.setExchangeFrom(x.getExchangeFromCode());
                x.setExchangeTo(x.getExchangeToCode());
            } else if (dto.getLen().equals("zh")) {
                x.setExchangeFrom(x.getExchangeFrom());
                x.setExchangeTo(x.getExchangeTo());
            }
        });

        return list;
    }

    @Override
    public R save(ExchangeRateDTO dto) {
        if(dto.getRate()==null){
            return R.failed("Exchange rate cannot be blank");
        }
        if(!checkExchangeRateIsExistsadd(dto,0)){
            return R.failed("Currency is blank or the exchange rate has been maintained");
        }
        ExchangeRate domain= new ExchangeRate();
        BeanUtils.copyProperties(dto,domain);
        int insert = exchangeRateMapper.insert(domain);
        if(insert>0){
            return R.ok();
        }
        return R.failed("Save Exception");
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

    private boolean checkExchangeRateIsExistsadd(ExchangeRateDTO dto, int count) {
        String currencyFromCode = dto.getExchangeFromCode();
        String currencyToCode = dto.getExchangeToCode();
        if(currencyFromCode==null||currencyToCode==null){
            return false;
        }
        List<ExchangeRate> list=exchangeRateMapper.checkExchangeRateIsExistsadd(dto);
        return list.size()<=count;
    }

    @Override
    public R update(ExchangeRateDTO dto) {
        if(!checkExchangeRateIsExists(dto,1)){
            return R.failed("Currency is blank or the exchange rate has been maintained");
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
        /*List<ExchangeRate> exchangeRates=exchangeRateMapper.selectList(new QueryWrapper<ExchangeRate>().lambda().gt(ExchangeRate::getExpireTime,new Date()).or().isNull(ExchangeRate::getExpireTime));
        RateCalculateUtil rateCalculateUtil = RateCalculateUtil.buildRateTree(currencyFromCode, currencyToCode, exchangeRates);
        BigDecimal fromToRate = rateCalculateUtil.getFromToRate();
        if(fromToRate!=null){
            return R.ok(fromToRate);
        }*/
        return R.failed("Exchange rate exchange of corresponding currency is not found");
    }

    @Override
    public List<ExchangeRateDTO> selectRates(Map map) {
        return exchangeRateMapper.selectRates(map);
    }

    @Override
    public int insertExchangeRate(List<Map> mapList) {
        log.info("传入参数：{}",mapList);
        JSONArray jsonArray = new JSONArray();
        log.info("放入jsonArray：{}");
        jsonArray.addAll(mapList);
        log.info("转换jsonArray：{}",jsonArray);
        List<ExchangeRate> list = jsonArray.toJavaList(ExchangeRate.class);
        log.info("转换list：{}",list);
        Set<ExchangeRate> set=new HashSet<ExchangeRate>();
        log.info("开始转换set：{}",set);
        set.addAll(list);
        log.info("转换后set：{}",set);
        set.forEach(x->{
             exchangeRateMapper.insert(x);

        });
       return 1;
    }

    @Override
    public void deleteExchangeRate(Map map) {
       exchangeRateMapper.deleteExchangeRate(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R uploadExchangeRate(MultipartFile file) {

        if(file == null){
            return R.failed("请上传文件");
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();

        //把字段名和导入数据匹配对应 存入数据库
        try {

            EasyExcel.read(
                    file.getInputStream(),
                    ExchangeRateExcelVO.class,
                    new ExchangeRateExcelListener(basSubFeignPluginService,exchangeRateMapper,exchangeRateLogMapper,loginUser)
            ).sheet().doRead();

            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导入汇率异常:{}",e.getMessage());
            return R.failed(e.getMessage());
        }
    }

}
