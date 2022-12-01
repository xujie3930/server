package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * <p>
 * 出库单
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "出库单DelOutboundSpecialDto", description = "DelOutboundSpecialDto")
public class DelOutboundSpecialDto {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 特货还是普货
     */
    private String specialGoods;
}
