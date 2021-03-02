package com.szmsd.bas.domain.dto;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
* <p>
    * 第三方接口 - 城市表
    * </p>
*
* @author admin
* @since 2021-01-20
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="第三方接口 - 城市表", description="BasApiCityDto对象")
public class BasApiCityDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "国家id")
    @Excel(name = "国家id")
    private Integer countryId;

    @ApiModelProperty(value = "实际地名")
    @Excel(name = "实际地名")
    private String actual_place_name;

    @ApiModelProperty(value = "城市名称")
    @Excel(name = "城市名称")
    private String name;

    @ApiModelProperty(value = "描述")
    @Excel(name = "描述")
    private String description;

    @ApiModelProperty(value = "经度")
    @Excel(name = "经度")
    private BigDecimal lon;

    @ApiModelProperty(value = "纬度")
    @Excel(name = "纬度")
    private BigDecimal lat;
}
