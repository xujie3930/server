package com.szmsd.http.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "记录部署日志", description = "部署日志")
public class HtpDeployLog {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建者")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
