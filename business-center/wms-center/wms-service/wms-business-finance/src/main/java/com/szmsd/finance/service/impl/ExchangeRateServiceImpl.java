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
public class ExchangeRateServiceImpl implements IExchangeRateService {

    @Autowired
    private ExchangeRateMapper exchangeRateMapper;

    @Autowired
    private BasSubFeignPluginService basSubFeignPluginService;

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
        return exchangeRateMapper.listPage(queryWrapper);
    }

    @Override
    public R save(ExchangeRateDTO dto) {
        if(dto.getRate()==null){
            return R.failed("Exchange rate cannot be blank");
        }
        if(!checkExchangeRateIsExists(dto,0)){
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

        //定义参数
        ArrayList<String> addList = new ArrayList<>();
        Collections.addAll(addList,"exchangeFrom","exchangeTo","rate","expireTime","remark");

        LoginUser loginUser = SecurityUtils.getLoginUser();

        //把字段名和导入数据匹配对应 存入数据库
        try {

            EasyExcel.read(
                    file.getInputStream(),
                    ExchangeRateExcelVO.class,
                    new ExchangeRateExcelListener(basSubFeignPluginService,exchangeRateMapper,loginUser)
            ).sheet().doRead();

//            R<Map<String, List<BasSubWrapperVO>>> basSubCurrencyRs = basSubFeignPluginService.getSub("008");
//
//            if(!Constants.SUCCESS.equals(basSubCurrencyRs.getCode())){
//                return R.failed("无法获取币种信息");
//            }
//
//            List<BasSubWrapperVO>  baslist = basSubCurrencyRs.getData().get("008");
//
//            log.info("导入调用");
//            //汇率专用  解析excel数据
//            List<Map> mapList = ExcelFile.getExcelDataFinance(file,addList);
//
//            log.info("导入参数：{}",mapList);
//            for (int x=0;x < mapList.size();x++) {
//
//                if (String.valueOf(mapList.get(x).get("exchangeFrom")).equals("") || String.valueOf(mapList.get(x).get("exchangeTo")).equals("") || String.valueOf(mapList.get(x).get("rate")).equals("") || String.valueOf(mapList.get(x).get("expireTime")).equals("")) {
//                    throw new BaseException("第" + (x + 2) + "行的导入数据需要填写必填项，必填项为（原币别，现币别，比率，失效时间）");
//                }
//                String expireTimes=String.valueOf(mapList.get(x).get("expireTime"));
//                String str = expireTimes.substring(0, 10);
//                String expireTime=str+" "+"23:59:59";
//                mapList.get(x).put("expireTime",expireTime);
//                for (int i = 0; i < baslist.size(); i++) {
//                    //根据中英文匹配现有的货币code
//                    if (String.valueOf(mapList.get(x).get("exchangeFrom")).equals(baslist.get(i).getSubName()) || String.valueOf(mapList.get(x).get("exchangeFrom")).equals(baslist.get(i).getSubNameEn())) {
//                        mapList.get(x).put("exchangeFromCode", baslist.get(i).getSubValue());
//                    }
//                    if (String.valueOf(mapList.get(x).get("exchangeTo")).equals(baslist.get(i).getSubName()) || String.valueOf(mapList.get(x).get("exchangeTo")).equals(baslist.get(i).getSubNameEn())) {
//                        mapList.get(x).put("exchangeToCode", baslist.get(i).getSubValue());
//                    }
//                }
//
//                //验证数据库是否存在(需针对原币别+现币别+失败时间，进行唯一判断)
//                List<ExchangeRateDTO> list = this.selectRates(mapList.get(x));
//                if (list.size()>0){
//                    this.deleteExchangeRate(mapList.get(x));
//                }
//            }
//
//            this.insertExchangeRate(mapList);
            return R.ok("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(((BaseException) e).getDefaultMessage());
        }
    }

}
