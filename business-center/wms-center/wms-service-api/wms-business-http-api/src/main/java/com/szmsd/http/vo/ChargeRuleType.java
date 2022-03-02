package com.szmsd.http.vo;

public enum ChargeRuleType {

    /**
     * 首重+续重
     */
    LinearFist,

    /**
     * 重量段
     */
    ByStage,

    /**
     * 基础费用+续重 (重量按单位重量)
     */
    LinearBase,

    /**
     * 基础费用+续重 (重量按实际重量)
     */
    LinearBasePer,

    /**
     * 首重+续重 (重量按实际重量)
     */
    LinearFistPer
}
