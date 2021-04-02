package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName: ReturnArrivalReqDTO
 * @Description: 接受VMS库存信息
 * @Author: 11
 * @Date: 2021/3/27 10:48
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("接受VMS库存信息")
public class ReturnArrivalReqDTO implements BOConvert {

    /**
     * 仓库退件单号，唯一，WMS生成 1-50
     */
    @StringLength(minLength = 1, maxLength = 50, message = "仓库退件单号超过约定长度[1-50]")
    @NotBlank(message = "仓库退件单号不能为空")
    @ApiModelProperty(value = "仓库退件单号，唯一，WMS生成",example = "VMS12345")
    private String returnNo;

    @ApiModelProperty(value = "仓库",example = "SZ")
    private String warehouseCode;

    /**
     * 预报单号，可能空 1-50
     */
    @StringLength(minLength = 1, maxLength = 50, message = "预报单号超过约定长度[1-50]")
    @ApiModelProperty(value = "预报单号",example = "OMS123")
    private String expectedNo;

    /**
     * 退件原出库单号 1-50
     */
    @StringLength(minLength = 1, maxLength = 50, message = "退件原出库单号超过约定长度[1-50]")
    @NotBlank(message = "退件原出库单号不能为空")
    @ApiModelProperty(value = "退件原出库单号")
    private String fromOrderNo;

    /**
     * string
     * maxLength: 50
     * minLength: 1
     * 退件上可扫描的编码，一般是挂号条码
     */
    @StringLength(minLength = 1, maxLength = 50, message = "退件码超过约定长度[1-50]")
    @NotBlank(message = "退件码不能为空")
    @ApiModelProperty(value = "退件上可扫描的编码，一般是挂号条码")
    private String scanCode;

    /**
     * stringf
     * maxLength: 50
     * minLength: 1
     * 卖家代码
     */
    @StringLength(minLength = 1, maxLength = 50, message = "卖家代码超过约定长度[1-50]")
    @NotBlank(message = "卖家代码不能为空")
    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    /**
     * string
     * maxLength: 500
     * minLength: 0
     * nullable: true
     * 备注
     */
    @StringLength(maxLength = 500, message = "备注超过约定长度[1-500]")
    @ApiModelProperty(value = "备注")
    private String remark;

    //TODO 未返回收到的货物
    @ApiModelProperty(value = "商品sku列表数据")
    private List<ReturnExpressGoodAddDTO> goodList;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
