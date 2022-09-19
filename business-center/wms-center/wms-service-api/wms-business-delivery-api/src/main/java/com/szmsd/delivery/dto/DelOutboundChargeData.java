package com.szmsd.delivery.dto;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * <p>
 * 出库单
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单", description = "DelOutbound对象")
public class DelOutboundChargeData extends DelOutbound {

    private static final long serialVersionUID = 1L;

    /**
     * 币别
     */
    private List<DelOutboundCharge> delOutboundCharges;

}
