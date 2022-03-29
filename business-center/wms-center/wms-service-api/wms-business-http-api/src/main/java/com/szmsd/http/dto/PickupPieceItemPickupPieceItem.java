package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wangshuai
 * @date : 2022-03-24 20:35
 * @description :包裹内物信息
 **/
@Data
@Accessors(chain = true)
@ApiModel(value = "PickupPieceItemPickupPieceItem")
@NoArgsConstructor
@AllArgsConstructor
public class PickupPieceItemPickupPieceItem {

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 备注
     */
    private String customerTag;

}
