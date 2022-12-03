package com.szmsd.delivery.command;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import com.szmsd.delivery.dto.OfflineCostExcelDto;
import com.szmsd.delivery.dto.OfflineDeliveryExcelDto;
import com.szmsd.delivery.dto.OfflineReadDto;
import com.szmsd.delivery.mapper.OfflineDeliveryImportMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OfflineDeliveryReadExcelCmd extends BasicCommand<OfflineReadDto> {

    private MultipartFile file;

    public OfflineDeliveryReadExcelCmd(MultipartFile file){
        this.file = file;
    }

    protected void beforeDoExecute() {
        AssertUtil.notNull(file, "上传文件不存在");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "导入文件名称不存在");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXlsx, "请上传xlsx文件");
    }


    @Override
    protected OfflineReadDto doExecute() throws Exception {

        OfflineReadDto offlineReadDto = new OfflineReadDto();

        ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), OfflineDeliveryExcelDto.class, null).sheet(0);
        List<OfflineDeliveryExcelDto> dtoList = excelReaderSheetBuilder.doReadSync();

        if (CollectionUtils.isEmpty(dtoList)) {
            throw new RuntimeException("导入的线下出库数据不能为空");
        }

        Map<String, Long> deliverGroup = dtoList.stream().collect(Collectors.groupingBy(OfflineDeliveryExcelDto::getTrackingNo, Collectors.counting()));

        List<String> deliverGroupFilter = deliverGroup.keySet().stream().filter(key -> deliverGroup.get(key) > 1).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(deliverGroupFilter)){
            throw new RuntimeException(JSON.toJSONString(deliverGroupFilter)+"存在重复数据");
        }

        List<String> trackingNoList = dtoList.stream().map(OfflineDeliveryExcelDto::getTrackingNo).collect(Collectors.toList());

        OfflineDeliveryImportMapper offlineDeliveryImportMapper = SpringUtils.getBean(OfflineDeliveryImportMapper.class);
        List<OfflineDeliveryImport> importsData = offlineDeliveryImportMapper.selectList(Wrappers.<OfflineDeliveryImport>query().lambda().in(OfflineDeliveryImport::getTrackingNo,trackingNoList));

        if(CollectionUtils.isNotEmpty(importsData)){
            List<String> trackNoList = importsData.stream().map(OfflineDeliveryImport::getTrackingNo).collect(Collectors.toList());
            throw new RuntimeException("跟踪号"+JSON.toJSONString(trackNoList)+"已存在");
        }

        List<String> warehouseCodeList = dtoList.stream().map(OfflineDeliveryExcelDto::getWarehouseCode).collect(Collectors.toList());

        BasWarehouseClientService basWarehouseClientService = SpringUtils.getBean(BasWarehouseClientService.class);
        List<BasWarehouse> basWarehouses = basWarehouseClientService.queryByWarehouseCodes(warehouseCodeList);

        if(CollectionUtils.isEmpty(basWarehouses)){
            throw new RuntimeException("无法获取仓库信息");
        }

        Map<String,List<BasWarehouse>> basWarehouseMap = basWarehouses.stream().collect(Collectors.groupingBy(BasWarehouse::getWarehouseCode));

        for(OfflineDeliveryExcelDto excelDto : dtoList){
            List<BasWarehouse> warehouses = basWarehouseMap.get(excelDto.getWarehouseCode());
            if(CollectionUtils.isEmpty(warehouses)){
                throw new RuntimeException("跟踪号:"+excelDto.getTrackingNo()+",仓库代码异常无法导入");
            }
        }

        ExcelReaderSheetBuilder costBuilder = EasyExcelFactory.read(file.getInputStream(), OfflineCostExcelDto.class, null).sheet(1);
        List<OfflineCostExcelDto> costExcelDtos = costBuilder.doReadSync();

        if(CollectionUtils.isEmpty(costExcelDtos)){
            throw new RuntimeException("导入的补收费用数据不能为空");
        }

        offlineReadDto.setOfflineDeliveryExcelList(dtoList);
        offlineReadDto.setOfflineCostExcelDtoList(costExcelDtos);

        return offlineReadDto;
    }
}
