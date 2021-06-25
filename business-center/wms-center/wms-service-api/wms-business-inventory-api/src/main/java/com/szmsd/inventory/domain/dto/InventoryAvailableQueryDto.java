package com.szmsd.inventory.domain.dto;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryAvailableQueryDto extends QueryDto implements Serializable {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "sku")
    private String eqSku;

    @ApiModelProperty(value = "skus")
    private List<String> skus;

    @ApiModelProperty(value = "查询类型，1可用库存为0时不查询。2可用库存为0时查询。默认1")
    private Integer queryType = 1;

    @ApiModelProperty(value = "只查询SKU，传值SKU")
    private String querySku;

    @ApiModelProperty(value = "SKU来源，不传默认084002")
    private String source;
}
