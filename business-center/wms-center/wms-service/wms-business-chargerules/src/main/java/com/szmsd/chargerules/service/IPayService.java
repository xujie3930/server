package com.szmsd.chargerules.service;

import com.szmsd.common.core.domain.R;

import java.math.BigDecimal;

public interface IPayService {

    /**
     * 调用扣费接口扣费
     * @param customCode 客户编号
     * @param amount 金额
     * @return result
     */
    R pay(String customCode, BigDecimal amount);

}
