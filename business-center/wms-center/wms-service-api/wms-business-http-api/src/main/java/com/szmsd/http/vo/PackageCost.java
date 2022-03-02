package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class PackageCost {

    // 包裹处理号
    private String ProcessNo;
    // 包裹成本初始计算的时间
    private String CreateTime;
    // 包裹成本最后一次计算的时间
    private String LastModifiedOn;
    // 费用详情
    private List<PackageCostItem> CostItems;
    // 是否成功获取
    private Boolean IsSucesss;
    // 获取失败的消息
    private String FaildMessage;

}
