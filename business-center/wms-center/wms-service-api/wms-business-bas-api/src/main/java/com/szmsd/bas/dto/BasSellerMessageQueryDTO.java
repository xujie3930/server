package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSellerMessage;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerMessageQueryDTO extends BasSellerMessage {
    @ApiModelProperty(value = "创建时间-查询使用")
    @Excel(name = "创建时间-查询使用")
    private  String[] createTimes;


    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String type;
}
