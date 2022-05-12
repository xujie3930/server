package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;
import com.szmsd.bas.mapper.BasCk1ShopifyWebhooksLogMapper;
import com.szmsd.bas.service.IBasCk1ShopifyWebhooksLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ck1对应shopify的webhooks日志 服务实现类
 * </p>
 *
 * @author asd
 * @since 2022-05-12
 */
@Service
public class BasCk1ShopifyWebhooksLogServiceImpl extends ServiceImpl<BasCk1ShopifyWebhooksLogMapper, BasCk1ShopifyWebhooksLog> implements IBasCk1ShopifyWebhooksLogService {

    @Async
    @Override
    public void saveLog(BasCk1ShopifyWebhooksLog log) {
        super.save(log);
    }
}

