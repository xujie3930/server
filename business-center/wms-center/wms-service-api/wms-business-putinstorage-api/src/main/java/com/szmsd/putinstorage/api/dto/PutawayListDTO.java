package com.szmsd.putinstorage.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "CK1-sku入库上架推送-sku明细")
public class PutawayListDTO {
    @NotBlank(message = "SKU不能为空")
    @ApiModelProperty(value = "SKU", notes = "长度:0 ~ 255")
    private String Sku;
    @NotNull(message = "数量不能为空")
    @ApiModelProperty(value = "数量", notes = "长度:0 ~ 255")
    private Integer Qty;
}