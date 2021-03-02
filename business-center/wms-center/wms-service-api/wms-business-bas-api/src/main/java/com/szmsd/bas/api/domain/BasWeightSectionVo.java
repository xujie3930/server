package com.szmsd.bas.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * <p>
 * 重量区间设置
 * </p>
 *
 * @author 2
 * @since 2021-01-11
 */
@Data
@ApiModel(value = "重量区间设置返回对象", description = "BasWeightSectionVo对象")
public class BasWeightSectionVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "重量段开始")
    private String weightStart;

    @ApiModelProperty(value = "重量段结束")
    private String weightEnd;

    @ApiModelProperty(value = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    private String userName;

}
