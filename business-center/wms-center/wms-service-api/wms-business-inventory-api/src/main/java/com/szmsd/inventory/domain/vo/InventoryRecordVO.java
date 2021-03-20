package com.szmsd.inventory.domain.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryRecordVO", description = "InventoryRecordVO库存日志")
public class InventoryRecordVO {

    @ApiModelProperty(value = "单据号")
    private String receiptNo;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "类型：系统语言")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.INVENTORY_RECORD_TYPE)
    private String typeName;

    @ApiModelProperty(value = "操作数量")
    private Integer quantity;

    @ApiModelProperty(value = "之前总库存")
    private Integer beforeTotalInventory;

    @ApiModelProperty(value = "之前可用库存")
    private Integer beforeAvailableInventory;

    @ApiModelProperty(value = "之前冻结库存")
    private Integer beforeFreezeInventory;

    @ApiModelProperty(value = "之前总入库")
    private Integer beforeTotalInbound;

    @ApiModelProperty(value = "之前总出库")
    private Integer beforeTotalOutbound;

    @ApiModelProperty(value = "之后总库存")
    private Integer afterTotalInventory;

    @ApiModelProperty(value = "之后可用库存")
    private Integer afterAvailableInventory;

    @ApiModelProperty(value = "之后原冻结库存")
    private Integer afterFreezeInventory;

    @ApiModelProperty(value = "之后原总入库")
    private Integer afterTotalInbound;

    @ApiModelProperty(value = "之后原总出库")
    private Integer afterTotalOutbound;

    @ApiModelProperty(value = "日志")
    private String logs;

    @ApiModelProperty(value = "日志 - 系统语言")
    @FieldJsonI18n(isPlaceholder = true, localLanguageType = LocalLanguageTypeEnum.INVENTORY_RECORD_LOGS)
    private String logsLanguage;

}
