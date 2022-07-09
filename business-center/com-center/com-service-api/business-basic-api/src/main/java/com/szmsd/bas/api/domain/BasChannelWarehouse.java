package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="渠道节点维护仓库表", description="BasChannelWarehouse对象")
public class BasChannelWarehouse {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "渠道主表id")
    private Integer channelId;

    @ApiModelProperty(value = "仓库code")
    @Excel(name = "仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名")
    @Excel(name = "仓库名")
    private String warehouseName;


}