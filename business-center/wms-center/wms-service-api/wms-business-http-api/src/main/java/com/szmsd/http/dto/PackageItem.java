package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 13:51
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PackageItem")
public class PackageItem {

    @ApiModelProperty(value = "申报名称")
    private String declareName;

    @ApiModelProperty(value = "单件申报价值")
    private Double declareValue;

    @ApiModelProperty(value = "单件申报重量，单位克")
    private Integer weightInGram;

    @ApiModelProperty(value = "尺寸")
    private Size size;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "海关编码")
    private String hsCode;

    @ApiModelProperty(value = "客户自定义标识")
    private String customerTag;

    @ApiModelProperty(value = "库存编码")
    private String storageNumber;
}
