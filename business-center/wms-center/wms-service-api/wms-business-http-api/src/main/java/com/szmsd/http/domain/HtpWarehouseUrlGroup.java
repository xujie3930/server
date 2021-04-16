package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "HtpWarehouseUrlGroup", description = "外部服务仓库组表 关联 外部服务地址分组表")
public class HtpWarehouseUrlGroup {

    @ApiModelProperty(value = "仓库组")
    private String warehouseGroupId;

    @ApiModelProperty(value = "仓库组名称")
    private String warehouseGroupName;

    @ApiModelProperty(value = "外部服务组")
    private String urlGroupId;

    @ApiModelProperty(value = "外部服务组名称")
    private String urlGroupName;

}
