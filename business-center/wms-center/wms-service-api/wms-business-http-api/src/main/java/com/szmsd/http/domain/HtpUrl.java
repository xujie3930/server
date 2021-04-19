package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "HtpUrl", description = "外部服务地址表")
public class HtpUrl {

    @ApiModelProperty(value = "服务组")
    private String groupId;

    @ApiModelProperty(value = "服务组名称")
    private String groupName;

    @ApiModelProperty(value = "服务", hidden = true)
    private String serviceId;

    @ApiModelProperty(value = "请求头部, 字符串json{}")
    private Object headers;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "备注")
    private String remark;

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
