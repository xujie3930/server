package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName ClientDetails.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ClientDetails {
    private String accept_language;
    private int browser_height;
    private String browser_ip;
    private int browser_width;
    private String session_hash;
    private String user_agent;
}