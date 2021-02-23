package com.szmsd.system.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.SysSite;
import com.szmsd.system.api.feign.RemoteSiteService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 网点服务降级
 */
@Component
public class RemoteSiteFallbackFactory implements FallbackFactory<RemoteSiteService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteSiteFallbackFactory.class);

    @Override
    public RemoteSiteService create(Throwable throwable) {
        log.error("网点服务调用失败:{}", throwable.getMessage());
        return new RemoteSiteService()
        {
            @Override
            public R<SysSite> getSiteInfo(String siteCode)
            {
                return null;
            }

            @Override
            public R<List<SysSite>> getAllSiteInfo() {
                return null;
            }

            @Override
            public R<List<SysSite>> getSubSite(String siteCode) {
                return null;
            }
        };
    }
}
