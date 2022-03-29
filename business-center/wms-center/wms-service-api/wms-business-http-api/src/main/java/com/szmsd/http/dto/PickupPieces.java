package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author : wangshuai
 * @date : 2022-03-24 18:06
 * @description :地址与联系信息
 **/
@Data
@Accessors(chain = true)
@ApiModel(value = "PickupPieces")
@NoArgsConstructor
@AllArgsConstructor
public class PickupPieces {

    /**
     * 总重量
     */
    private Integer totalWeight;

    /**
     * 重量单位 "KGS,LBS"   (总重量除以1000即为KGS，两位小数)
     */
    private String unitOfMeasurement;

    private List<PickupPieceItemPickupPieceItem> pickupPieceItems;

}
