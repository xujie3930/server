package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;

/**
 * <p>
 * ck1对应shopify的webhooks日志 服务类
 * </p>
 *
 * @author asd
 * @since 2022-05-12
 */
public interface IBasCk1ShopifyWebhooksLogService extends IService<BasCk1ShopifyWebhooksLog> {

    void saveLog(BasCk1ShopifyWebhooksLog log);
}

