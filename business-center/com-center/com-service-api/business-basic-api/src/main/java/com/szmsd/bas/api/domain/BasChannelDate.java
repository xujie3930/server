package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="渠道节点维护从表", description="BasChannelDate对象")
public class BasChannelDate {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "渠道主表id")
    private Integer channelId;

    @ApiModelProperty(value = "预计时效")
    @Excel(name = "预计时效")
    private String estimateWholeTime;

    @ApiModelProperty(value = "排序")
    @Excel(name = "排序")
    private Integer sortId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "发货节点code")
    @Excel(name = "发货节点code")
    private String shipmentNodeCode;

    @ApiModelProperty(value = "发货节点名称")
    @Excel(name = "发货节点名称")
    private String shipmentNodeName;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remarks;

}