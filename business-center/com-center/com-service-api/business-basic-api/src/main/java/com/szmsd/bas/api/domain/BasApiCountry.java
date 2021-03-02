package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
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
    * 第三方接口 - 国家表
    * </p>
*
* @author admin
* @since 2021-01-20
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="第三方接口 - 国家表", description="BasApiCountry对象")
public class BasApiCountry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String name;

    @ApiModelProperty(value = "描述（网络域名）")
    @Excel(name = "描述（网络域名）")
    private String description;

    @ApiModelProperty(value = "经度")
    @Excel(name = "经度")
    private BigDecimal lon;

    @ApiModelProperty(value = "纬度")
    @Excel(name = "纬度")
    private BigDecimal lat;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;


}
