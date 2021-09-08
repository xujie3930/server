package com.szmsd.delivery.service.wrapper;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 14:52
 */
public interface IDelOutboundAsyncService {

    /**
     * #D2 接收出库包裹使用包材
     *
     * @param id id
     * @return int
     */
    int shipmentPacking(Long id);

    /**
     * #D2 接收出库包裹使用包材
     *
     * @param id               id
     * @param shipmentShipping 失败了是否推送发货指令，默认false不推送
     * @return int
     */
    int shipmentPacking(Long id, boolean shipmentShipping);

    /**
     * 出库单完成
     *
     * @param orderNo orderNo
     */
    void completed(String orderNo);

    /**
     * 出库单取消
     *
     * @param orderNo orderNo
     */
    void cancelled(String orderNo);
}
