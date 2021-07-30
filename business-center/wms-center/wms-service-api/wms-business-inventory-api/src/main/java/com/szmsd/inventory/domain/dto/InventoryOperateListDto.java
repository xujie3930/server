package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
@ApiModel(value = "InventoryOperateListDto", description = "InventoryOperateListDto对象")
public class InventoryOperateListDto implements Serializable {

    @ApiModelProperty(value = "单据编号")
    private String invoiceNo;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "操作库存")
    private List<InventoryOperateDto> operateList;

    @ApiModelProperty(value = "取消操作库存")
    private List<InventoryOperateDto> unOperateList;

    @ApiModelProperty(value = "冻结可以为负数，默认null，值为1是冻结可以为负数")
    private Integer freeType;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;
}
