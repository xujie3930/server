package com.szmsd.bas.domain;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author chenanze
 * @date 2020-10-29
 * @description
 */
@Data
public class Mes {

    @ApiModelProperty(value = "付费网点")
    @Excel(name = "付费网点")
    private String paySite;

    @ApiModelProperty(value = "付费网点编号")
    private String paySiteCode;

    @ApiModelProperty(value = "成功条数")
    @Excel(name = "成功条数")
    private String success;

    @ApiModelProperty(value = "全部条数")
    @Excel(name = "全部条数")
    private String all;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "paySiteList")
    private List<String> paySiteList;

    @ApiModelProperty(value = "id")
    private String id;
}
