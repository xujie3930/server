package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author zhangyuyuan
 * @date 2020-04-23 17:30
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "GenerateNumberDto", description = "流水号")
public class GenerateNumberDto {

    @NotBlank(message = "业务编码不能为空")
    @ApiModelProperty(value = "业务编码")
    private String code;

    @Min(value = 1, message = "生成个数不能小于1")
    @ApiModelProperty(value = "生成个数")
    private int num;

    public GenerateNumberDto() {
        this.num = 1;
    }

    public GenerateNumberDto(String code) {
        this.code = code;
        this.num = 1;
    }

    public GenerateNumberDto(String code, int num) {
        this.code = code;
        this.num = num;
    }
}
