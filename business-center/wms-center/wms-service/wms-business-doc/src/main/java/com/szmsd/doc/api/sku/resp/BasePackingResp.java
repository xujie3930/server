package com.szmsd.doc.api.sku.resp;

import com.szmsd.bas.domain.BasePacking;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @FileName BasePackingDto
 * @Description ---------- ---------
 * @Date 2021-05-28
 * @Author jr
 * @Version 1.0
 */
@Data
@ApiModel(description = "物流包装信息")
public class BasePackingResp {
    @ApiModelProperty(value = "包材类别")
    private String packingMaterialType;

//    @ApiModelProperty(value = "包材代码，例如B001")
//    private String packageMaterialCode;

//    @ApiModelProperty(value = "包材名称")
//    private String packageMaterialName;

    @ApiModelProperty(value = "价格范围")
    private String priceRange;
}
