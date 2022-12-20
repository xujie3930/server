package com.szmsd.delivery.command;

import com.alibaba.fastjson.JSON;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.finance.api.feign.RefundRequestFeignService;
import com.szmsd.finance.dto.RefundRequestAutoDTO;
import com.szmsd.finance.dto.RefundRequestDTO;
import com.szmsd.finance.dto.RefundRequestListAutoDTO;
import com.szmsd.finance.dto.RefundRequestListDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OfflineCreateCostCmd extends BasicCommand<Integer> {

    private OfflineResultDto offlineResultDto;

    public OfflineCreateCostCmd(OfflineResultDto offlineResultDto){
        this.offlineResultDto = offlineResultDto;
    }

    @Override
    protected void beforeDoExecute() {

    }

    @Override
    protected Integer doExecute() throws Exception {

        RefundRequestFeignService refundRequestFeignService = SpringUtils.getBean(RefundRequestFeignService.class);
        RefundRequestListAutoDTO autoRefundData = this.generatorRefundRequest();

        logger.info("autoRefund 退费参数：{}", JSON.toJSONString(autoRefundData));
        //自动退费
        R addRequest = refundRequestFeignService.autoRefund(autoRefundData);

        if(addRequest.getCode() != 200){
            throw new RuntimeException(addRequest.getMsg());
        }

        logger.info("autoRefund 退费返回：{}", JSON.toJSONString(addRequest));

        return 1;
    }

    @Override
    protected void afterExecuted(Integer result) throws Exception {

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.CREATE_COST.getCode());
            updateData.add(offlineImportDto);
        }

        if(CollectionUtils.isNotEmpty(updateData)) {
            //更新状态成 已创建费用
            importMapper.updateDealState(updateData);
        }

        super.afterExecuted(result);
    }

    private RefundRequestListAutoDTO generatorRefundRequest() {

        RefundRequestListAutoDTO refundRequestListDTO = new RefundRequestListAutoDTO();
        List<RefundRequestAutoDTO> refundRequestList = new ArrayList<>();

        List<OfflineCostImport> offlineCostImports = offlineResultDto.getOfflineCostImportList();
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

//        DelOutboundMapper delOutboundMapper = SpringUtils.getBean(DelOutboundMapper.class);
//
//        List<String> orderNoList = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getOrderNo).distinct().collect(Collectors.toList());
//
//        List<List<String>> orderPartions = Lists.partition(orderNoList,200);
//
//        List<DelOutbound> allDelOutboundList = new ArrayList<>();
//
//        for(List<String> strings : orderPartions){
//
//            List<DelOutbound> delOutboundList = delOutboundMapper.selectList(Wrappers.<DelOutbound>query().lambda().in(DelOutbound::getOrderNo,strings));
//            allDelOutboundList.addAll(delOutboundList);
//        }
//
//        Map<String,DelOutbound> delOutboundMap = allDelOutboundList.stream().collect(Collectors.toMap(DelOutbound::getOrderNo,v->v));


        List<String> warehouseCodeList = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getWarehouseCode).distinct().collect(Collectors.toList());
        BasWarehouseFeignService basWarehouseFeignService = SpringUtils.getBean(BasWarehouseFeignService.class);
        Map<String,BasWarehouse> basWarehouseMap = null;
        if(CollectionUtils.isNotEmpty(warehouseCodeList)){
            R<List<BasWarehouse>> warehouseRs = basWarehouseFeignService.queryByWarehouseCodes(warehouseCodeList);
            if(warehouseRs == null){
                throw new RuntimeException("无法获取仓库基本信息");
            }

            List<BasWarehouse> basWarehouses = warehouseRs.getData();

            basWarehouseMap = basWarehouses.stream().collect(Collectors.toMap(BasWarehouse::getWarehouseCode,v->v));
        }

        //List<String> currencyCodeList = offlineCostImports.stream().map(OfflineCostImport::getCurrencyCode).distinct().collect(Collectors.toList());

        BasSubFeignService basSubFeignService = SpringUtils.getBean(BasSubFeignService.class);
        R<Map<String, List<BasSubWrapperVO>>> basSubCurrencyRs = basSubFeignService.getSub("008");

        if(!Constants.SUCCESS.equals(basSubCurrencyRs.getCode())){
            throw  new RuntimeException("无法获取币种信息");
        }

        List<BasSubWrapperVO> baslist = basSubCurrencyRs.getData().get("008");

        if(CollectionUtils.isEmpty(baslist)){
            throw  new RuntimeException("无法获取币种信息");
        }

        Map<String,BasSubWrapperVO> basSubWrapperCodeVOMap = baslist.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue,v->v));

        Map<String,OfflineDeliveryImport> deliveryImportMap = offlineDeliveryImports.stream().collect(Collectors.toMap(OfflineDeliveryImport::getTrackingNo, v->v));

        for(OfflineCostImport costImport :offlineCostImports){

            OfflineDeliveryImport deliveryImport = deliveryImportMap.get(costImport.getTrackingNo());
            RefundRequestAutoDTO refundRequestDTO = new RefundRequestAutoDTO();

            refundRequestDTO.setAmount(costImport.getAmount());
            refundRequestDTO.setCurrencyCode(costImport.getCurrencyCode());
            refundRequestDTO.setTreatmentProperties("补收");
            refundRequestDTO.setTreatmentPropertiesCode("025003");
            refundRequestDTO.setCusCode(deliveryImport.getSellerCode());
            refundRequestDTO.setCusName(deliveryImport.getSellerCode());
            refundRequestDTO.setWarehouseCode(deliveryImport.getWarehouseCode());
            refundRequestDTO.setBusinessTypeCode("转运");
            refundRequestDTO.setBusinessTypeCode("012001");
            //refundRequestDTO.setFeeTypeCode("038003");
            refundRequestDTO.setFeeTypeName(costImport.getChargeType());
           // refundRequestDTO.setFeeCategoryCode();
            refundRequestDTO.setFeeCategoryName(costImport.getChargeCategory());

            refundRequestDTO.setStandardPayout(new BigDecimal("0.00"));
            refundRequestDTO.setAdditionalPayout(new BigDecimal("0.00"));
            refundRequestDTO.setCompensationPaymentFlag("1");
            refundRequestDTO.setPayoutAmount(new BigDecimal("0.00"));
            refundRequestDTO.setAttributes("自动退费");
            refundRequestDTO.setRemark(costImport.getRemark());
            refundRequestDTO.setOrderNo(deliveryImport.getOrderNo());
            refundRequestDTO.setProcessNo(deliveryImport.getTrackingNo());
            refundRequestDTO.setShipmentRule(deliveryImport.getShipmentService());
            refundRequestDTO.setTrackingNo(deliveryImport.getTrackingNo());

            if(basWarehouseMap != null){
                BasWarehouse basWarehouse = basWarehouseMap.get(refundRequestDTO.getWarehouseCode());
                refundRequestDTO.setWarehouseName(basWarehouse.getWarehouseNameCn());
            }

            if(basSubWrapperCodeVOMap != null){
                BasSubWrapperVO basSubCodeWrapperVO = basSubWrapperCodeVOMap.get(refundRequestDTO.getCurrencyCode());
                refundRequestDTO.setCurrencyName(basSubCodeWrapperVO.getSubName());
            }

            refundRequestList.add(refundRequestDTO);
        }

        refundRequestListDTO.setRefundRequestList(refundRequestList);

        return refundRequestListDTO;
    }
}
