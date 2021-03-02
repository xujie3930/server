package com.szmsd.bas.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * <p>
 * 重量区间设置
 * </p>
 *
 * @author 2
 * @since 2021-01-11
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "重量区间设置Dto", description = "BasWeightSectionDto对象")
public class BasWeightSectionDto {


    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "重量段集")
    private List<BasWeightDto> weightDto;
}
