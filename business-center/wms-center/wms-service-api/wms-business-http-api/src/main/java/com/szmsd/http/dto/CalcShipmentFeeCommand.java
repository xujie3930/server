package com.szmsd.http.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 11:37
 */
@Data
@Accessors(chain = true)
public class CalcShipmentFeeCommand implements Serializable {

    // 产品代码
    private String productCode;

    // 客户代码
    private String clientCode;

    // 发货类型---普货，电池，粉末等
    private String shipmentType;

    // 增值税号
    private String ioss;

    // 包裹信息
    private List<PackageInfo> packageInfos;

    private Address toAddress;

    private Address fromAddress;

    // 联系信息
    private ContactInfo toContactInfo;

    // 标签
    private List<String> tags;

    // 等级
    private String grade;

    // 报价表
    private String sheetCode;

    // 参考号
    private String refNo;

    // 验证地址
    private Boolean addressValifition;

    // 计价时间，用于选择对应生效的折扣
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date calcTimeForDiscount;

    // 需要忽略计算的费用类型
    private List<String> ignoreChargeTypes;

    private UserIdentity userIdentity;
}
