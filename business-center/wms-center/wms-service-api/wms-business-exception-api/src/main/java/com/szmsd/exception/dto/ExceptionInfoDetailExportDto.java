package com.szmsd.exception.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class ExceptionInfoDetailExportDto {

    @Excel(name="sku明细id",width = 30,needMerge=true)
    private String delDetailId;

    @Excel(name="中文申报品名",width = 30,needMerge=true)
    private String productNameChinese;

    @Excel(name="产品名称",width = 30,needMerge=true)
    private String productName;

    @Excel(name="申报价值",width = 30,needMerge=true)
    private String declaredValue;

    private String orderNo;

}
