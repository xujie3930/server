package com.szmsd.bas.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liyingfeng
 * @date 2021-01-11 17:34
 */
@Data
@Accessors(chain = true)
@ApiModel(value="重量Dto", description="BasWeightDto对象")
public class BasWeightDto {

    @ApiModelProperty(value = "重量段开始")
    private String weightStart;

    @ApiModelProperty(value = "重量段结束")
    private String weightEnd;
}
