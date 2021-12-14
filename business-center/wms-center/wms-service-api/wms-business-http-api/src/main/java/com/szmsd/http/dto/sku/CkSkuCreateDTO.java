package com.szmsd.http.dto.sku;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @ClassName: CkSkuCreateDTO
 * @Description: sku创建对象CK1
 * @Author: 11
 * @Date: 2021-12-14 11:55
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "sku创建-CK1")
public class CkSkuCreateDTO {
    @NotBlank
    @ApiModelProperty(value = "商家SKU", notes = "长度: 0 ~ 100", required = true)
    private String Sku;

    @Pattern(regexp = "^[a-zA-Z0-9-]{2,25}$")
    @ApiModelProperty(value = "自定义库存编码")
    private String CustomStorageNo;

    @NotBlank
    @ApiModelProperty(value = "产品名称", required = true)
    private String ProductName;

    @NotBlank
    @ApiModelProperty(value = "产品描述", notes = "长度: 0 ~ 2000", required = true)
    private String ProductDescription;

    @NotNull
    @ApiModelProperty(value = "产品描述", notes = "范围: 1 ~ 2147483647", required = true)
    private Integer Weight;
    @NotBlank
    @ApiModelProperty(value = "长（cm）", notes = "浮点数格式: 8,2", required = true)
    private BigDecimal Length;

    @ApiModelProperty(value = "宽（cm）", notes = "浮点数格式: 8,2", required = true)
    private String Width;

    @ApiModelProperty(value = "高（cm）", notes = "浮点数格式: 8,2", required = true)
    private String Height;

    @Pattern(regexp = "(?![\\d\\s]+$)^[a-zA-Z_\\s0-9\\-\\(\\)\\'&,\\|]+$")
    @ApiModelProperty(value = "申报名称", notes = "长度: 0 ~ 100", required = true)
    private String DeclareName;

    @ApiModelProperty(value = "申报价值(USD)", notes = "浮点数格式: 18,2", required = true)
    private Integer DeclareValue;

    @ApiModelProperty(value = "产品类型", notes = "")
    private ProductFlag ProductFlag;

    @ApiModelProperty(value = "库存警报", notes = "范围: 0 ~ 2147483647")
    private Integer ProductAmountWarn;

    @ApiModelProperty(value = "产品品类", notes = "长度: 0 ~ 50")
    private String ProductCategory;

    @ApiModelProperty(value = "产品备注", notes = "长度: 0 ~ 255")
    private String ProductRemark;

}

@Getter
@AllArgsConstructor
enum ProductFlag {
    /**
     * 一般产品
     */
    Simple,
    /**
     *
     */
    Special;

}
