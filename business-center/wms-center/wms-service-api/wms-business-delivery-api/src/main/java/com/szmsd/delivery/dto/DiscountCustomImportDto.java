package com.szmsd.delivery.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "DiscountCustomImportDto", description = "折扣方案-导入关联客户")
public class DiscountCustomImportDto {


    @Excel(name = "客户代码")
    private String clientCode;

    @Excel(name = "生效时间")
    private String beginTime;

    @Excel(name = "截止时间")
    private String endTime;

    @Excel(name = "是否有效")
    private Boolean isValid;























}
