package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "InventoryOperateDto", description = "InventoryOperateDto对象")
public class InventoryOperateDto implements Serializable {

    @ApiModelProperty(value = "单据行号")
    private String invoiceLineNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "库存")
    private Integer num;

    /**
     * 数量运算
     *
     * @param qty qty
     */
    public void addQty(int qty) {
        this.num = this.num + qty;
    }
}
