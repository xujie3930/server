package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.finance.enums.BillEnum;

import java.math.BigDecimal;
import java.util.List;

public interface IPayService {

    /**
     * 计算费用
     * @param firstPrice firstPrice
     * @param nextPrice nextPrice
     * @param qty qty
     * @return 费用
     */
    BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Integer qty);

    /**
     * 多SKU计算费用
     * @param firstPrice firstPrice
     * @param nextPrice nextPrice
     * @param delOutboundDetailList delOutboundDetailList
     * @return 费用
     */
    BigDecimal manySkuCalculate(BigDecimal firstPrice, BigDecimal nextPrice, List<DelOutboundDetailDto> delOutboundDetailList);

    /**
     * 调用扣费接口扣费
     * @param customCode 客户编号
     * @param amount 金额
     * @return result
     */
    R pay(String customCode, BigDecimal amount, BillEnum.PayMethod payMethod, ChargeLog chargeLog);

}
