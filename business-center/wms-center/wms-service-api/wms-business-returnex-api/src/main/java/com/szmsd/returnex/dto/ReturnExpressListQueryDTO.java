package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.page.PageDomain;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
    private LocalDate createTimeStart;

    @ApiModelProperty(value = "创建日期 结束", example = "2021-03-01")
    private LocalDate createTimeEnd;

    @ApiModelProperty(value = "客户代码", example = "UID123456")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "预报单号", example = "YBD123456;YBD333")
    private String forecastNumber;

    public void setForecastNumber(String forecastNumber) {
        this.forecastNumber = forecastNumber;
        Optional.ofNullable(forecastNumber)
                .filter(StringUtil::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ","))
                .ifPresent(res -> forecastNumberList = Arrays.asList(res.split(",")));
    }

    @ApiModelProperty(value = "预报单号", hidden = true)
    private List<String> forecastNumberList;

    @ApiModelProperty(value = "WMS处理单号", example = "123,123")
    private String returnNo;

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
        Optional.ofNullable(returnNo)
                .filter(StringUtil::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ","))
                .ifPresent(res -> returnNoList = Arrays.asList(res.split(",")));
    }

    @ApiModelProperty(value = "VMS处理单号", hidden = true)
    private List<String> returnNoList;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]",example = "OWN_INVENTORY_RETURN")
    private String returnType;

    @ApiModelProperty(value = "类型[退件预报 WMS通知退件]",example = "RETURN_FORECAST")
    private String returnSource;

    @ApiModelProperty(value = "退件目标仓库编码",example = "SZ")
    private String warehouseCode;

    @ApiModelProperty(value = "处理状态编码[销毁 整包上架 拆包检查 按明细上架]",example = "Destroy")
    private String processType;

    @ApiModelProperty(value = "无名件列表查询", hidden = true)
    private Boolean noUserQuery = false;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
