package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCk1ShopifyLog;

/**
 * <p>
 * ck1对应shopify请求日志 服务类
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
public interface IBasCk1ShopifyLogService extends IService<BasCk1ShopifyLog> {

    void saveLog(BasCk1ShopifyLog log);
}