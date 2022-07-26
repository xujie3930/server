package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryAdjustmentDTO", description = "库存调整")
public class InventoryAdjustmentDTO {

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "调整方式5调增、6调减")
    private String adjustment;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "卖家code")
    private String sellerCode;
    /**
     * 退件 调整类型为退件
     */
    @ApiModelProperty(value = "退件来源",hidden = true)
    private Boolean formReturn = false;

    @ApiModelProperty(value = "退件来源-单号",hidden = true)
    private String receiptNo;

    @ApiModelProperty(value = "关联单号")
    private String relevanceNumber;

    @ApiModelProperty(value = "备注")
    private String remark;

}
