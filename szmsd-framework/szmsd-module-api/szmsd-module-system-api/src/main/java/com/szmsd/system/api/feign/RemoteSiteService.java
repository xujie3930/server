package com.szmsd.system.api.feign;

import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.SysSite;
import com.szmsd.system.api.factory.RemoteLogFallbackFactory;
import com.szmsd.system.api.factory.RemoteSiteFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 网点对外服务提供
 */
@FeignClient(contextId = "remoteSiteService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSiteFallbackFactory.class)
public interface RemoteSiteService {

    /**
     * 通过网点编号查询网点信息
     *
     * @param siteCode 网点编号
     * @return 结果
     */
    @GetMapping(value = "/site/info/{siteCode}")
     R<SysSite> getSiteInfo(@PathVariable(value = "siteCode")String siteCode);

    @GetMapping(value = "/site/listAll")
    R<List<SysSite>> getAllSiteInfo();

    @GetMapping(value = "/site/getSubSite/{siteCode}")
    R<List<SysSite>> getSubSite(@PathVariable(value = "siteCode")String siteCode);
}
