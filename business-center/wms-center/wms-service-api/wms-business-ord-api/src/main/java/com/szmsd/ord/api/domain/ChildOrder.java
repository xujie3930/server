package com.szmsd.ord.api.domain;

import lombok.Data;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-12 11:11
 * @Description
 */
@Data
public class ChildOrder {
    /**
     * 运单号
     */
    private String waybillNo;
    /**
     * 子单号
     */
    private String subWaybillNo;
}
