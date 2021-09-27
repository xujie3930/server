package com.szmsd.doc.api.region.request;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegionReq extends QueryDto {

    @ApiModelProperty(value = "地址类别:1国家 2省份 3市 4区 5街道", allowableValues = "1:国家,2:省份,3:市,4:区,5:街道")
    private Integer type;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "地址简码")
    private String addressCode;

}
