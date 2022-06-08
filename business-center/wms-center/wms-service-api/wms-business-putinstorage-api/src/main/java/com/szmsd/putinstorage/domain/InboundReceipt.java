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

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "InboundReceipt", description = "InboundReceipt入库表")
public class InboundReceipt extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "采购单")
    private String orderNo;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "普通入库（OMS用）：Normal" +
            "集运入库（OMS用）：Collection" +
            "包裹转运入库（OMS用）：PackageTransfer" +
            "新SKU入库（OMS用）：NewSku" +
            "上架入库（Yewu用）：Putaway" +
            "点数入库（Yewu用）：Counting")
    private String orderType;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;

    @ApiModelProperty(value = "类别编码")
    private String warehouseCategoryCode;

    @ApiModelProperty(value = "VAT")
    private String vat;

    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ApiModelProperty(value = "送货单号")
    private String deliveryNo;

    @ApiModelProperty(value = "合计申报数量")
    private Integer totalDeclareQty;

    @ApiModelProperty(value = "合计上架数量")
    private Integer totalPutQty;

    @ApiModelProperty(value = "产品货源地编码")
    private String goodsSourceCode;
    @Deprecated
    @ApiModelProperty(value = "挂号")
    private String trackingNumber;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    private String status;

    @ApiModelProperty(value = "审核人")
    private String reviewBy;

    @ApiModelProperty(value = "审核人名称")
    private String reviewByName;

    @ApiModelProperty(value = "审核时间")
    private Date reviewTime;

    @ApiModelProperty(value = "审核备注")
    private String reviewRemark;

    @ApiModelProperty(value = "创建ID",hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID",hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "数据来源")
    private String sourceType;

    @ApiModelProperty(value = "揽收单号")
    private String collectionNo;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;
}
