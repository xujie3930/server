package com.szmsd.putinstorage.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptVO", description = "InboundReceiptVO入库单视图")
public class InboundReceiptVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "采购单")
    private String orderNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.INBOUND_RECEIPT_STATUS)
    private String statusName;

    @ApiModelProperty(value = "送货方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    private String deliveryWayName;

    @ApiModelProperty(value = "目的仓库名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseName;

    @ApiModelProperty(value = "入库方式名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    private String warehouseMethodName;

    @ApiModelProperty(value = "合计申报数量")
    private Integer totalDeclareQty;

    @ApiModelProperty(value = "到仓数量")
    private Integer totalPutQty;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
