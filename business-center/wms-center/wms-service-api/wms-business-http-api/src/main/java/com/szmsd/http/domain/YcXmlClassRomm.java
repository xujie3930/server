package com.szmsd.http.domain;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


//@XmlRootElement
@Data
public class YcXmlClassRomm {

    @ApiModelProperty(value = "请求密钥集合")
    private List<YcAppParameter> ycAppParameterList;

    @ApiModelProperty(value = "请求的数据内容，json格式")
    private JSONObject jsonObject;

    @ApiModelProperty(value = "接口方法，参考接口方法列表")
    private String service;



}
