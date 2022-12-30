package com.szmsd.delivery.command;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineImportDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.delivery.service.IDelOutboundService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OfflineDeliveryTrackYeeCmd extends BasicCommand<Integer> {

    private OfflineResultDto offlineResultDto;

    public OfflineDeliveryTrackYeeCmd(OfflineResultDto offlineResultDto){
        this.offlineResultDto = offlineResultDto;
    }

    @Override
    protected Integer doExecute() throws Exception {

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();
        List<String> trackNos = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());

        IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

        List<DelOutbound> delOutboundList = iDelOutboundService.list(Wrappers.<DelOutbound>query().lambda().in(DelOutbound::getTrackingNo,trackNos));

        if(CollectionUtils.isEmpty(delOutboundList)){
            return 0;
        }

        List<String> ids = new ArrayList<>();
        for(DelOutbound delOutbound : delOutboundList){
            Long id = delOutbound.getId();
            ids.add(id.toString());
        }

        try {
            logger.info("推送TP开始:{}", JSON.toJSONString(ids));
            iDelOutboundService.manualTrackingYee(ids);
            logger.info("推送TP结束",JSON.toJSONString(ids));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("推送PY失败：",e.getMessage());
            return 0;
        }
        return 1;
    }

    @Override
    protected void rollback(String errorMsg) {

        logger.error("OfflineDeliveryTrackYeeCmd 异常回滚:{}",errorMsg);

        OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

        List<OfflineImportDto> updateData = new ArrayList<>();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

            OfflineImportDto offlineImportDto = new OfflineImportDto();
            offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
            offlineImportDto.setId(deliveryImport.getId());
            offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.PUSH_TY.getCode());
            if(StringUtils.isNotEmpty(errorMsg)) {
                offlineImportDto.setErrorMsg(errorMsg);
            }else{
                offlineImportDto.setErrorMsg("推送TY异常");
            }
            updateData.add(offlineImportDto);
        }

        if(CollectionUtils.isNotEmpty(updateData)) {
            importMapper.updateDealState(updateData);
//            for(OfflineImportDto importDto : updateData){
//
//                OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                offlineDeliveryImport.setId(importDto.getId());
//                offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
//                offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//                offlineDeliveryImport.setOrderNo(importDto.getOrderNo());
//                offlineDeliveryImport.setErrorMsg(importDto.getErrorMsg());
//
//                importMapper.updateById(offlineDeliveryImport);
//            }
        }

        super.rollback(errorMsg);
    }

    @Override
    protected void afterExecuted(Integer result) throws Exception {

        logger.info("OfflineDeliveryTrackYeeCmd afterExecuted:{}", result);

        if(result == 1){

            OfflineDeliveryImportMapper importMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
            List<OfflineDeliveryImport> offlineDeliveryImports = offlineResultDto.getOfflineDeliveryImports();

            List<OfflineImportDto> updateData = new ArrayList<>();
            for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){

                OfflineImportDto offlineImportDto = new OfflineImportDto();
                offlineImportDto.setTrackingNo(deliveryImport.getTrackingNo());
                offlineImportDto.setId(deliveryImport.getId());
                offlineImportDto.setDealStatus(OfflineDeliveryStateEnum.PUSH_TY.getCode());
                updateData.add(offlineImportDto);
            }

            if(CollectionUtils.isNotEmpty(updateData)) {
                importMapper.updateDealState(updateData);
//                for(OfflineImportDto importDto : updateData){
//
//                    OfflineDeliveryImport offlineDeliveryImport = new OfflineDeliveryImport();
//                    offlineDeliveryImport.setId(importDto.getId());
//                    offlineDeliveryImport.setTrackingNo(importDto.getTrackingNo());
//                    offlineDeliveryImport.setDealStatus(importDto.getDealStatus());
//                    offlineDeliveryImport.setOrderNo(importDto.getOrderNo());
//
//                    importMapper.updateById(offlineDeliveryImport);
//                }
            }
        }

        super.afterExecuted(result);
    }
}
