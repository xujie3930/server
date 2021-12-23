package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName: CkSkuInventoryQueryDTO
 * @Description: CK1 sku查询库存对象
 * @Author: 11
 * @Date: 2021-12-22 17:07
 */
@NoArgsConstructor
@Data
@ApiModel(description = "CK1-sku查询库存对象")
public class CkSkuInventoryQueryDTO {

    @Size(max = 30, message = "仓库Id最多仅支持30个字符")
    @ApiModelProperty(value = "仓库Id", notes = "长度: 0 ~ 30")
    private String warehouseId;

    @Size(max = 30, message = "商家SKU最多仅支持30条")
    @ApiModelProperty(value = "商家SKU，最多30个")
    private List<String> skus;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
