package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: PickupPackageListVO
 * @Description: 可用的提货服务名称
 * @Author: 11
 * @Date: 2022-03-25 16:06
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "可用的提货服务名称")
public class PickupPackageListVO {

    @ApiModelProperty("提货服务名字")
    private String serviceName;

    @ApiModelProperty("提货服务描述")
    private String serviceDescription;

    @ApiModelProperty("提货供应商名字")
    private String pickupSupplierName;
}
