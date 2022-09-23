package com.szmsd.delivery.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;



/**
 * <p>
 * 出库单临时第三方任务表
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */
@Data
@ApiModel(value = "出库单临时第三方任务表", description = "DelOutboundThirdPartyzhVO")
public class DelOutboundThirdPartyzhVO  {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "出库单号")
    @Excel(name = "订单号",width = 30)
    private String orderNo;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态",width = 10)
    private String state;



    @ApiModelProperty(value = "备注")
    @Excel(name = "处理状态",width = 50)
    private String remark;


    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Excel(name = "创建时间",width = 30)
    private String createTimes;



}
