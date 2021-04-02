package com.szmsd.delivery.service.wrapper;

/**
 * 出库发货步骤
 *
 * @author zhangyuyuan
 * @date 2021-04-01 16:21
 */
public enum ShipmentEnum {

    /**
     * 开始
     */
    BEGIN,

    // #1 创建承运商物流订单
    SHIPMENT_ORDER,

    // #2 更新挂号
    SHIPMENT_TRACKING,

    // #3 获取标签
    LABEL,

    // #4 更新标签
    SHIPMENT_LABEL,

    // #5 PRC计费
    PRC_PRICING,

    // #6 取消冻结费用
    THAW_BALANCE,

    // #7 冻结费用
    FREEZE_BALANCE,

    // #8 更新发货指令，成功
    SHIPMENT_SHIPPING_SUCCESS,

    // #9 更新发货指令，失败
    SHIPMENT_SHIPPING_FAIL,

    /**
     * 结束
     */
    END,
    ;
}
