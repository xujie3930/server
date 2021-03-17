package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PageDTO {

   @ApiModelProperty(value = "当前页码")
   private Integer pageNumber;

   @ApiModelProperty(value = "页面大小")
   private Integer pageSize;

}
