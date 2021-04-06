package com.szmsd.pack.api.feign.client;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description: 通过HTTP服务发起 http请求调用外部VMS接口
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
public interface IBasFeignClientService {

    /**
     * 查询sellerCode
     * @param
     * @return
     */
    String getLoginSellerCode();
}
