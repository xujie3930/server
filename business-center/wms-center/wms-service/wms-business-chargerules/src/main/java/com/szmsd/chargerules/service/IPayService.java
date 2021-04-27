package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;

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
    BigDecimal calculate(BigDecimal firstPrice, BigDecimal nextPrice, Long qty);

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
     * @param custPayDTO custPayDTO
     * @param chargeLog chargeLog
     * @return result
     */
    R pay(CustPayDTO custPayDTO, ChargeLog chargeLog);

    /**
     * 费用冻结
     * @param dto dto
     * @param chargeLog chargeLog
     * @return result
     */
    R freezeBalance(CusFreezeBalanceDTO dto, ChargeLog chargeLog);

    /**
     * 费用解冻
     * @param dto dto
     * @param chargeLog chargeLog
     * @return result
     */
    R thawBalance(CusFreezeBalanceDTO dto, ChargeLog chargeLog);

}
