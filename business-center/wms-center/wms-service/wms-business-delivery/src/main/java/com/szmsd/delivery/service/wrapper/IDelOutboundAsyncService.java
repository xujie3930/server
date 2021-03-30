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
}
