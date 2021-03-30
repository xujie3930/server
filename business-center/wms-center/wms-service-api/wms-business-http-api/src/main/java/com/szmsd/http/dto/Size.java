package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 13:49
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Size")
public class Size {

    @ApiModelProperty(value = "长，单位厘米")
    private Double lengthInCm;

    @ApiModelProperty(value = "宽，单位厘米")
    private Double widthInCm;

    @ApiModelProperty(value = "高，单位厘米")
    private Double heightInCm;
}
