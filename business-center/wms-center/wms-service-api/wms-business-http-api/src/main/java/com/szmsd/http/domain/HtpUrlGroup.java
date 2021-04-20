package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "HtpUrlGroup", description = "地址组")
public class HtpUrlGroup {

    @ApiModelProperty(value = "地址组")
    private String groupId;

    @ApiModelProperty(value = "地址组名称")
    private String groupName;

    @ApiModelProperty(value = "1默认")
    private String defaultFlag;

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
