package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CreateProductDTO", description = "创建产品服务")
public class CreateProductDTO {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类型（普通产品还是组合产品）")
    private String type;

    @ApiModelProperty(value = "产品分类 - 产品类别")
    private String category;

    @ApiModelProperty(value = "挂号逾期天数")
    private Integer overdueDay;

    @ApiModelProperty(value = "支持发货类型：普货、电池/带电池、液体、粉末")
    private List<String> shipmentTypeSupported;

    @ApiModelProperty(value = "服务渠道名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "服务商名称")
    private String terminalCarrier;

}
