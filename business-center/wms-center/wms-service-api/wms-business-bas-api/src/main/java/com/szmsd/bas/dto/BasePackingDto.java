package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasePacking;
import com.szmsd.common.core.annotation.Excel;
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
public class BasePackingDto extends BasePacking {
    @ApiModelProperty(value = "价格范围")
    private String priceRange;
}
