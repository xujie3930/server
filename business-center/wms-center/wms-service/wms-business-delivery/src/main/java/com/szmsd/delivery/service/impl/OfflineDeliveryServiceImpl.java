package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.command.OfflineCreateCostCmd;
import com.szmsd.delivery.command.OfflineDeliveryCreateOrderCmd;
import com.szmsd.delivery.command.OfflineDeliveryReadExcelCmd;
import com.szmsd.delivery.command.OfflineDeliveryTrackYeeCmd;
import com.szmsd.delivery.convert.OfflineDeliveryConvert;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineCostExcelDto;
import com.szmsd.delivery.dto.OfflineDeliveryExcelDto;
import com.szmsd.delivery.dto.OfflineReadDto;
import com.szmsd.delivery.dto.OfflineResultDto;
import com.szmsd.delivery.enums.OfflineDeliveryStateEnum;
import com.szmsd.delivery.mapper.OfflineCostImportMapper;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import com.szmsd.delivery.service.OfflineDeliveryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfflineDeliveryServiceImpl  implements OfflineDeliveryService {

    @Autowired
    private OfflineDeliveryImportMapper offlineDeliveryImportMapper;

    @Autowired
    private OfflineCostImportMapper offlineCostImportMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(MultipartFile file) {

        //step 1. 解析excel
        OfflineReadDto offlineReadDto = new OfflineDeliveryReadExcelCmd(file).execute();

        List<OfflineDeliveryExcelDto> deliveryExcelDtoList = offlineReadDto.getOfflineDeliveryExcelList();
        List<OfflineCostExcelDto> offlineCostExcelDtos = offlineReadDto.getOfflineCostExcelDtoList();
        if(CollectionUtils.isEmpty(deliveryExcelDtoList) || CollectionUtils.isEmpty(offlineCostExcelDtos)){
            return R.failed("无法解析excel数据");
        }

        //step 2. 转换
        List<OfflineDeliveryImport> offlineDeliveryImports = OfflineDeliveryConvert.INSTANCE.toOfflineDeliveryImportList(deliveryExcelDtoList);
        List<OfflineCostImport> offlineCostImports = OfflineDeliveryConvert.INSTANCE.toOfflineCostImportList(offlineCostExcelDtos);

        String loginUser = SecurityUtils.getUsername();
        Date date = new Date();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){
            deliveryImport.setVersion(1L);
            deliveryImport.setCreateBy(loginUser);
            deliveryImport.setCreateTime(date);
            deliveryImport.setCreateByName(loginUser);
            deliveryImport.setDealStatus(OfflineDeliveryStateEnum.INIT.getCode());
        }
        for(OfflineCostImport costImport : offlineCostImports){
            costImport.setCreateBy(loginUser);
            costImport.setCreateTime(date);
            costImport.setCreateByName(loginUser);
        }

        //step 3. 保存解析记录
        int batchSaveDelivery = offlineDeliveryImportMapper.saveBatch(offlineDeliveryImports);
        int batchSaveCost = offlineCostImportMapper.saveBatch(offlineCostImports);
        if(batchSaveDelivery > 0 && batchSaveCost > 0){
            return R.ok();
        }

        return R.failed("导入失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R dealOfflineDelivery() {

        //step 1. 查询INIT 状态的导入数据
        OfflineResultDto offlineResultDto = this.selectOfflineData();
        if(offlineResultDto == null){
            return R.failed("无数据");
        }

        //step 2. 生成线下出库单，offline 状态修改成CREATE_ORDER
        int createOrder = new OfflineDeliveryCreateOrderCmd(offlineResultDto).execute();
        if(createOrder != 1){
            return R.failed("创建订单异常");
        }

        //step 3. 生成退费、补收费用，自动审核退费
        int createCost = new OfflineCreateCostCmd(offlineResultDto).execute();
        if(createCost != 1){
            return R.failed("创建退费费用异常");
        }

        //step 4. 推送TY
        int trackYee = new OfflineDeliveryTrackYeeCmd(offlineResultDto).execute();
        if(trackYee != 1){
            return R.failed("推送TY异常");
        }
        return R.ok();
    }


    private OfflineResultDto selectOfflineData(){

        List<OfflineDeliveryImport> offlineDeliveryImports = offlineDeliveryImportMapper.selectList(Wrappers.<OfflineDeliveryImport>query().lambda()
                .eq(OfflineDeliveryImport::getDealStatus,OfflineDeliveryStateEnum.INIT.getCode())
        );

        if(CollectionUtils.isEmpty(offlineDeliveryImports)){
            return null;
        }

        List<String> trackNoList = offlineDeliveryImports.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());
        List<OfflineCostImport> offlineCostImportList = offlineCostImportMapper.selectList(Wrappers.<OfflineCostImport>query().lambda().in(OfflineCostImport::getTrackingNo,trackNoList));
        OfflineResultDto offlineResultDto = new OfflineResultDto();
        offlineResultDto.setOfflineDeliveryImports(offlineDeliveryImports);
        offlineResultDto.setOfflineCostImportList(offlineCostImportList);

        return offlineResultDto;
    }

}
