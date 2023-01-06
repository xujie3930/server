package com.szmsd.returnex.command;

import com.mysql.cj.protocol.WatchableWriter;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundChargeData;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.Packing;
import com.szmsd.http.dto.TagSurchargeRequest;
import com.szmsd.http.dto.Weight;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ReturnExpressAutoGeneratorFeeCmd extends BasicCommand<List<String>> {

    private List<String> orderNoList;

    public ReturnExpressAutoGeneratorFeeCmd(List<String> orderNoList){
        this.orderNoList = orderNoList;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected List<String> doExecute() throws Exception {

        DelOutboundClientService delOutboundFeignService = SpringUtils.getBean(DelOutboundClientService.class);

        //step 2.根据退件费单据查询最新的PRC费用、查询出库单基本信息
        List<DelOutboundChargeData> chargeRs = delOutboundFeignService.findDelboundCharges(orderNoList);

        if(CollectionUtils.isEmpty(chargeRs)){
            throw new RuntimeException("无法获取退件费单据查询最新的PRC费用、查询出库单基本信息");
        }

        for(DelOutboundChargeData data : chargeRs) {

            //step 3.计算包裹的特定附加费用
            IHtpPricedProductClientService iHtpPricedProductClientService = SpringUtils.getBean(IHtpPricedProductClientService.class);
            TagSurchargeRequest tagSurchargeRequest = this.generatorTagSurcharge(data);
            ChargeWrapper chargeWrapper = iHtpPricedProductClientService.tagSurcharge(tagSurchargeRequest);

            if(chargeWrapper == null){
                throw new RuntimeException("单号："+data.getOrderNo()+",计算包裹的特定附加费用异常");
            }

            //step 4.调用财务扣费接口

            //step 5.回写退费件数据
        }

        return null;
    }

    private TagSurchargeRequest generatorTagSurcharge(DelOutboundChargeData data) {

        TagSurchargeRequest tagSurchargeRequest = new TagSurchargeRequest();

        tagSurchargeRequest.setTags(Arrays.asList("退件费"));
        tagSurchargeRequest.setGrade(data.getGrade());
        tagSurchargeRequest.setFinalPricingProductCode(data.getPrcInterfaceProductCode());
        tagSurchargeRequest.setProductCode(data.getPrcInterfaceProductCode());
        Packing packing = new Packing();
        packing.setLength(toBigDecimal(data.getLength()));
        packing.setWidth(toBigDecimal(data.getWidth()));
        packing.setHeight(toBigDecimal(data.getHeight()));
        packing.setLengthUnit("cm");
        tagSurchargeRequest.setPacking(packing);
        Weight weight = new Weight();
        weight.setValue(data.getCalcWeight());
        weight.setUnit(data.getCalcWeightUnit());
        tagSurchargeRequest.setCalcWeight(weight);
        Weight acweight = new Weight();
        acweight.setUnit("g");
        acweight.setValue(toBigDecimal(data.getWeight()));
        tagSurchargeRequest.setActualWeight(acweight);
        tagSurchargeRequest.setZoneName(data.getZoneName());
        tagSurchargeRequest.setClientCode(data.getCustomCode());

        return tagSurchargeRequest;
    }

    private BigDecimal toBigDecimal(Double d){
        if(d == null){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(d);
    }

    @Override
    protected void afterExecuted(List<String> result) throws Exception {
        super.afterExecuted(result);
    }

    @Override
    protected void rollback(String errorMsg) {
        super.rollback(errorMsg);
    }
}
