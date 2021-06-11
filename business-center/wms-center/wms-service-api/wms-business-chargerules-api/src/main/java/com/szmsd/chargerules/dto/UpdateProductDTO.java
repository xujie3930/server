package com.szmsd.chargerules.dto;

import com.szmsd.chargerules.vo.PricedProductInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UpdateProductDTO", description = "修改产品服务")
public class UpdateProductDTO extends PricedProductInfoVO {

    @ApiModelProperty(value = "子产品")
    private List<String> subProducts;

}