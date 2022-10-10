package com.szmsd.delivery.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 出库单
 * </p>
 *
 * @author jiangjun
 * @since 2022-10-10
 */
@Data
@ApiModel(value = "更新挂号失败记录", description = "DelOutboundTarckError对象")
public class DelOutboundTarckError {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "出库单号")
    @Excel(name="出库单号",width = 60,needMerge = true)
    private String orderNo;

    @ApiModelProperty(value = "挂号")
    @Excel(name="挂号",width = 60,needMerge = true)
    private String trackingNo;

    @ApiModelProperty(value = "失败原因")
    @Excel(name="失败原因",width = 60,needMerge = true)
    private String errorReason;


}