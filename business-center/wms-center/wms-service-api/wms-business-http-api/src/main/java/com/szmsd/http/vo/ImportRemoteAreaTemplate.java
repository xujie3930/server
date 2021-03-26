package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ImportRemoteAreaTemplate", description = "导入的地址库数据")
public class ImportRemoteAreaTemplate extends RemoteAreaRule {

    @ApiModelProperty("模板名称")
    private String name;

    @ApiModelProperty("导入数据的数据行")
    private Integer rowNumber;

    @ApiModelProperty("导入的数据结果描述")
    private String message;

}
