package com.szmsd.returnex.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Optional;

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
    @Min(0)
    @ApiModelProperty(value = "数量")
    private Integer qty;

    public void setQty(Integer qty) {
        this.qty = qty;
        Optional.ofNullable(qty).filter(x -> x > 0).ifPresent(this::setSkuNumber);
    }

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU")
    private String sku;
    /**
     * SKU处理备注 0-500
     */
    @ApiModelProperty(value = "备注")
    private String remark;

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
    private String putawaySku;

    @ApiModelProperty(value = "SKU处理备注")
    @Excel(name = "SKU处理备注")
    private String processRemark;
}
