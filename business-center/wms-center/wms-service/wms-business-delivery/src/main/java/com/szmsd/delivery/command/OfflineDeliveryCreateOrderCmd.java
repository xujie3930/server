package com.szmsd.delivery.command;

import com.google.common.collect.Lists;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.support.Context;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineCostImportMapper;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundService;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class OfflineDeliveryCreateOrderCmd extends BasicCommand<OfflineResultDto> {

    private OfflineResultDto offlineResultDto;

    public OfflineDeliveryCreateOrderCmd(OfflineResultDto offlineResultDto){
        this.offlineResultDto = offlineResultDto;
    }

    @Override
    protected void beforeDoExecute() {
        if(CollectionUtils.isEmpty(offlineResultDto.getOfflineDeliveryImports())){
            throw new RuntimeException("参数异常无法生成订单");
        }
    }

    @Override
    protected OfflineResultDto doExecute() throws Exception {

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<OfflineCostImport> offlineCostImports = offlineResultDto.getOfflineCostImportList();

        Map<String,List<OfflineCostImport>> offCostMap = offlineCostImports.stream().collect(Collectors.groupingBy(OfflineCostImport::getTrackingNo));

        List<DelOutbound> delOutbounds = new ArrayList<>();
        List<DelOutboundAddress> delOutboundAddresses = new ArrayList<>();
        List<OfflineImportDto> updateData = new ArrayList<>();
        SerialNumberClientService serialNumberClientService = SpringUtils.getBean(SerialNumberClientService.class);

        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            DelOutbound delOutbound = this.generatorDeloutbond(deliveryImport);
            DelOutboundAddress delOutboundAddress = this.generatorOutAddress(deliveryImport);
            OfflineImportDto offlineImportDto = new OfflineImportDto();

            String trackIngNo = deliveryImport.getTrackingNo();
            List<OfflineCostImport> offlineCostImportList = offCostMap.get(trackIngNo);

            StringBuilder currencyDescribe = new StringBuilder();
            Map<String,List<OfflineCostImport>> offlineCostImportMap = offlineCostImportList.stream().collect(Collectors.groupingBy(OfflineCostImport::getCurrencyCode));

            Iterator<Map.Entry<String,List<OfflineCostImport>>> iter = offlineCostImportMap.entrySet().iterator();

            while(iter.hasNext()){

                Map.Entry<String,List<OfflineCostImport>> it = iter.next();
                List<OfflineCostImport> offlineCostImports1 = it.getValue();

                BigDecimal amount = BigDecimal.ZERO;
                for(OfflineCostImport offlineCostImport1 : offlineCostImports1){
                    amount = amount.add(offlineCostImport1.getAmount());
                }
                currencyDescribe.append(BigDecimalUtil.setScale(amount,2));
                currencyDescribe.append(it.getKey());
                currencyDescribe.append(";");
            }

            //生产单号
            String serNumber = serialNumberClientService.generatorNumber(SerialNumberConstant.DEL_OUTBOUND_NO);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CK");
            stringBuilder.append(deliveryImport.getSellerCode());
            stringBuilder.append(serNumber);
            String orderNo = stringBuilder.toString();

            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.CREATE_ORDER.getCode());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setOrderNo(orderNo);
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());

            deliveryImport.setOrderNo(orderNo);
            delOutbound.setOrderNo(orderNo);
            delOutbound.setCurrencyDescribe(currencyDescribe.toString());
            delOutboundAddress.setOrderNo(orderNo);
            delOutbounds.add(delOutbound);
            delOutboundAddresses.add(delOutboundAddress);
            updateData.add(offlineImportDto);
        }

        if(CollectionUtils.isEmpty(delOutbounds) || CollectionUtils.isEmpty(delOutboundAddresses)){
            return null;
        }

        List<DelOutbound> newdelOutbounds = delOutbounds.stream().collect(Collectors.collectingAndThen(Collectors
                        .toCollection(()->new TreeSet<>(Comparator.comparing(DelOutbound::getTrackingNo))), ArrayList::new));


        //List<List<DelOutbound>> delOutboundsps = Lists.partition(delOutbounds,100);

        //List<List<DelOutboundAddress>> delOutboundsaddresps = Lists.partition(delOutboundAddresses,100);

        try {
            //批量添加出口单据
            //IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            int batchDeloutbound[] = Context.batchInsert("del_outbound",delOutbounds,"id");
            //iDelOutboundService.saveBatch(newdelOutbounds);
            //IDelOutboundAddressService iDelOutboundAddressService = SpringUtils.getBean(IDelOutboundAddressService.class);
            //iDelOutboundAddressService.saveBatch(delOutboundAddresses);
            int batchDeloutboundAddress[] = Context.batchInsert("del_outbound_address",delOutboundAddresses,"id");

            //修改导入的数据
            if (CollectionUtils.isNotEmpty(updateData) && batchDeloutbound.length > 0) {
                OfflineDeliveryImportMapper offlineDeliveryImportMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
                OfflineCostImportMapper offlineCostImportMapper = SpringUtils.getBean(OfflineCostImportMapper.class);
                int update = offlineDeliveryImportMapper.updateDealState(updateData);
                offlineCostImportMapper.updateOrderNo(updateData);

            }

            return offlineResultDto;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void rollback(String errorMsg) {

        logger.error("OfflineDeliveryCreateOrderCmd 异常回滚:{}",errorMsg);

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.CREATE_ORDER.getCode());
            if(StringUtils.isNotEmpty(errorMsg)) {
                offlineImportDto.setErrorMsg(errorMsg);
            }else{
                offlineImportDto.setErrorMsg("创建订单失败");
            }
            updateData.add(offlineImportDto);
        }

        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(updateData)) {

//            for(OfflineImportDto importDto : updateData){
//
//                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                offlineDeliveryImport.setId(importDto.getId());
//                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//                offlineDeliveryImport.setErrorMsg(importDto.getErrorMsg());
//                importMapper.updateById(offlineDeliveryImport);
//            }

            importMapper.updateDealState(updateData);
        }

        super.rollback(errorMsg);
    }

    private DelOutbound generatorDeloutbond(OfflineDeliveryImport deliveryImport){
        DelOutbound delOutbound = new DelOutbound();
        String specifications = deliveryImport.getSpecifications();

        delOutbound.setCreateBy(deliveryImport.getCreateBy());
        delOutbound.setCreateByName(deliveryImport.getCreateByName());
        delOutbound.setCreateTime(deliveryImport.getCreateTime());
        delOutbound.setDelFlag("0");
        if(StringUtils.isNotEmpty(specifications)) {
            delOutbound.setSpecifications(specifications);
            delOutbound.setLength(deliveryImport.getLength().doubleValue());
            delOutbound.setWidth(deliveryImport.getWidth().doubleValue());
            delOutbound.setHeight(deliveryImport.getHeight().doubleValue());
        }
        delOutbound.setWeight(deliveryImport.getWeight().doubleValue());
        delOutbound.setCalcWeightUnit("G");
        delOutbound.setTrackingNo(deliveryImport.getTrackingNo());
        delOutbound.setRefNo(deliveryImport.getRefNo());
        delOutbound.setState(DelOutboundStateEnum.COMPLETED.getCode());
        delOutbound.setSourceType("offline");
        delOutbound.setReassignType("N");
        delOutbound.setAmount(deliveryImport.getAmount());
        delOutbound.setCalcWeight(deliveryImport.getCalcWeight());
        delOutbound.setRemark(deliveryImport.getRemark());
        delOutbound.setCurrencyCode("CNY");
        delOutbound.setBringVerifyState("END");
        delOutbound.setWarehouseCode(deliveryImport.getWarehouseCode());
        delOutbound.setVersion(deliveryImport.getVersion());
        delOutbound.setSellerCode(deliveryImport.getSellerCode());
        delOutbound.setCustomCode(deliveryImport.getSellerCode());
        delOutbound.setShipmentRule(deliveryImport.getShipmentService());
        delOutbound.setHouseNo(deliveryImport.getHouseNo());
        delOutbound.setBringVerifyTime(deliveryImport.getBringTime());
        //delOutbound.setDeliveryTime(deliveryImport.getDeliveryTime());
        delOutbound.setOrderType(DelOutboundOrderEnum.PACKAGE_TRANSFER.getCode());
        delOutbound.setShipmentsTime(deliveryImport.getDeliveryTime());
        delOutbound.setCreateTime(deliveryImport.getBringTime());
        //delOutbound.setDeliveryAgent(deliveryImport.getSupplierName());
        delOutbound.setShipmentState("END");
        delOutbound.setCompletedState("END");
        if(deliveryImport.getCod() != null) {
            delOutbound.setCodAmount(new BigDecimal(deliveryImport.getCod()));
        }
        delOutbound.setAmazonLogisticsRouteId(deliveryImport.getAmazonLogisticsRouteId());

        return delOutbound;
    }

    private DelOutboundAddress generatorOutAddress(OfflineDeliveryImport deliveryImport){

        DelOutboundAddress delOutboundAddress = new DelOutboundAddress();

        delOutboundAddress.setCreateBy(deliveryImport.getCreateBy());
        delOutboundAddress.setDelFlag("0");
        delOutboundAddress.setCreateTime(deliveryImport.getCreateTime());
        delOutboundAddress.setCreateByName(deliveryImport.getCreateByName());
        delOutboundAddress.setCountry(deliveryImport.getCountry());
        delOutboundAddress.setCountryCode(deliveryImport.getCountryCode());
        delOutboundAddress.setStreet1(deliveryImport.getStreet1());
        delOutboundAddress.setStreet2(deliveryImport.getStreet2());
        delOutboundAddress.setCity(deliveryImport.getCity());
        delOutboundAddress.setPostCode(deliveryImport.getPostCode());
        delOutboundAddress.setRemark(deliveryImport.getRemark());
        delOutboundAddress.setEmail(deliveryImport.getEmail());
        delOutboundAddress.setTaxNumber(deliveryImport.getTaxNumber());
        delOutboundAddress.setStateOrProvince(deliveryImport.getStateOrProvince());
        delOutboundAddress.setVersion(deliveryImport.getVersion());
        delOutboundAddress.setPhoneNo(deliveryImport.getPhoneNo());
        delOutboundAddress.setConsignee(deliveryImport.getCustomCode());

        return delOutboundAddress;
    }

}
