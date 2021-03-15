package com.szmsd.inventory.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "Inventory", description = "Inventory库存表")
public class Inventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "总库存")
    private Integer totalInventory;

    @ApiModelProperty(value = "可用库存")
    private Integer availableInventory;

    @ApiModelProperty(value = "冻结库存")
    private Integer freezeInventory;

    @ApiModelProperty(value = "总入库")
    private Integer totalInbound;

    @ApiModelProperty(value = "总出库")
    private Integer totalOutbound;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "创建ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;


}
