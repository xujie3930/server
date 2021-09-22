package com.szmsd.doc.api.sku.request;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.putinstorage.enums.SourceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


/**
 * <p>
 *
 * </p>
 *
 * @author jr
 * @since 2021-03-04
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "包材新增对象")
public class BasePackingAddReq {

    @ApiModelProperty(value = "包材名称", example = "包材名称1", required = true)
    @Size(max = 255,message = "包材名称仅支持200字符")
    @NotBlank(message = "包材名称不能为空")
    private String productName;

    @ApiModelProperty(value = "包材编码")
    @Size(max = 50,message = "包材编码最多支持50字符")
    private String code;
    @DecimalMax(value = "999999", message = "初始重量最大仅支持999999 g")
    @DecimalMin(value = "0.01", message = "初始重量最小仅支持0.01 g")
    @ApiModelProperty(value = "初始重量g", example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始重量不能为空")
    private Double initWeight;
    @DecimalMax(value =  "500", message = "初始长度最大仅支持500 cm")
    @DecimalMin(value = "0.01", message = "初始长度最小仅支持0.01 cm")
    @ApiModelProperty(value = "初始长 cm", example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始长度不能为空")
    private Double initLength;
    @DecimalMax(value = "500", message = "初始宽度最大仅支持500 cm")
    @DecimalMin(value = "0.01", message = "初始宽度最小仅支持0.01 cm")
    @ApiModelProperty(value = "初始宽 cm", example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始宽度不能为空")
    private Double initWidth;
    @DecimalMax(value = "500", message = "初始高度最大仅支持500 cm")
    @DecimalMin(value = "0.01", message = "初始高度最小仅支持0.01 cm")
    @ApiModelProperty(value = "初始高 cm", example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始高度不能为空")
    private Double initHeight;

    @DecimalMin(value = "0.01", message = "初始体积最小仅支持0.01 cm3")
    @ApiModelProperty(value = "初始体积 cm3", example = "1", required = true, hidden = true)
    @Digits(integer = 14, fraction = 2,message = "体积格式仅支持2位小数,14位整数")
//    @NotNull(message = "初始体积不能为空")
    private BigDecimal initVolume;

    //    @NotBlank(message = "客户（卖家）编码不能为空")
    @ApiModelProperty(value = "客户（卖家）编码", hidden = true, example = "CNYWO7", required = true)
    @Excel(name = "客户（卖家）编码")
    @Size(max = 100)
    private String sellerCode;

    @ApiModelProperty(value = "包材内容说明", example = "包材说明")
    @Excel(name = "包材说明")
    @Size(max = 200,message = "包材内容说明最多支持200字符")
    private String productDescription;

    @ApiModelProperty(value = "类别", allowableValues = "sku", hidden = true)
    @Size(max = 20)
    private String category = "包材";


    @ApiModelProperty(value = "来源", hidden = true)
    private String source = SourceTypeEnum.DOC.name();

    public BasePackingAddReq calculateTheVolume() {
        // 4、体积，接口自动计算 体积如果为空，系统计算出来 新建SKU的时候， 体积给他用长*宽*高
        if (null == this.getInitVolume() || this.getInitVolume().compareTo(BigDecimal.ZERO) <= 0) {
            Double initLength = Optional.ofNullable(this.getInitLength()).orElse(0.00);
            Double initHeight = Optional.ofNullable(this.getInitHeight()).orElse(0.00);
            Double initWeight = Optional.ofNullable(this.getInitWidth()).orElse(0.00);
            double volume = initLength * initHeight * initWeight;
            BigDecimal bigDecimal = BigDecimal.valueOf(volume).setScale(2, RoundingMode.HALF_UP);
            this.setInitVolume(bigDecimal);
        }
        return this;
    }
}
