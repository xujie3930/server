package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel("服务端新增退货单DTO")
public class ReturnExpressServiceAddDTO extends ReturnExpressAddDTO {

    /**
     * ---ADD----
     */
    @ApiModelProperty(value = "过期时间")
    @Excel(name = "过期时间")
    private Date expireTime;

    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    private String processTime;

    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String customerRemark;

    @ApiModelProperty(value = "新出库单号")
    @Excel(name = "新出库单号")
    private String fromOrderNoNew;

    @ApiModelProperty(value = "新物流跟踪号")
    @Excel(name = "新物流跟踪号")
    private String scanCodeNew;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
