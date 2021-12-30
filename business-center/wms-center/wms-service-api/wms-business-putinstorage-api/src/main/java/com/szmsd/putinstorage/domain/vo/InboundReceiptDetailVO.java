package com.szmsd.putinstorage.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel(value = "InboundReceiptDetailVO", description = "InboundReceiptDetailVO入库明细视图")
public class InboundReceiptDetailVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ExcelProperty(index = 0)
    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "申报品名")
    private String skuName;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "申报数量")
    @Excel(name = "申报数量")
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    private Integer putQty;
    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "原产品编码")
    @Excel(name = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "采购-运单号/出库-出库单号作为该单号")
    private String deliveryNo;

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;

    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

}
