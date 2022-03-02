package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class PackageCostItem {
    // 供应商服务Code
    private String ServiceCode;
    // 供应商服务名称
    private String ServiceName;
    // 供应商Code
    private String SupplierCode;
    // 供应商名称
    private String SupplierName;
    // 发货地节点
    private String FromNode;
    // 目的地节点
    private String ToNode;
    // 总费用
    private Money AmountCost;
    // 具体费用明细
    private List<ChargeItem> CostDetails;

}
