package com.szmsd.delivery.service.wrapper;

/**
 * 提审业务
 *
 * @author zhangyuyuan
 * @date 2021-03-23 16:33
 */
public interface IDelOutboundBringVerifyService {

    /**
     * 提审
     *
     * @param id id
     * @return int
     */
    int bringVerify(Long id);
}
