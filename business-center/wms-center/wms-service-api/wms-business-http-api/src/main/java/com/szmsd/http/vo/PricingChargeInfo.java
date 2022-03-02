package com.szmsd.http.vo;

import com.szmsd.http.dto.AnalysisInfoPackageInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingChargeInfo {

    // 重量段（Min）
    private BigDecimal MinWeight;
    // 重量段（Max）
    private BigDecimal MaxWeight;
    // 计费方式
    private ChargeRuleType ChargeRuleType;
    // 分区名称
    private String ZoneName;
    // 供应商Code
    private String SupplierCode;
    // 服务Id
    private String ServiceId;
    // 服务名称
    private String ServiceName;
    // 报价表
    private String PricingSheet;
    // 包裹计费信息
    private AnalysisInfoPackageInfo PackageForCalc;
}
