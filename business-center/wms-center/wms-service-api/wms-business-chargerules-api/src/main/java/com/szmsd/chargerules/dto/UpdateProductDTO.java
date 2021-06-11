package com.szmsd.chargerules.dto;

import com.szmsd.chargerules.vo.PricedProductInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UpdateProductDTO", description = "修改产品服务")
public class UpdateProductDTO extends PricedProductInfoVO {

}