package com.szmsd.doc.api.warehouse.resp;

import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SkuInventoryAgeVo
 * @Description: sku库龄
 * @Author: 11
 * @Date: 2021-08-06 9:22
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "sku库龄")
public class SkuInventoryAgeResp {

    @ApiModelProperty(value = "sku", example = "SCNYWO7000214")
    private String sku;
    @ApiModelProperty(value = "仓库code", example = "NJ")
    private String warehouseCode;
    @ApiModelProperty(value = "sku库龄详情")
    private List<SkuInventoryAgeDetailsResp> details;

    public static List<SkuInventoryAgeResp> convert(List<SkuInventoryAgeVo> skuInventoryAgeList, String warehouseCode, String sku) {
        List<SkuInventoryAgeResp> skuInventoryAgeResps = new ArrayList<>();
        SkuInventoryAgeResp skuInventoryAgeResp = new SkuInventoryAgeResp();
        skuInventoryAgeResp.setSku(sku).setWarehouseCode(warehouseCode);
        List<SkuInventoryAgeDetailsResp> details = skuInventoryAgeList.stream().map(x -> {
            SkuInventoryAgeDetailsResp skuInventoryAgeDetailsResp = new SkuInventoryAgeDetailsResp();
            BeanUtils.copyProperties(x, skuInventoryAgeDetailsResp);
            return skuInventoryAgeDetailsResp;
        }).collect(Collectors.toList());
        skuInventoryAgeResp.setDetails(details);
        skuInventoryAgeResps.add(skuInventoryAgeResp);
        return skuInventoryAgeResps;
    }
}
