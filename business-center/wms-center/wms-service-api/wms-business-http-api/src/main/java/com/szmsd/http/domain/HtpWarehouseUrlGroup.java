package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "HtpWarehouseUrlGroup", description = "仓库组关联地址组")
public class HtpWarehouseUrlGroup {

    @ApiModelProperty(value = "仓库组")
    private String warehouseGroupId;

    @ApiModelProperty(value = "仓库组名称")
    private String warehouseGroupName;

    @ApiModelProperty(value = "地址组")
    private String urlGroupId;

    @ApiModelProperty(value = "地址组名称")
    private String urlGroupName;

}
