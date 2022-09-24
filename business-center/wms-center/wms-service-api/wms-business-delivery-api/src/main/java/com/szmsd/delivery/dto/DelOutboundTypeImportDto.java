package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundTypeImportDto", description = "DelOutboundTypeImportDto对象")
public class DelOutboundTypeImportDto implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库方式")
    private String orderTypeName;

}
