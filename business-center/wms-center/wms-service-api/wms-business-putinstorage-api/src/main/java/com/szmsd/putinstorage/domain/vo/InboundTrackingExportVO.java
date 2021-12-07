package com.szmsd.putinstorage.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


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
@ApiModel(description = "InboundTrackingExportVO")
public class InboundTrackingExportVO {

    @ExcelProperty(index = 0, value = "入库单号")
    private String orderNo;

    @ExcelProperty(index = 1, value = "快递单号/揽收单号")
    private String trackingNumber;
    /**
     * 0=未到货，1=已到货
     */
    @ExcelProperty(index = 2, value = "收货状态")
    private String receiptStatus = "已到货";

    @ExcelProperty(index = 3, value = "操作时间")
    private String operateOn;
}
