package com.szmsd.http.dto;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BasCodExternalDto extends QueryDto {



    @ApiModelProperty(value = "国家代码")
    private String countryCode;


    @ApiModelProperty(value = "源币种代码")
    private String currencyFrom;



    @ApiModelProperty(value = "目标币种代码")
    private String currencyTo;




    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;


}