package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "TpieceVO", description = "TpieceVO对象")
public class TpieceVO {

    @ApiModelProperty(value = "partnerCode")
    private String partnerCode;

    @ApiModelProperty(value = "hash")
    private String hash;

    @ApiModelProperty(value = "job")
    private String job;


}
