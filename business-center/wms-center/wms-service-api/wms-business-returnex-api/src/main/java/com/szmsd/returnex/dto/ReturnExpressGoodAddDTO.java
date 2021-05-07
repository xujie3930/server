package com.szmsd.returnex.dto;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.exception.com.AssertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @Min(value = 0, message = "SKU到库数量不能小于0")
    @NotNull(message = "SKU到库数量不能为空")
    @ApiModelProperty(value = "SKU到库数量")
    @Excel(name = "SKU到库数量")
    private Integer skuNumber;

    @ApiModelProperty(value = "仓库上架数量（保留字段）")
    @Excel(name = "仓库上架数量")
    private Integer warehouseQty;
    /* @Min(value = 0, message = "上架数量最少为0")
     @NotNull(message = "上架数量不能为空")*/
    @ApiModelProperty(value = "上架数量")
    @Excel(name = "上架数量")
    private Integer putawayQty;
    /* @NotEmpty(message = "新上架的SKU编码不能为空")
     @ApiModelProperty(value = "新上架编码")*/
    @Excel(name = "新上架编码")
    private String putawaySku;

    @ApiModelProperty(value = "SKU处理备注")
    @Excel(name = "SKU处理备注")
    private String processRemark;

    /**
     * 入库校验 只有上架才调用，销毁不调用
     */
    public void check() {
        //上架需要校验
        boolean present = Optional.ofNullable(putawayQty).filter(x -> x >= 0).filter(x -> x <= skuNumber).isPresent();
        AssertUtil.isTrue(present, "上架数量必须大于等于0 且小于等于到库数量");
        boolean present2 = Optional.ofNullable(putawaySku).filter(StringUtils::isNotEmpty).isPresent();
        AssertUtil.isTrue(present2, "上架的sku不能为空");
    }
}
