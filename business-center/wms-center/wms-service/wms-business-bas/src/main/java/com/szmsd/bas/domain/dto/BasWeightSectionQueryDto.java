package com.szmsd.bas.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


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
@ApiModel(value = "重量区间设置查询", description = "BasWeightSectionQueryDto对象")
public class BasWeightSectionQueryDto {

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    private String userName;

}
