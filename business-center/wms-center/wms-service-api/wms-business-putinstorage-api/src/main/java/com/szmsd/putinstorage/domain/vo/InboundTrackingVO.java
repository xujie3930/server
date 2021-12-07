package com.szmsd.putinstorage.domain.vo;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 入库物流到货记录
 * </p>
 *
 * @author 11
 * @since 2021-09-06
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "InboundTracking对象")
public class InboundTrackingVO {

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String orderNo;

    @ApiModelProperty(value = "快递单号")
    @Excel(name = "快递单号")
    private String trackingNumber;

    @ApiModelProperty(value = "收货时间")
    @Excel(name = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "到货情况[0=未到货，1=已到货]")
    @Excel(name = "到货情况")
    private String arrivalStatus= "0";


}
