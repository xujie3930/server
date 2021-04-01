package com.szmsd.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 15:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AddShipmentRuleRequest implements Serializable {

    // 发货规则（一般填写物流服务）
    private String shipmentRule;

    /**
     * 获取标签类型
     * <p>
     * 无运单：None
     * 下单时获取 First
     * 仓库核重后获取 Finnal
     */
    private String getLabelType;
}
