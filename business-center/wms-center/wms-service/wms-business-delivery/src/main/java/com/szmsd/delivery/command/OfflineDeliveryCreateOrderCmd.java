package com.szmsd.delivery.command;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundService;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OfflineDeliveryCreateOrderCmd extends BasicCommand<Integer> {

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
    protected Integer doExecute() throws Exception {

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<DelOutbound> delOutbounds = new ArrayList<>();
        List<DelOutboundAddress> delOutboundAddresses = new ArrayList<>();
        List<OfflineImportDto> updateData = new ArrayList<>();
        SerialNumberClientService serialNumberClientService = SpringUtils.getBean(SerialNumberClientService.class);

        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            DelOutbound delOutbound = this.generatorDeloutbond(deliveryImport);
            DelOutboundAddress delOutboundAddress = this.generatorOutAddress(deliveryImport);
            OfflineImportDto offlineImportDto = new OfflineImportDto();
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

            delOutbound.setOrderNo(orderNo);
            delOutboundAddress.setOrderNo(orderNo);
            delOutbounds.add(delOutbound);
            delOutboundAddresses.add(delOutboundAddress);
            updateData.add(offlineImportDto);

        }

        if(CollectionUtils.isEmpty(delOutbounds) || CollectionUtils.isEmpty(delOutboundAddresses)){
            return 0;
        }

        //批量添加出口单据
        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);
        iDelOutboundService.saveBatch(delOutbounds);
        IDelOutboundAddressService iDelOutboundAddressService = SpringUtils.getBean(IDelOutboundAddressService.class);
        iDelOutboundAddressService.saveBatch(delOutboundAddresses);

        //修改导入的数据
        if(CollectionUtils.isNotEmpty(updateData)) {
            OfflineDeliveryImportMapper offlineDeliveryImportMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
            offlineDeliveryImportMapper.updateDealState(updateData);
        }
        return 1;
    }


    private DelOutbound generatorDeloutbond(OfflineDeliveryImport deliveryImport){
        DelOutbound delOutbound = new DelOutbound();

        delOutbound.setCreateBy(deliveryImport.getCreateBy());
        delOutbound.setCreateByName(deliveryImport.getCreateByName());
        delOutbound.setCreateTime(deliveryImport.getCreateTime());
        delOutbound.setDelFlag("0");
        delOutbound.setSpecifications(deliveryImport.getSpecifications());
        delOutbound.setWeight(deliveryImport.getWeight().doubleValue());
        delOutbound.setTrackingNo(deliveryImport.getTrackingNo());
        delOutbound.setRefNo(deliveryImport.getRefNo());
        delOutbound.setState(DelOutboundStateEnum.COMPLETED.getCode());
        delOutbound.setSourceType("offline");
        delOutbound.setAmount(deliveryImport.getAmount());
        delOutbound.setCalcWeight(deliveryImport.getCalcWeight());
        delOutbound.setRemark(deliveryImport.getRemark());
        delOutbound.setCurrencyCode(deliveryImport.getCountryCode());
        delOutbound.setBringVerifyState("END");
        delOutbound.setWarehouseCode(deliveryImport.getWarehouseCode());
        delOutbound.setVersion(deliveryImport.getVersion());
        delOutbound.setSellerCode(deliveryImport.getSellerCode());
        delOutbound.setCustomCode(deliveryImport.getCustomCode());
        delOutbound.setShipmentService(deliveryImport.getShipmentService());
        delOutbound.setHouseNo(deliveryImport.getHouseNo());
        delOutbound.setBringVerifyTime(deliveryImport.getBringTime());
        delOutbound.setDeliveryTime(deliveryImport.getDeliveryTime());
        //delOutbound.setDeliveryAgent(deliveryImport.getSupplierName());
        delOutbound.setShipmentState("END");
        delOutbound.setCompletedState("END");
        if(deliveryImport.getCod() != null) {
            delOutbound.setCodAmount(new BigDecimal(deliveryImport.getCod()));
        }
        delOutbound.setAmazonReferenceId(deliveryImport.getAmazonLogisticsRouteId());

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
