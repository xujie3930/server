package com.szmsd.inventory.domain.excel;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "采购入库列表信息")
public class PurchaseStorageDetailsExcle {



    @ApiModelProperty(value = "快递/揽收单号")
    @Excel(name = "快递/揽收单号",width = 30,needMerge = true)
    private String deliveryNo;

//    @ApiModelProperty(value = "产品编号")
//    @Excel(name = "产品编号")
//    private String sku;

    @ApiModelProperty(value = "申报数量")
    @Excel(name = "申报数量",width = 15,needMerge = true)
    private Integer declareQty;

    @ApiModelProperty(value = "采购单信息的sku")
    private String sku;

    @ApiModelProperty(value = "associationId")
    private String associationId;
}
