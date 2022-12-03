package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.support.Context;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.command.OfflineDeliveryReadExcelCmd;
import com.szmsd.delivery.convert.OfflineDeliveryConvert;
import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineCostExcelDto;
import com.szmsd.delivery.dto.OfflineDeliveryExcelDto;
import com.szmsd.delivery.dto.OfflineReadDto;
import com.szmsd.delivery.service.OfflineDeliveryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class OfflineDeliveryServiceImpl  implements OfflineDeliveryService {

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

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Date date = new Date();
        for(OfflineDeliveryImport deliveryImport : offlineDeliveryImports){
            deliveryImport.setVersion(1L);
            deliveryImport.setCreateBy(loginUser.getUsername());
            deliveryImport.setCreateTime(date);
            deliveryImport.setCreateByName(loginUser.getUsername());
        }
        for(OfflineCostImport costImport : offlineCostImports){
            costImport.setCreateBy(loginUser.getUsername());
            costImport.setCreateTime(date);
            costImport.setCreateByName(loginUser.getUsername());
        }

        //step 3. 保存解析记录
        int batchSaveDelivery[] = Context.batchInsert("offline_delivery_import",offlineDeliveryImports,"id");
        int batchSaveCost [] = Context.batchInsert("offline_cost_import",offlineCostImports,"id");

        if(batchSaveDelivery.length > 0 && batchSaveCost.length > 0){
            return R.ok();
        }

        return R.failed("导入失败");
    }
}
