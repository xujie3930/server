package com.szmsd.inventory.domain.dto;

import com.google.common.collect.Lists;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuQueryDTO", description = "InventorySkuQueryDTO库存管理 - 查询入参")
public class InventorySkuQueryDTO {

    @ApiModelProperty(value = "主键ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "主键ID集合")
    private List<Long> ids;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "产品属性")
    private String skuPropertyCode;

    @ApiModelProperty(value = "sku多个用\",\"隔开")
    private String sku;

    @ApiModelProperty(value = "多个sku的和之前的不冲突加的")
    private String skus;

    public void setSkus(String skus) {
        this.skus = skus;
        Optional.ofNullable(skus).filter(StringUtils::isNotBlank).ifPresent(x -> this.skuList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "sku集合")
    private List<String> skuList;

    @ApiModelProperty(value = "申报品名")
    private String skuDeclaredName;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "客户编码list")
    private List<String> cusCodeList;



    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
        this.cusCodeList = StringUtils.isNotEmpty(cusCode) ? Arrays.asList(cusCode.split(",")) : Lists.newArrayList();
    }
}
