package com.szmsd.delivery.command;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.delivery.dto.OfflineCostExcelDto;
import com.szmsd.delivery.dto.OfflineDeliveryExcelDto;
import com.szmsd.delivery.dto.OfflineReadDto;
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
