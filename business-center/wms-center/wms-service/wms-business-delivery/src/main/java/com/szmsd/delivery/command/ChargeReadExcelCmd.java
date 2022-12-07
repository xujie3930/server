package com.szmsd.delivery.command;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.common.core.command.BasicCommand;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.convert.ChargeImportConvert;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.dto.ChargeExcelDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class ChargeReadExcelCmd extends BasicCommand<List<ChargeImport>> {

    private MultipartFile file;

    public ChargeReadExcelCmd(MultipartFile file){
        this.file = file;
    }

    @Override
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
    protected List<ChargeImport> doExecute() throws Exception {

        ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), ChargeExcelDto.class, null).sheet(0);
        List<ChargeExcelDto> chargeExcelDtos = excelReaderSheetBuilder.doReadSync();

        if (CollectionUtils.isEmpty(chargeExcelDtos)) {
            throw new RuntimeException("导入的线下出库数据不能为空");
        }

        List<ChargeImport> chargeImportList = ChargeImportConvert.INSTANCE.toChargeImportList(chargeExcelDtos);

        String userName = SecurityUtils.getUsername();
        for(ChargeImport chargeImport : chargeImportList){
            chargeImport.setCreateBy(userName);
            chargeImport.setCreateTime(new Date());
        }

        return chargeImportList;
    }
}
