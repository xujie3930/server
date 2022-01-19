package com.szmsd.bas.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerWrapVO {

    @ApiModelProperty(value = "是否需要推送CK1")
    private Boolean pushFlag;

    @ApiModelProperty(value = "授权码")
    private String authorizationCode;
}
