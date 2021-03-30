package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 13:47
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Package")
public class Package {

    @ApiModelProperty(value = "包裹号")
    private String packageNumber;

    @ApiModelProperty(value = "客户自定义标识")
    private String customerTag;

    @ApiModelProperty(value = "尺寸")
    private Size size;

    @ApiModelProperty(value = "单个包裹重量")
    private Integer weightInGram;

    @ApiModelProperty(value = "包裹内物信息")
    private List<PackageItem> packageItems;
}
