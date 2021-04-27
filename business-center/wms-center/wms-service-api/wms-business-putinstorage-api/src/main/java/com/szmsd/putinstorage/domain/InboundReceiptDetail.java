package com.szmsd.putinstorage.domain;

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


/**
 * <p>
 * rec_wareh_detail - 入库明细
 * </p>
 *
 * @author liangchao
 * @since 2021-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDetail", description = "InboundReceiptDetail入库明细表")
public class InboundReceiptDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "申报品名")
    private String skuName;

    @ApiModelProperty(value = "申报数量")
    private Integer declareQty;

    @ApiModelProperty(value = "上架数量")
    private Integer putQty;

    @ApiModelProperty(value = "原产品编码")
    private String originCode;

    @ApiModelProperty(value = "创建ID",hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID",hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "出库单号")
    private String deliveryNo;
}
