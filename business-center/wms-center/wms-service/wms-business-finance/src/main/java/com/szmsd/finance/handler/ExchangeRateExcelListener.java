package com.szmsd.finance.handler;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.plugin.service.BasSubFeignPluginService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.domain.ExchangeRate;
import com.szmsd.finance.mapper.ExchangeRateMapper;
import com.szmsd.finance.vo.ExchangeRateExcelVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ExchangeRateExcelListener extends AnalysisEventListener<ExchangeRateExcelVO> {

    private List<ExchangeRateExcelVO> exchangeRateExcels = new ArrayList<>();

    private BasSubFeignPluginService basSubFeignPluginService;

    private ExchangeRateMapper exchangeRateMapper;

    private LoginUser loginUser;

    public ExchangeRateExcelListener(BasSubFeignPluginService basSubFeignPluginService,ExchangeRateMapper exchangeRateMapper,LoginUser loginUser){
        this.basSubFeignPluginService = basSubFeignPluginService;
        this.exchangeRateMapper = exchangeRateMapper;
        this.loginUser = loginUser;
    }

    @Override
    public void invoke(ExchangeRateExcelVO data, AnalysisContext context) {

        if(data != null){
            exchangeRateExcels.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        if(CollectionUtils.isEmpty(exchangeRateExcels)){
            throw new RuntimeException("无法读取excel表格汇率信息");
        }

        //组合数据
        List<ExchangeRate> exchangeRates = this.generatorExchangeRate();

        //根据from  to 去重复
        List<ExchangeRate> exchangeRateList = exchangeRates.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o->o.getExchangeFromCode()+";"+o.getExchangeToCode()))), ArrayList::new));

        List<ExchangeRate> removeExchangeRate = new ArrayList<>();

        for(ExchangeRate exchangeRate : exchangeRateList){

            List<ExchangeRate> hasExchangeRates = exchangeRateMapper.selectList(Wrappers.<ExchangeRate>query().lambda()
                    .eq(ExchangeRate::getExchangeFromCode,exchangeRate.getExchangeFromCode())
                    .eq(ExchangeRate::getExchangeToCode,exchangeRate.getExchangeToCode())
            );

            if(CollectionUtils.isNotEmpty(hasExchangeRates)){
                removeExchangeRate.addAll(hasExchangeRates);
            }
        }

    }


    private List<ExchangeRate> generatorExchangeRate(){

        R<Map<String, List<BasSubWrapperVO>>> basSubCurrencyRs = basSubFeignPluginService.getSub("008");

        if(!Constants.SUCCESS.equals(basSubCurrencyRs.getCode())){
            throw  new RuntimeException("无法获取币种信息");
        }

        List<BasSubWrapperVO> baslist = basSubCurrencyRs.getData().get("008");

        if(CollectionUtils.isEmpty(baslist)){
            throw  new RuntimeException("无法获取币种信息");
        }

        //map<币种名称,>
        Map<String,BasSubWrapperVO> basSubWrapperNameEnVOMap = baslist.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubNameEn,v->v));

        Map<String,BasSubWrapperVO> basSubWrapperNameVOMap = baslist.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubName,v->v));

        //map<币种代码,>
        Map<String,BasSubWrapperVO> basSubWrapperCodeVOMap = baslist.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubCode,v->v));

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        for(ExchangeRateExcelVO exchangeRateExcelVO : exchangeRateExcels){

            String strExpireTime = exchangeRateExcelVO.getExpireTime();
            String exchangeFrom = exchangeRateExcelVO.getExchangeFrom();
            String exchangeTo = exchangeRateExcelVO.getExchangeTo();

            if(StringUtils.isEmpty(strExpireTime)){
                throw new RuntimeException("失效时间不允许为空");
            }

            if(StringUtils.isEmpty(exchangeFrom) || StringUtils.isEmpty(exchangeTo)){
                throw new RuntimeException("原币种 或 转换币种不允许为空");
            }

            if(exchangeRateExcelVO.getRate() == null){
                throw new RuntimeException("汇率不允许为空");
            }

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setExchangeFrom(exchangeFrom);
            exchangeRate.setExchangeTo(exchangeTo);

            Date expireTime = DateUtils.dateTime(strExpireTime + " 23:59:59",DateUtils.YYYY_MM_DD_HH_MM_SS);

            exchangeRate.setExpireTime(expireTime);
            exchangeRate.setRate(exchangeRateExcelVO.getRate());

            //中文币种
            BasSubWrapperVO basSubWrapperVONameEn = basSubWrapperNameEnVOMap.get(exchangeFrom);
            BasSubWrapperVO basSubWrapperVOName = basSubWrapperNameVOMap.get(exchangeFrom);
            BasSubWrapperVO basSubCodeWrapperVO = basSubWrapperCodeVOMap.get(exchangeFrom);
            if(basSubWrapperVONameEn != null){
                exchangeRate.setExchangeFromCode(basSubWrapperVONameEn.getSubValue());
            }else{
                if(basSubWrapperVOName != null){
                    exchangeRate.setExchangeFromCode(basSubWrapperVOName.getSubValue());
                }
            }

            //英文币种
            if(StringUtils.isEmpty(exchangeRate.getExchangeFromCode())){
                if(basSubCodeWrapperVO != null){
                    exchangeRate.setExchangeFromCode(basSubCodeWrapperVO.getSubValue());
                }
            }

            BasSubWrapperVO basSubWrapperExchangeTo = basSubWrapperNameEnVOMap.get(exchangeTo);
            BasSubWrapperVO basSubWrapperExchangeName = basSubWrapperNameVOMap.get(exchangeTo);
            BasSubWrapperVO basSubCodeWrapperVOTo = basSubWrapperCodeVOMap.get(exchangeTo);
            if(basSubWrapperExchangeTo != null){
                exchangeRate.setExchangeToCode(basSubWrapperExchangeTo.getSubValue());
            }else{
                if(basSubWrapperExchangeName != null){
                    exchangeRate.setExchangeToCode(basSubWrapperExchangeName.getSubValue());
                }
            }

            if(StringUtils.isEmpty(exchangeRate.getExchangeToCode())){
                if(basSubCodeWrapperVOTo != null){
                    exchangeRate.setExchangeToCode(basSubCodeWrapperVOTo.getSubValue());
                }
            }

            exchangeRate.setCreateTime(new Date());
            exchangeRate.setRemark(exchangeRateExcelVO.getRemark());
            exchangeRate.setCreateBy(loginUser.getSellerCode());
            exchangeRate.setCreateByName(loginUser.getUsername());

            exchangeRates.add(exchangeRate);
        }

        return exchangeRates;
    }
}
