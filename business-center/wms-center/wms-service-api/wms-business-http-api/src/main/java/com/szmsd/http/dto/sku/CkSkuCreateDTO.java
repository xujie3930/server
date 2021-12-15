package com.szmsd.http.dto.sku;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.io.Serializable;
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
public class CkSkuCreateDTO implements Serializable {

    @Size(max = 100, message = "商家SKU长度: 0 ~ 100")
    @NotBlank(message = "商家SKU不能为空")
    @ApiModelProperty(value = "商家SKU", notes = "长度: 0 ~ 100", required = true)
    private String Sku;

    @Pattern(regexp = "^[a-zA-Z0-9-]{2,25}$", message = "自定义库存编码不符合")
    @ApiModelProperty(value = "自定义库存编码")
    private String CustomStorageNo;

    @NotBlank(message = "产品名称不能为空")
    @ApiModelProperty(value = "产品名称", required = true)
    private String ProductName;

    @Size(max = 2000, message = "产品描述长度不能大于2000")
    @NotBlank(message = "产品描述不能为空")
    @ApiModelProperty(value = "产品描述", notes = "长度: 0 ~ 2000", required = true)
    private String ProductDescription;

    @NotNull(message = "重量不能为空")
    @ApiModelProperty(value = "重量", notes = "范围: 1 ~ 2147483647", required = true)
    private Integer Weight;

    @NotNull(message = "长不能为空")
    @Digits(integer = 6, fraction = 2, message = "长不符合格式")
    @ApiModelProperty(value = "长（cm）", notes = "浮点数格式: 8,2", required = true)
    private BigDecimal Length;

    @NotNull(message = "宽不能为空")
    @Digits(integer = 6, fraction = 2, message = "宽不符合格式")
    @ApiModelProperty(value = "宽（cm）", notes = "浮点数格式: 8,2", required = true)
    private BigDecimal Width;

    @NotNull(message = "高不能为空")
    @Digits(integer = 6, fraction = 2, message = "高不符合格式")
    @ApiModelProperty(value = "高（cm）", notes = "浮点数格式: 8,2", required = true)
    private BigDecimal Height;

    @NotBlank(message = "申报名称不能为空")
    @Pattern(regexp = "(?![\\d\\s]+$)^[a-zA-Z_\\s0-9\\-\\(\\)\\'&,\\|]+$", message = "申报名称不符合格式")
    @ApiModelProperty(value = "申报名称", notes = "长度: 0 ~ 100", required = true)
    private String DeclareName;

    @NotNull(message = "申报价值不能为空")
    @Digits(integer = 16, fraction = 2, message = "申报价值不符合格式")
    @ApiModelProperty(value = "申报价值(USD)", notes = "浮点数格式: 18,2", required = true)
    private BigDecimal DeclareValue;

    @ApiModelProperty(value = "产品类型", notes = "")
    private ProductFlag ProductFlag;

    @ApiModelProperty(value = "库存警报", notes = "范围: 0 ~ 2147483647")
    private Integer ProductAmountWarn;

    @ApiModelProperty(value = "产品品类", notes = "长度: 0 ~ 50")
    private String ProductCategory;

    @ApiModelProperty(value = "产品备注", notes = "长度: 0 ~ 255")
    private String ProductRemark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

@Getter
@AllArgsConstructor
enum ProductFlag {
    /**
     * 一般产品
     */
    Simple,
    /**
     * 特殊产品
     */
    Special;

}
