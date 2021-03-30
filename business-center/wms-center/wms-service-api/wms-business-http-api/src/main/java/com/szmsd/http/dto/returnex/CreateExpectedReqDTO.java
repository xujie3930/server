package com.szmsd.http.dto.returnex;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @ClassName: CreateExpectedReqDTO
 * @Description: 创建退件预报 DTO
 * @Author: 11
 * @Date: 2021/3/26 14:22
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "创建退件预报")
public class CreateExpectedReqDTO implements Serializable {

    /**
     * 仓库代码
     * length:1-10
     */
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    /**
     * 跟OMS交互的退货预报单号（这个不是退货单号，退货单号由WMS生成） 1-50
     */
    @NotEmpty(message = "预报单号不能为空")
    @ApiModelProperty(value = "跟OMS交互的退货预报单号（这个不是退货单号，退货单号由WMS生成）")
    private String expectedNo;

    /**
     * 退件原始单号 1-50
     */
    @ApiModelProperty(value = "退件原始单号")
    private String refOrderNo;

    /**
     * 退件可扫描编码 1-50
     */
    @ApiModelProperty(value = "退件可扫描编码")
    private String scanCode;

    /**
     * 卖家代码 1-50
     */
    @NotEmpty(message = "卖家代码不能为空")
    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    /**
     * 处理方式 1-50
     */
    @NotEmpty(message = "请输入处理方式")
    @ApiModelProperty(value = "处理方式")
    private String processType;

    /**
     * 处理方式 1-500
     */
    @ApiModelProperty(value = "处理备注")
    private String processRemark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
