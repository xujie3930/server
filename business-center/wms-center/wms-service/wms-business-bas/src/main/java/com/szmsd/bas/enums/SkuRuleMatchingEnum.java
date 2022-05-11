package com.szmsd.bas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum SkuRuleMatchingEnum {
    SHOPIFY("0", "Shopify"),
    ;
    private String code;
    private String name;


}
