package com.szmsd.inventory.domain.excel;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "PurchaseInfoDetailExcle对象")
public class PurchaseInfoDetailExcle {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称",needMerge = true)
    private String productName;

    @ApiModelProperty(value = "产品描述")
    @Excel(name = "产品说明",width = 30,needMerge = true)
    private String productDescription;

    @ApiModelProperty(value = "产品编号")
    @Excel(name = "产品编号",width = 30,needMerge = true)
    private String sku;

    @ApiModelProperty(value = "采购数量")
    @Excel(name = "采购数量",width = 15,needMerge = true)
    private Integer purchaseQuantity;

    /**
     集合
     **/
    @ExcelCollection(name = "采购单产品的快递/揽收明细")
    private List<PurchaseStorageDetailsExcle> purchaseStorageDetailsExcles;


}
