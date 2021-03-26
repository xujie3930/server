package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 13:35
 */
@Data
@Accessors(chain = true)
public class Money implements Serializable {

    private Double amount;
    private String currencyCode;
}
