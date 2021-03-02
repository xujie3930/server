package com.szmsd.bas.api.domain;

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
 * 路由表
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
@TableName("bas_route")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasRoute对象", description = "路由表")
public class BasRoute {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "路由code")
    @Excel(name = "路由code")
    private String routeCode;

    @ApiModelProperty(value = "路由name")
    @Excel(name = "路由name")
    private String routeName;

    @ApiModelProperty(value = "起始站")
    @Excel(name = "起始站")
    private String startStation;

    @ApiModelProperty(value = "起始站code")
    @Excel(name = "起始站")
    private String startStationCode;

    @ApiModelProperty(value = "结束站")
    @Excel(name = "结束站")
    private String endStation;

    @ApiModelProperty(value = "结束站code")
    @Excel(name = "结束站")
    private String endStationCode;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createBy;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人io")
    private String updateBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建网点code")
    @Excel(name = "创建网点code")
    private String createSiteCode;

    @ApiModelProperty(value = "创建网点")
    @Excel(name = "创建网点")
    private String createSiteName;

    @ApiModelProperty(value = "修改网点code")
    @Excel(name = "修改网点code")
    private String updateSiteCode;

    @ApiModelProperty(value = "修改网点")
    @Excel(name = "修改网点")
    private String updateSiteName;

}
