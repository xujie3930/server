package com.szmsd.delivery.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 *
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "", description = "DelTrack对象")
public class DelTrack extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

    @ApiModelProperty(value = "运输包裹的物流跟踪号")
    @Excel(name = "运输包裹的物流跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "运输包裹的运输商编码")
    @Excel(name = "运输包裹的运输商编码")
    private String carrierCode;

    @ApiModelProperty(value = "运输包裹 Id，代表唯一的一个运输包裹信息")
    @Excel(name = "运输包裹 Id，代表唯一的一个运输包裹信息")
    private String shipmentId;

    @ApiModelProperty(value = "运输包裹的订单号，可以是电商平台的订单号，也可以是用 于自己做唯一标示的号。")
    @Excel(name = "运输包裹的订单号，可以是电商平台的订单号，也可以是用 于自己做唯一标示的号。")
    private String orderNo;

    @ApiModelProperty(value = "轨迹状态")
    @Excel(name = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = " 序号，每单轨迹从 1 开始")
    @Excel(name = " 序号，每单轨迹从 1 开始")
    private Integer no;

    @ApiModelProperty(value = "轨迹信息描述")
    @Excel(name = "轨迹信息描述")
    private String description;

    @ApiModelProperty(value = "轨迹时间")
    @Excel(name = "轨迹时间")
    private Date trackingTime;

    @ApiModelProperty(value = "动作节点")
    @Excel(name = "动作节点")
    private String actionCode;

    @ApiModelProperty(value = "物流轨迹发生的位置显示描述")
    @Excel(name = "物流轨迹发生的位置显示描述")
    private String display;

    @ApiModelProperty(value = "包裹所处的国家编码")
    @Excel(name = "包裹所处的国家编码")
    private String countryCode;

    @ApiModelProperty(value = "国家或地区名称(英文)")
    @Excel(name = "国家或地区名称(英文)")
    private String countryNameEn;

    @ApiModelProperty(value = "国家或地区名称（中文）")
    @Excel(name = "国家或地区名称（中文）")
    private String countryNameCn;

    @ApiModelProperty(value = "省")
    @Excel(name = "省")
    private String province;

    @ApiModelProperty(value = "市")
    @Excel(name = "市")
    private String city;

    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String postcode;

    @ApiModelProperty(value = "街道1")
    @Excel(name = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    @Excel(name = "街道2")
    private String street2;

    @ApiModelProperty(value = "街道3")
    @Excel(name = "街道3")
    private String street3;


}
