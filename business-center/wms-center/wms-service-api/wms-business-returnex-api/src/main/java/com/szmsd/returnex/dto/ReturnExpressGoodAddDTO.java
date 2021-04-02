package com.szmsd.returnex.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * @ClassName: ReturnExpressGoodVO
 * @Description: 退件-sku列表数据
 * @Author: 11
 * @Date: 2021/4/2 14:22
 */
@Data
public class ReturnExpressGoodAddDTO {

    @Min(value = 1, message = "id异常")
    @ApiModelProperty(value = "主键ID")
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "关联退货单主键id", notes = "后端关联")
    @Excel(name = "关联退货单主键id")
    private Integer associationId;

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "SKU到库数量")
    @Excel(name = "SKU到库数量")
    private Integer skuNumber;

    @ApiModelProperty(value = "仓库上架数量")
    @Excel(name = "仓库上架数量")
    private Integer warehouseQty;

    @ApiModelProperty(value = "上架数量")
    @Excel(name = "上架数量")
    private Integer putawayQty;

    @ApiModelProperty(value = "新上架编码")
    @Excel(name = "新上架编码")
    private Integer putawaySku;

    @ApiModelProperty(value = "SKU处理备注")
    @Excel(name = "SKU处理备注")
    private String processRemark;
}
