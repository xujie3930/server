package com.szmsd.delivery.command;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.command.BasicCommand;
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
import com.szmsd.finance.dto.RefundRequestListAutoDTO;

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
        //更新状态成 已创建费用
        importMapper.updateDealState(updateData);

        return 1;
    }

    private RefundRequestListAutoDTO generatorRefundRequest() {

        RefundRequestListAutoDTO refundRequestListDTO = new RefundRequestListAutoDTO();
        List<RefundRequestAutoDTO> refundRequestList = new ArrayList<>();

        List<OfflineCostImport> offlineCostImports = offlineResultDto.getOfflineCostImportList();
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

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

            refundRequestList.add(refundRequestDTO);
        }

        refundRequestListDTO.setRefundRequestList(refundRequestList);

        return refundRequestListDTO;
    }
}
