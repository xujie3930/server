package com.szmsd.putinstorage.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptExportVO", description = "入库单导出")
public class InboundReceiptExportVO implements Serializable {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(index = 0, value = "入库单号")
    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehouseNo;
    @ExcelProperty(index = 1, value = "采购单号")
    @ApiModelProperty(value = "采购单")
    @Excel(name = "采购单号")
    private String orderNo;
    @ExcelIgnore
    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ExcelProperty(index = 2, value = "送货方式")
    @ApiModelProperty(value = "送货方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @Excel(name = "送货方式")
    private String deliveryWayName;

    @ExcelProperty(index = 3, value = "快递单号/揽收单号")
    @ApiModelProperty(value = "送货单号")
    @Excel(name = "快递单号/揽收单号")
    private String deliveryNo;
    @ExcelIgnore
    @ApiModelProperty(value = "状态")
    private String status;
    @ExcelProperty(index = 4, value = "状态")
    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.INBOUND_RECEIPT_STATUS)
    @Excel(name = "状态")
    private String statusName;
    @ExcelIgnore
    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;
    @ExcelProperty(index = 5, value = "目的仓库")
    @ApiModelProperty(value = "目的仓库名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @Excel(name = "目的仓库")
    private String warehouseName;
    @ExcelIgnore
    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;
    @ExcelProperty(index = 6, value = "入库方式")
    @ApiModelProperty(value = "入库方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @Excel(name = "入库方式")
    private String warehouseMethodName;
    @ExcelProperty(index = 7, value = "SKU")
    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;
    @ExcelProperty(index = 8, value = "初始数量")
    @ApiModelProperty(value = "申报数量")
    @Excel(name = "初始数量")
    private Integer declareQty;
    @ExcelProperty(index = 9, value = "到仓数量")
    @ApiModelProperty(value = "上架数量")
    @Excel(name = "到仓数量")
    private Integer putQty;
    @ExcelProperty(index = 10, value = "原产品编码")
    @ApiModelProperty(value = "原产品编码")
    @Excel(name = "原产品编码")
    private String originCode;
    @ExcelProperty(index = 11, value = "下单时间")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "下单时间")
    private String createTime;
    @ExcelProperty(index = 12, value = "到仓时间")
    @ApiModelProperty(value = "最后修改时间")
    @Excel(name = "到仓时间")
    private String updateTime;
    @ExcelProperty(index = 13, value = "审核备注")
    @ApiModelProperty(value = "审核备注")
    @Excel(name = "审核备注")
    private String reviewRemark;
    @ExcelProperty(index = 14, value = "客户备注")
    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String remark;
    @ExcelProperty(index = 15, value = "销售VAT")
    @ApiModelProperty(value = "VAT")
    @Excel(name = "销售VAT")
    private String vat;

}
