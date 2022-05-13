package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @ClassName:LabelCountDTO
 * @Description:标签状态数量实体类
 * @version V1.0
 */
@Data
public class LabelCountDTO {
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String status;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer count;
}
