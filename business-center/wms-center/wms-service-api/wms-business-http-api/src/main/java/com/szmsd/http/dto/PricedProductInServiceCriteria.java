package com.szmsd.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 18:56
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PricedProductInServiceCriteria implements Serializable {

    // 客户代码
    private String clientCode;

    // 国家名称
    private String countryName;

    private Address fromAddress;

    // 是否带电
    // private Boolean isElectriferous;

    // sku的属性
    private List<String> shipmentTypes;
}
