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
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "InventoryInspectionDetails", description = "InventoryInspectionDetails验货详情表")
public class InventoryInspectionDetails extends BaseEntity {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "申请单号")
    private String inspectionNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "创建ID", hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID", hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

}
