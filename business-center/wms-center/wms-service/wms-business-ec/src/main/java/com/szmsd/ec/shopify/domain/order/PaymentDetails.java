package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName PaymentDetails.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class PaymentDetails {
    private String credit_card_bin;
    private String avs_result_code;
    private String cvv_result_code;
    private String credit_card_number;
    private String credit_card_company;
}