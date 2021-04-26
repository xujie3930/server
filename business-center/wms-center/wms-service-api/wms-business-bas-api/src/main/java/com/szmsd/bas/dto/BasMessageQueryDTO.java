package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasMessage;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasMessageQueryDTO extends BasMessage {
    @ApiModelProperty(value = "创建时间-查询使用")
    @Excel(name = "创建时间-查询使用")
    private  String[] createTimes;
}
