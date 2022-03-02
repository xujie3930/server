package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisRouteResult {

    // 线路图Id
    private String RouteId;
    // 线路图名称
    private String RouteName;
    // 是否可能更改供应商
    private Boolean IsMayChangeSupplier;
    // 完整线路
    private List<LogisticsLink> Links;
    // 线路的过滤信息
    private String FiltterMessage;
}
