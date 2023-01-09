package com.szmsd.delivery.command;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.finance.api.feign.RefundRequestFeignService;
import com.szmsd.finance.dto.RefundRequestAutoDTO;
import com.szmsd.finance.dto.RefundRequestListAutoDTO;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.pack.domain.PackageAddress;
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

        List<RefundRequestAutoDTO> refundRequestAutoDTOS = autoRefundData.getRefundRequestList();

        List<List<RefundRequestAutoDTO>> autoRefundDataPart = Lists.partition(refundRequestAutoDTOS,10);

        for(List<RefundRequestAutoDTO> refundRequestAutoDTOS1 : autoRefundDataPart) {

            RefundRequestListAutoDTO partData = new RefundRequestListAutoDTO();
            partData.setRefundRequestList(refundRequestAutoDTOS1);
            //自动退费
            R addRequest = refundRequestFeignService.autoRefund(partData);

            if (addRequest == null || addRequest.getCode() != 200) {

                logger.error("autoRefund 退费返回失败数据:{}",JSON.toJSONString(partData));
                List<String> errorOrderNoList = refundRequestAutoDTOS1.stream().map(RefundRequestAutoDTO::getOrderNo).distinct().collect(Collectors.toList());
                String errorMsg = "生成退费、补收费用，自动审核退费失败，需要重新执行";
                if(addRequest != null && addRequest.getMsg() != null) {
                    errorMsg = addRequest.getMsg();
                }
                this.errorHander(errorOrderNoList,errorMsg);
                //throw new RuntimeException(addRequest.getMsg());
                continue;
            }

            logger.info("autoRefund 退费返回：{}", JSON.toJSONString(addRequest));
        }

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

//            for(OfflineImportDto importDto : updateData){
//
//                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                offlineDeliveryImport.setId(importDto.getId());
//                offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
//                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//
//                importMapper.updateById(offlineDeliveryImport);
//            }

        }

        super.afterExecuted(result);
    }

    @Override
    protected void rollback(String errorMsg) {

        logger.error("OfflineCreateCostCmd 异常回滚:{}",errorMsg);

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.CREATE_COST.getCode());
            if(StringUtils.isNotEmpty(errorMsg)) {
                offlineImportDto.setErrorMsg(errorMsg);
            }else{
                offlineImportDto.setErrorMsg("生成退费、补收费用，自动审核退费失败");
            }
            updateData.add(offlineImportDto);
        }

        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(updateData)) {

//            for(OfflineImportDto importDto : updateData){
//
//                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                offlineDeliveryImport.setId(importDto.getId());
//                offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
//                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//
//                importMapper.updateById(offlineDeliveryImport);
//            }

            importMapper.updateDealState(updateData);
        }

        super.rollback(errorMsg);
    }

    private void errorHander(List<String> orderNoList,String errorMsg) {

        logger.error("OfflineCreateCostCmd 异常:{}",orderNoList);

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(String orderNo : orderNoList){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.CREATE_COST.getCode());
            offlineImportDto.setErrorMsg(errorMsg);
            offlineImportDto.setOrderNo(orderNo);
            updateData.add(offlineImportDto);
        }

        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(updateData)) {

            for(OfflineImportDto importDto : updateData){

                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
                offlineDeliveryImport.setId(importDto.getId());
                offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());

                importMapper.update(null, Wrappers.<OfflineDeliveryImport>lambdaUpdate()
                        .eq(OfflineDeliveryImport::getOrderNo,importDto.getOrderNo())
                        .set(OfflineDeliveryImport::getDealStatus,importDto.getDealStatus())
                        .set(OfflineDeliveryImport::getErrorMsg,importDto.getErrorMsg())
                );
            }

            //importMapper.updateDealStateByOrder(updateData);
        }
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
            String processNo = createSerialNumber();
            refundRequestDTO.setOrderNo(deliveryImport.getOrderNo());
            refundRequestDTO.setProcessNo(processNo);
            refundRequestDTO.setShipmentRule(deliveryImport.getShipmentService());
            refundRequestDTO.setTrackingNo(deliveryImport.getTrackingNo());
            //状态已完成
            refundRequestDTO.setAuditStatus(RefundStatusEnum.COMPLETE.getStatus());

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

    private String createSerialNumber(){

        String s = DateUtils.dateTime();
        String randomNums = RandomUtil.randomNumbers(8);
        return s + randomNums;
    }
}
