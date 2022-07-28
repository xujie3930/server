package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@ApiModel(value = "BulkOrderRequestDto", description = "BulkOrderRequestDto对象")
public class BulkOrderTaskConfigDto implements Serializable {

    /**
     * 接收发货指令类型
     * 不接收发货指令：NotReceive
     * <p>
     * 出库测量后接收发货指令：AfterMeasured
     */
    private String receiveShippingType;

    /**
     * 是否回传包材
     */
    private Boolean isPublishPackageMaterial;

    /**
     * 是否回传重量
     */
    private Boolean isPublishPackageWeight;

    /**
     * 打印出库标签类型
     * 不打印:NotPrint
     * <p>
     * 波次后打印:PrintAfterWave
     * <p>
     * 波次打印:PrintOnWave
     */
    private String printShippingLabelType;


}
