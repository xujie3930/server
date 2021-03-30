package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:40
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "PackageResult", description = "PackageResult对象")
public class PackageResult {

    @ApiModelProperty(value = "包裹号")
    private String packageNumber;

    @ApiModelProperty(value = "包裹标签")
    private String labelUrl;

    @ApiModelProperty(value = "包裹挂号")
    private String trackingNumber;
}
