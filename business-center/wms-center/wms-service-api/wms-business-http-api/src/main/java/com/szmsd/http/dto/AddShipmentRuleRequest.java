package com.szmsd.http.dto;

import com.alibaba.fastjson.annotation.JSONField;
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

    // 仓库编码 - 非业务数据
    @JSONField(serialize = false)
    private String warehouseCode;


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

    // 无用字段，方便在接口日志里面查询
    private String orderNo;
}
