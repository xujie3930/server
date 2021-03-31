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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "InventoryCounting", description = "Inventory申请盘点结果表")
public class InventoryCounting extends BaseEntity {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    @NotBlank(message = "业务主键不能为空")
    private String transactionId;

    @ApiModelProperty(value = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @ApiModelProperty(value = "所属盘点单号")
    private String orderNo;

    @NotNull
    @ApiModelProperty(value = "盘点时系统数量")
    private Integer systemQty;

    @NotNull
    @ApiModelProperty(value = "当时盘点数量")
    private Integer countingQty;

    @NotNull
    @ApiModelProperty(value = "差异数量，等于盘点数量减去系统数量")
    private Integer diffQty;

    @ApiModelProperty(value = "创建ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

}
