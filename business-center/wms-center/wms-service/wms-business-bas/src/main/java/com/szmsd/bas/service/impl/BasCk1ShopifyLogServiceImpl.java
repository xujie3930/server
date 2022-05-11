package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasCk1ShopifyLog;
import com.szmsd.bas.mapper.BasCk1ShopifyLogMapper;
import com.szmsd.bas.service.IBasCk1ShopifyLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ck1对应shopify请求日志 服务实现类
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
@Service
public class BasCk1ShopifyLogServiceImpl extends ServiceImpl<BasCk1ShopifyLogMapper, BasCk1ShopifyLog> implements IBasCk1ShopifyLogService {

    @Async
    @Override
    public void saveLog(BasCk1ShopifyLog log) {
        super.save(log);
    }
}
