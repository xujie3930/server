package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="FBA仓库信息", description="BasFba对象")
public class BasFba extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;
    @ApiModelProperty(value = "fba仓库code")
    @Excel(name = "fba仓库code")
    private String fbaCode;

    @ApiModelProperty(value = "fba仓库名称")
    @Excel(name = "fba仓库名称")
    private String fbaName;
    @ApiModelProperty(value = "地址一")
    @Excel(name = "地址一")
    private String addressNameOn;
    @ApiModelProperty(value = "地址二")
    @Excel(name = "地址二")
    private String addressNameTwo;
    @ApiModelProperty(value = "城市")
    @Excel(name = "城市")
    private String cityName;
    @ApiModelProperty(value = "州/省")
    @Excel(name = "州/省")
    private String provinceName;
    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String countryName;
    @ApiModelProperty(value = "国家简码")
    @Excel(name = "国家简码")
    private String countryCode;

    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String postcode;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;





    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "删除标识：0未删除 2已删除")
    private String delFlag;

}