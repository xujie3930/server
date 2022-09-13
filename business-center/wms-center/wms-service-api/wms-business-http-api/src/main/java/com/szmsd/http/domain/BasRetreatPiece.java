package com.szmsd.http.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "退件国外临时对接表", description = "BasRetreatPiece对象")
public class BasRetreatPiece {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "partnerCode")
    private String partnerCode;

    @ApiModelProperty(value = "receptionWmsId")
    private Integer receptionWmsId;

    @ApiModelProperty(value = "dateFinished")
    private Date dateFinished;

    @ApiModelProperty(value = "receptionService")
    private String receptionService;

    @ApiModelProperty(value = "codeKey")
    private String codeKey;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "um")
    private String um;

    @ApiModelProperty(value = "qtyReceived")
    private Integer qtyReceived;

    @ApiModelProperty(value = "sn")
    private String sn;

    @ApiModelProperty(value = "ua")
    private String ua;


    @ApiModelProperty(value = "0表示已经推送，1表示还没推送")
    private Integer state;


}