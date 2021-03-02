package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-09-24
 */
@TableName("bas_car")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCar对象", description = "车辆管理")
public class BasCar {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车辆编号")
    @Excel(name = "车辆编号")
    private String carCode;

    @ApiModelProperty(value = "车辆品牌名称")
    @Excel(name = "车辆品牌名称")
    private String carName;

    @ApiModelProperty(value = "车牌号")
    @Excel(name = "车牌号")
    private String carBrand;

    @ApiModelProperty(value = "车架号")
    @Excel(name = "车架号")
    private String carNumber;

    @ApiModelProperty(value = "车型")
    @Excel(name = "车型")
    private String carShape;

    @ApiModelProperty(value = "供应商")
    @Excel(name = "供应商")
    private String supplier;

    @ApiModelProperty(value = "载重")
    @Excel(name = "载重")
    private String loads;

    @ApiModelProperty(value = "体积")
    @Excel(name = "体积")
    private String volume;

    @ApiModelProperty(value = "车辆规格")
    @Excel(name = "车辆规格")
    private String carSpecifications;

    @ApiModelProperty(value = "车辆颜色")
    @Excel(name = "车辆颜色")
    private String carColour;

    @ApiModelProperty(value = "司机")
    @Excel(name = "司机")
    private String driver;

    @ApiModelProperty(value = "司机手机号")
    @Excel(name = "司机手机号")
    private String driverPhone;

    @ApiModelProperty(value = "创建人id、")
    @Excel(name = "创建人id、")
    private String createId;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "所属网点code")
    @Excel(name = "所属网点code")
    private String belongSiteCode;

    @ApiModelProperty(value = "所属网点name")
    @Excel(name = "所属网点name")
    private String belongSiteName;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateId;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;
}
