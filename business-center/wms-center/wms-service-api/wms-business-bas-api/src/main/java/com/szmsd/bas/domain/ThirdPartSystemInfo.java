package com.szmsd.bas.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: ThirdPartSystemInfo
 * @Description: 其他系统信息 token等
 * @Author: 11
 * @Date: 2021-12-09 15:06
 */
@Data
@ApiModel(description = "其他系统存储信息")
public class ThirdPartSystemInfo {

    @ApiModelProperty(value = "业务系统", hidden = true)
    private ThirdPartEnum thirdPartEnum = ThirdPartEnum.OMS;

    @ApiModelProperty(value = "请求头名称", hidden = true)
    private String headName;

    @ApiModelProperty(value = "业务系统token")
    private String token;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "仓库")
    private String warehouse;
}