package com.szmsd.http.domain;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YcAppParameter {


    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "API密钥")
    private String appToken;

    @ApiModelProperty(value = "API标识")

    private String appKey;

    @ApiModelProperty(value = "service地址")
    private String ycUrl;

    @ApiModelProperty(value = "功能模块")
    private String functiontrModule;


    @ApiModelProperty(value = "账号名")
    private String accountName;


    @ApiModelProperty(value = "请求的数据内容，json格式")
    @TableField(exist = false)
    private JSONObject jsonObject;

    @ApiModelProperty(value = "接口方法，参考接口方法列表")
    @TableField(exist = false)
    private String service;


}
