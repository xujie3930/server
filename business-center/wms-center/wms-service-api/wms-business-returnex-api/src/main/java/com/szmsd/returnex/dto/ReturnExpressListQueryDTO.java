package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: ReturnExpressListQueryDTO
 * @Description: 退货单列表查询
 * @Author: 11
 * @Date: 2021/3/26 13:44
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class ReturnExpressListQueryDTO extends PageDomain {

    @Min(value = 0, message = "数据异常")
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建日期 开始", example = "2021-03-01")
    private String createTimeStart;

    @ApiModelProperty(value = "创建日期 结束", example = "2021-03-01")
    private String createTimeEnd;

    @ApiModelProperty(value = "客户代码", example = "UID123456")
    @Excel(name = "客户代码")
    private String sellerCode;

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
        Optional.ofNullable(sellerCode).filter(StringUtil::isNotBlank)
                .ifPresent(x -> this.sellerCodeList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "客户代码", example = "UID123456", hidden = true)
    @Excel(name = "客户代码")
    private List<String> sellerCodeList;

    @ApiModelProperty(value = "预报单号", example = "YBD123456;YBD333")
    private String forecastNumber;

    public void setForecastNumber(String forecastNumber) {
        this.forecastNumber = forecastNumber;
        Optional.ofNullable(forecastNumber).filter(StringUtil::isNotBlank)
                .ifPresent(x -> this.forecastNumberList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "预报单号", hidden = true)
    private List<String> forecastNumberList;

    @ApiModelProperty(value = "处理号", example = "123,123")
    private String returnNo;

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
        Optional.ofNullable(returnNo).filter(StringUtil::isNotBlank)
                .ifPresent(x -> this.returnNoList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "WMS处理单号", hidden = true)
    private List<String> returnNoList;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "OWN_INVENTORY_RETURN")
    private String returnType;

    @ApiModelProperty(value = "类型[退件预报 WMS通知退件]", example = "RETURN_FORECAST")
    private String returnSource;

    @ApiModelProperty(value = "退件目标仓库编码", example = "SZ")
    private String warehouseCode;

    @ApiModelProperty(value = "处理状态编码[销毁 整包上架 拆包检查 按明细上架]", example = "Destroy")
    private String processType;

    @ApiModelProperty(value = "无名件列表查询", hidden = true)
    private Boolean noUserQuery = false;

    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "跟踪号")
    private String scanCode;

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
        Optional.ofNullable(scanCode).filter(StringUtil::isNotBlank)
                .ifPresent(x -> this.scanCodeList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "跟踪号",hidden = true)
    @Excel(name = "跟踪号")
    private List<String> scanCodeList;

    @ApiModelProperty(value = "退件单号")
    @Excel(name = "退件单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private String dealStatus;

    public void setFromOrderNo(String fromOrderNo) {
        this.fromOrderNo = fromOrderNo;
        Optional.ofNullable(fromOrderNo).filter(StringUtil::isNotBlank)
                .ifPresent(x -> this.fromOrderNoList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "退件单号")
    @Excel(name = "退件单号")
    private List<String> fromOrderNoList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
