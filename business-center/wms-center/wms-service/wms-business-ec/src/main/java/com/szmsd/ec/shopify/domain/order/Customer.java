package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName Customer.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class Customer {
    private long id;
    private String email;
    private boolean accepts_marketing;
    private String created_at;
    private String updated_at;
    private String first_name;
    private String last_name;
    private int orders_count;
    private String state;
    private String total_spent;
    private long last_order_id;
    private String note;
    private boolean verified_email;
    private String multipass_identifier;
    private boolean tax_exempt;
    private String phone;
    private String tags;
    private String last_order_name;
    private String currency;
    private String accepts_marketing_updated_at;
    private String marketing_opt_in_level;
    private String admin_graphql_api_id;
    private DefaultAddress default_address;
}