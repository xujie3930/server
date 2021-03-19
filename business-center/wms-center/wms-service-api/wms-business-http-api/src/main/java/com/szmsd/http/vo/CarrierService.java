package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "CarrierService", description = "可用承运商服务")
public class CarrierService {

    @ApiModelProperty(value = "承运商系统内部服务名字")
    private String serviceName;

    @ApiModelProperty(value = "承运商系统对外显示服务名字")
    private String carrierName;

    @ApiModelProperty(value = "承运商的名字(同一个承运商，能一起打印标签)")
    private String serviceDescription;

}
