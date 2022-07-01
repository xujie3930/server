package com.szmsd.http.dto.custom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssociatedCustomersDto {

    @ApiModelProperty("客户编号")
    private String clientCode;


    @ApiModelProperty("有效开始时间")
    private String beginTime;

    @ApiModelProperty("有效解速时间")
    private String endTime;

    @ApiModelProperty("是否有效")
    private Boolean isValid;

}
