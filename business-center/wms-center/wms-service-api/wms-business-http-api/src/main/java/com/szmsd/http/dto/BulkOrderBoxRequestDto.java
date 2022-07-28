package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BulkOrderBoxRequestDto", description = "BulkOrderBoxRequestDto对象")
public class BulkOrderBoxRequestDto implements Serializable {

    @ApiModelProperty(value = "箱号")
    private String BoxNo;
}
