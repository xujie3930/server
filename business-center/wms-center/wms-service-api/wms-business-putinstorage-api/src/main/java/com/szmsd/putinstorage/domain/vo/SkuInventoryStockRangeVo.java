package com.szmsd.putinstorage.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @ClassName: SkuInventoryAgeVo
 * @Description: sku库龄
 * @Author: 11
 * @Date: 2021-08-06 9:22
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "sku入库状况")
public class SkuInventoryStockRangeVo {
    @ApiModelProperty(value = "sku", example = "SKU001")
    private String sku;
    @ApiModelProperty(value = "SKU名", example = "SKU001")
    private String skuName;
    @ApiModelProperty(value = "预报总数量", example = "10")
    private Long forecastTotal;
    @ApiModelProperty(value = "实际上架数量", example = "1")
    private Long actualOnShelvesTotal;
    @ApiModelProperty(value = "在途总数量", example = "9")
    private Long inTransitTotal;
}
