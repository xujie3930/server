package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 19:03
 */
@Data
@ApiModel(value = "ShipmentPackingMaterialRequestDto", description = "ShipmentPackingMaterialRequestDto对象")
public class ShipmentPackingMaterialRequestDto implements Serializable {

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    /**
     * 判断是不是只更新包材
     *
     * @return boolean
     */
    public boolean isPackingMaterial() {
        // 如果长宽高重量，都是0，就认为，只更新包材类型
        // 如果是null，也默认为0
        this.length = this.setDefaultValue(this.length);
        this.width = this.setDefaultValue(this.width);
        this.height = this.setDefaultValue(this.height);
        this.weight = this.setDefaultValue(this.weight);
        return (0.0D == this.length
                || 0.0D == this.width
                || 0.0D == this.height
                || 0.0D == this.weight);
    }

    private double setDefaultValue(Double value) {
        if (null == value) {
            return 0.0D;
        }
        return value;
    }
}
