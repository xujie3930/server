package com.szmsd.putinstorage.domain.vo;

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

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "采购单")
    @Excel(name = "采购单号")
    private String orderNo;

    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ApiModelProperty(value = "送货方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @Excel(name = "送货方式")
    private String deliveryWayName;

    @ApiModelProperty(value = "送货单号")
    @Excel(name = "快递单号/揽收单号")
    private String deliveryNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.INBOUND_RECEIPT_STATUS)
    @Excel(name = "状态")
    private String statusName;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "目的仓库名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @Excel(name = "目的仓库")
    private String warehouseName;

    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;

    @ApiModelProperty(value = "入库方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @Excel(name = "入库方式")
    private String warehouseMethodName;

    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "申报数量")
    @Excel(name = "初始数量")
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    @Excel(name = "到仓数量")
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    @Excel(name = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "下单时间")
    private String createTime;

    @ApiModelProperty(value = "最后修改时间")
    @Excel(name = "到仓时间")
    private String updateTime;

    @ApiModelProperty(value = "审核备注")
    @Excel(name = "审核备注")
    private String reviewRemark;

    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String remark;

    @ApiModelProperty(value = "VAT")
    @Excel(name = "销售VAT")
    private String vat;

}
