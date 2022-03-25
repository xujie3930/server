package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PickupPackageService", description = "可用提货服务")
public class PickupPackageService {

    @ApiModelProperty(value = "提货服务名字")
    private String serviceName;

    @ApiModelProperty(value = "提货供应商名字")
    private String pickupSupplierName;

    @ApiModelProperty(value = "提货服务描述")
    private String serviceDescription;

}
