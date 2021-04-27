package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "ImportResult", description = "数据导入的结果")
public class ImportResult extends ResponseVO {

    @ApiModelProperty("数据导入成功条数")
    private Integer successNumber;

    @ApiModelProperty("导入成功，但是存在警告的")
    private Integer successWithWarning;

    @ApiModelProperty("数据导入失败条数")
    private Integer failNumber;

    @ApiModelProperty("数据导入失败结果")
    private List<ImportRemoteAreaTemplate> failImportRowResults;

    @ApiModelProperty("数据导入成功，但是存在警告的结果")
    private List<ImportRemoteAreaTemplate> successImportRowWithWarningResults;

    @ApiModelProperty("模板异常数量")
    private Integer templateCodeExceptionNum;

    @ApiModelProperty("模板异常数据")
    private List<ImportRemoteAreaTemplate> templateCodeExceptionResults;

}
