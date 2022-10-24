package com.szmsd.http.dto.discount;

import com.szmsd.http.dto.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DiscountPage extends PageDTO {

    @ApiModelProperty(value = "id")
    private String id;
}
