package com.szmsd.delivery.command;

import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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

        for(ChargeImport chargeImport : chargeImportList){

            DelOutbound delOutbound = new DelOutbound();
            delOutbound.setOrderNo(chargeImport.getOrderNo());
            delOutbound.setLength(chargeImport.getLength().doubleValue());
            delOutbound.setWeight(chargeImport.getWeight().doubleValue());
            delOutbound.setWidth(chargeImport.getWidth().doubleValue());
            delOutbound.setSpecifications(chargeImport.getSpecifications());
            delOutbound.setHeight(chargeImport.getHeight().doubleValue());
            delOutbound.setCalcWeight(chargeImport.getCalcWeight());
            delOutbound.setCalcWeightUnit(chargeImport.getWeightUnit());

            delOutbounds.add(delOutbound);
        }

        DelOutboundMapper delOutboundMapper = SpringUtils.getBean(DelOutboundMapper.class);

        int updBatch = delOutboundMapper.updateDeloutByOrder(delOutbounds);

        if(updBatch == 0){
            return null;
        }

        List<String> orders = delOutbounds.stream().map(DelOutbound::getOrderNo).collect(Collectors.toList());

        return orders;
    }
}
