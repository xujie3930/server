package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "HtpWarehouseGroup", description = "外部服务仓库组")
public class HtpWarehouseGroup {

    @ApiModelProperty(value = "服务组")
    private String groupId;

    @ApiModelProperty(value = "服务组名称")
    private String groupName;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建者")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改者")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
