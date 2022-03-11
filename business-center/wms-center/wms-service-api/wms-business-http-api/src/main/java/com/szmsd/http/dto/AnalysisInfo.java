package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "AnalysisInfo", description = "AnalysisInfo对象")
@Accessors(chain = true)
public class AnalysisInfo {

    private String warehouseCode;
    private String routeId;
    // 供应商服务
    private String service;
    // 包裹信息
    private List<AnalysisInfoPackageInfo> Packages;
    // 处理号
    private String RefNo;
    // 目标地址
    private AnalysisInfoAddress ToAddress;
    // 起始节点（仓库/处理点）
    private String StartNode;
    // Tag标签
    private List<String> Tags;
    // 是否单件计价（指的是将PackageInfo独立作为一个包裹计价）
    private Boolean IsSimplePricing;
}
