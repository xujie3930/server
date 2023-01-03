package com.szmsd.delivery.command;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.support.Context;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.ChargePricingOrderMsgDto;
import com.szmsd.delivery.enums.ChargeImportStateEnum;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.finance.api.feign.ConvertUnitFeignService;
import com.szmsd.finance.domain.FssConvertUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ChargeUpdateOutboundCmd extends BasicCommand<List<String>> {

    private List<ChargeImport> chargeImportList;

    public ChargeUpdateOutboundCmd(List<ChargeImport> chargeImportList){
        this.chargeImportList = chargeImportList;
    }

    @Override
    protected void beforeDoExecute() {
        if(CollectionUtils.isEmpty(chargeImportList)){
            throw new RuntimeException("无数据");
        }
    }

    @Override
    protected List<String> doExecute() throws Exception {

        List<DelOutbound> delOutbounds = new ArrayList<>();
        ConvertUnitFeignService convertUnitFeignService = Context.getBean(ConvertUnitFeignService.class);

        R<List<FssConvertUnit>> convertUnitRs = convertUnitFeignService.findAll();

        if(convertUnitRs == null || convertUnitRs.getCode() != Constants.SUCCESS){
            throw new RuntimeException("无法获取单位换算关系基础信息");
        }

        List<FssConvertUnit> fssConvertUnitList = convertUnitRs.getData();

        if(CollectionUtils.isEmpty(fssConvertUnitList)){
            throw new CommonException("400", "ConvertUnit数据为空");
        }

        Map<String,FssConvertUnit> fssConvertUnitMap = fssConvertUnitList.stream().collect(Collectors.toMap(FssConvertUnit::getCalcUnit, v->v));

        for (ChargeImport chargeImport : chargeImportList) {

            DelOutbound delOutbound = new DelOutbound();
            delOutbound.setOrderNo(chargeImport.getOrderNo());
            delOutbound.setLength(chargeImport.getLength().doubleValue());
            delOutbound.setWeight(chargeImport.getWeight().doubleValue());
            delOutbound.setWidth(chargeImport.getWidth().doubleValue());
            delOutbound.setSpecifications(chargeImport.getSpecifications());
            delOutbound.setHeight(chargeImport.getHeight().doubleValue());

            FssConvertUnit fssConvertUnit = fssConvertUnitMap.get(chargeImport.getWeightUnit());

            if(fssConvertUnit != null){
                BigDecimal convertValue = fssConvertUnit.getConvertValue();
                BigDecimal packcalcWeight = BigDecimalUtil.setScale(chargeImport.getCalcWeight().multiply(convertValue));
                delOutbound.setCalcWeight(packcalcWeight);
                delOutbound.setCalcWeightUnit(fssConvertUnit.getConvertUnit());
            }

            delOutbound.setSheetCode(chargeImport.getQuotationNo());

            delOutbounds.add(delOutbound);
        }

        DelOutboundMapper delOutboundMapper = SpringUtils.getBean(DelOutboundMapper.class);

        if (CollectionUtils.isNotEmpty(delOutbounds)){

            log.info("ChargeUpdateOutboundCmd 更新单据：{}", JSON.toJSONString(delOutbounds));
            int updBatch = delOutboundMapper.updateDeloutByOrder(delOutbounds);
            log.info("ChargeUpdateOutboundCmd updBatch：{}", updBatch);
        }

        List<String> orders = delOutbounds.stream().map(DelOutbound::getOrderNo).distinct().collect(Collectors.toList());

        return orders;
    }

    @Override
    protected void afterExecuted(List<String> result) throws Exception {

        ChargeImportMapper chargeImportMapper = SpringUtils.getBean(ChargeImportMapper.class);
        List<ChargePricingOrderMsgDto> allData = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(result)) {
            for (String s : result) {
                ChargePricingOrderMsgDto chargePricingOrderMsgDto = new ChargePricingOrderMsgDto();
                chargePricingOrderMsgDto.setState(ChargeImportStateEnum.UPDATE_ORDER.getCode());
                chargePricingOrderMsgDto.setOrderNo(s);
                allData.add(chargePricingOrderMsgDto);
            }
            chargeImportMapper.batchUpd(allData);
        }

        super.afterExecuted(result);
    }
}
