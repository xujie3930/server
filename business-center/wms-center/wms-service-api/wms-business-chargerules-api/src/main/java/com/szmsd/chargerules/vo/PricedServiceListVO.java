package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * prc服务列表
 *
 * @ClassName: PricedServiceListVO
 * @Author: 11
 * @Date: 2022-03-01 17:30
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "prc服务列表")
public class PricedServiceListVO {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("name")
    private String name;

    @ApiModelProperty(value = "supplierId", notes = "展示用")
    private String supplierId;
    @ApiModelProperty(value = "externalSupplier", notes = "展示用")
    private String externalSupplier;
}
