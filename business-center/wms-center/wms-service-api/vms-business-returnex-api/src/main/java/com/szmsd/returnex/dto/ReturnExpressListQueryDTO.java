package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import java.time.LocalDate;
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
public class ReturnExpressListQueryDTO {

    @Min(value = 0, message = "数据异常")
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建日期 开始")
    private LocalDate createTimeStart;

    @ApiModelProperty(value = "创建日期 结束")
    private LocalDate createTimeEnd;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "预报单号")
    private String forecastNumber;

    public void setForecastNumber(String forecastNumber) {
        this.forecastNumber = forecastNumber;
        Optional.ofNullable(forecastNumber)
                .filter(x -> x.contains(","))
                .ifPresent(res -> forecastNumberList = Arrays.asList(res.split(",")));
    }

    @ApiModelProperty(value = "预报单号", hidden = true)
    private List<String> forecastNumberList;

    @ApiModelProperty(value = "VMS处理单号")
    private String vmsProcessNumber;

    public void setVmsProcessNumber(String vmsProcessNumber) {
        this.vmsProcessNumber = vmsProcessNumber;
        Optional.ofNullable(vmsProcessNumber)
                .filter(x -> x.contains(","))
                .ifPresent(res -> vmsProcessNumberList = Arrays.asList(res.split(",")));
    }

    @ApiModelProperty(value = "VMS处理单号", hidden = true)
    private List<String> vmsProcessNumberList;

    @ApiModelProperty(value = "类型[ 退件预报，VMS通知退件]")
    private String returnType;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]")
    private String returnSubType;

    @ApiModelProperty(value = "退件目标仓库编码")
    private String returnDestinationWarehouse;

    @ApiModelProperty(value = "申请处理方式编码")
    private String applyProcessMethod;

    @ApiModelProperty(value = "处理状态编码")
    private String dealStatus;

    @ApiModelProperty(value = "无名件列表查询", hidden = true)
    private Boolean noUserQuery = false;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
