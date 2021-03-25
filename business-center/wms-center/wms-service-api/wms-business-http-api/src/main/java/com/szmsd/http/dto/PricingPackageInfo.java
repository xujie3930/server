package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 11:48
 */
@Data
@Accessors(chain = true)
public class PricingPackageInfo implements Serializable {

    // 包裹处理号
    private String refNo;

    // 包裹项数据
    private List<PackageInfo> packageInfos;

    private Packing packing;

    private Weight weight;

    private Weight volumeWeight;

    private Weight calcWeightWithoutAddition;

    private Weight calcWeight;
}
