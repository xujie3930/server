package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasMessage;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasMessageDto extends BasMessage {


    @ApiModelProperty(value = "是否读取")
    @Excel(name = "是否读取")
    private Boolean readable;
}
