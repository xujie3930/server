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
@ApiModel(value = "InventoryRecord", description = "InventoryRecord库存记录表")
public class InventoryRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "单据号")
    private String receiptNo;

    @ApiModelProperty(value = "类型：1入库、2出库")
    private String type;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "之前总库存")
    private Integer beforeTotalInventory;

    @ApiModelProperty(value = "之前可用库存")
    private Integer beforeAvailableInventory;

    @ApiModelProperty(value = "之前冻结库存")
    private Integer beforeFreezeInventory;

    @ApiModelProperty(value = "之前总入库")
    private Integer beforeTotalInbound;

    @ApiModelProperty(value = "之前总出库")
    private Integer beforeTotalOutbound;

    @ApiModelProperty(value = "之后总库存")
    private Integer afterTotalInventory;

    @ApiModelProperty(value = "之后可用库存")
    private Integer afterAvailableInventory;

    @ApiModelProperty(value = "之后原冻结库存")
    private Integer afterFreezeInventory;

    @ApiModelProperty(value = "之后原总入库")
    private Integer afterTotalInbound;

    @ApiModelProperty(value = "之后原总出库")
    private Integer afterTotalOutbound;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "占位符内容，多个值用 “,\" 隔开")
    private String placeholder;

    @ApiModelProperty(value = "创建ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;


}
