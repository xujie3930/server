package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
public class InventoryDeductionListDto implements Serializable {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "扣减库存")
    private List<InventoryDeductionDto> deductionList;
}
