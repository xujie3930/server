package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyuyuan
 * @date 2021-06-24 16:24
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Taxation", description = "Taxation对象")
public class Taxation {

    @ApiModelProperty(value = "类型")
    private String taxType;

    @ApiModelProperty(value = "编号")
    private String taxNumber;
}
