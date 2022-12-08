package com.szmsd.delivery.dto;

import com.szmsd.http.dto.ChargeWrapper;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ChargePricingResultDto implements Serializable {

    /**
     * 成功的数据
     */
    private List<ChargePricingOrderMsgDto> successOrders;

    /**
     * 失败的数据
     */
    private List<ChargePricingOrderMsgDto> errorOrders;

    /**
     * prc 费用 key 单号
     */
    private Map<String,ChargeWrapper> chargeWrapperMap;
}
