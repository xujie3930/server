package com.szmsd.putinstorage.domain.dto;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.putinstorage.api.dto.CkPutawayDTO;
import com.szmsd.putinstorage.api.dto.PutawayListDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ReceivingRequest", description = "ReceivingRequest接收入库上架")
public class ReceivingRequest {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "单号 - 入库单号")
    private String orderNo;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "上架数量")
    private Integer qty;

    @ApiModelProperty(value = "仓库编号", hidden = true)
    private String warehouseCode;

}
