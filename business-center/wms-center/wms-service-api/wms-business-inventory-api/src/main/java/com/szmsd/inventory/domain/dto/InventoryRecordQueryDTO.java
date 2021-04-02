package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryRecordQueryDTO", description = "InventoryRecordQueryDTO库存日志查询入参")
public class InventoryRecordQueryDTO {

    @ApiModelProperty(value = "单据号")
    private String receiptNo;

    @ApiModelProperty(value = "类型：1入库、2出库、3冻结、4盘点")
    private String type;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

}
