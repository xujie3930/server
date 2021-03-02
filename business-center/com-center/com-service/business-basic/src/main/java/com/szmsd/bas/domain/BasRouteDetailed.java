package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 路由明细表
 * </p>
 *
 * @author ziling
 * @since 2020-11-11
 */
@TableName("bas_route_detailed")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasRouteDetailed对象", description = "路由明细表")
public class BasRouteDetailed {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "路由编号")
    @Excel(name = "路由编号")
    private String routeCode;

    @ApiModelProperty(value = "路由名称")
    @Excel(name = "路由名称")
    private String routeName;

    @ApiModelProperty(value = "开始站")
    @Excel(name = "开始站")
    private String startStation;

    @ApiModelProperty(value = "开始站code")
    @Excel(name = "开始站")
    private String startStationCode;

    @ApiModelProperty(value = "结束站")
    @Excel(name = "结束站")
    private String endStation;

    @ApiModelProperty(value = "结束站code")
    @Excel(name = "结束站")
    private String endStationCode;

    @ApiModelProperty(value = "路由网点code")
    @Excel(name = "路由网点code")
    private String routeSiteCode;

    @ApiModelProperty(value = "路由网点")
    @Excel(name = "路由网点")
    private String routeSiteName;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createBy;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建网点code")
    @Excel(name = "创建网点code")
    private String createSiteCode;

    @ApiModelProperty(value = "创建网点")
    @Excel(name = "创建网点")
    private String createSiteName;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "修改网点code")
    @Excel(name = "修改网点code")
    private String updateSiteCode;

    @ApiModelProperty(value = "修改网点")
    @Excel(name = "修改网点")
    private String updateSiteName;

}
