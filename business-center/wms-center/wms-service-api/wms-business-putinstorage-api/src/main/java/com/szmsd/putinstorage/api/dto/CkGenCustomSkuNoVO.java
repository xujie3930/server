package com.szmsd.putinstorage.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @ClassName: CkGenCustomSkuNoDTO
 * @Description: 获取Sku库存编码 创建sku库存编码
 * @Author: 11
 * @Date: 2022-01-04 10:39
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ApiModel(description = "CK1-获取Sku库存编码-生成自定义库存编码")
public class CkGenCustomSkuNoVO {

    @NotBlank(message = "仓库代号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,10}$", message = "订单Id格式不支持")
    private String WarehouseId;

    @ApiModelProperty(value = "Sku", notes = "长度: 0 ~ 100", required = true)
    private String Sku;

    @NotBlank(message = "订单Id不能为空")
    @Size(max = 50, message = "订单Id最大仅支持50字符")

    @ApiModelProperty(value = "自定义库存编码", notes = "长度: 0 ~ 100", required = true)
    private String StorageNo;
}
