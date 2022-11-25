package com.szmsd.delivery.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BasShipmentRulesDto", description = "BasShipmentRulesDto对象")
public class BasShipmentRulesDto extends QueryDto {


    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "服务渠道名称")
    private String serviceChannelName;

    @ApiModelProperty(value = "推送时间")
    private String pushDate;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "修改人编号")
    private String delFlag;

    @ApiModelProperty(value = "集合 删除用")
    private List<Integer> ids;

    @ApiModelProperty(value = "子产品")
    private String productCodeSon;

    @ApiModelProperty(value = "(需要(1)/不需要(0)，默认0）")
    private String iossType;

    @ApiModelProperty(value = "子产品")
    private String serviceChannelSub;

    @ApiModelProperty(value = "功能模块")
    private String typeName;


}