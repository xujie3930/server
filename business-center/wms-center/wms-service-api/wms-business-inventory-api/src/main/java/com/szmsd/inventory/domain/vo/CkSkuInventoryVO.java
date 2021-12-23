package com.szmsd.inventory.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName: CkSkuInventoryVO
 * @Description: SKU库存记录情况
 * @Author: 11
 * @Date: 2021-12-22 16:34
 */
@NoArgsConstructor
@Data
@ApiModel(description = "CK1-SKU库存情况返回结果对象")
public class CkSkuInventoryVO {
    @ApiModelProperty(value = "仓库Id")
    private String warehouseId;
    @ApiModelProperty(value = "商家Sku")
    private String sku;
    @ApiModelProperty(value = "出口易Sku")
    private String ck1Sku;
    @ApiModelProperty(value = "库存编码")
    private String storageNo;
    @ApiModelProperty(value = "重量(g)")
    private Integer weight;
    @ApiModelProperty(value = "长(cm)")
    private Integer length;
    @ApiModelProperty(value = "宽(cm)")
    private Integer width;
    @ApiModelProperty(value = "高(cm)")
    private Integer height;
    @ApiModelProperty(value = "申报名称")
    private String declareName;
    @ApiModelProperty(value = "中文申报名称")
    private String declareNameCn;
    @ApiModelProperty(value = "申报价值(USD)")
    private Integer declareValue;
    @ApiModelProperty(value = "产品类型")
    private ProductFlag productFlag;
    @ApiModelProperty(value = "实际库存数")
    private Integer totalStockQty;
    @ApiModelProperty(value = "可用库存数")
    private Integer availStockQty;
    @ApiModelProperty(value = "在途库存数")
    private Integer inTransitStockQty;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}

@Getter
enum ProductFlag {
    /**
     * 一般产品
     */
    Simple,
    /**
     * 特殊产品
     */
    Special
}