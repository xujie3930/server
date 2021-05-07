package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:18
 */
@Data
@ApiModel(value = "DelOutboundExportItemListDto", description = "DelOutboundExportItemListDto对象")
public class DelOutboundExportItemListDto implements Serializable {

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

}
