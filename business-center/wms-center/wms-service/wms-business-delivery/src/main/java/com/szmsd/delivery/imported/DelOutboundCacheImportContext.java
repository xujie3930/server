package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundCacheImportContext extends ImportContext<DelOutboundImportDto> {

    protected CacheContext<String, String> orderTypeCache;
    protected CacheContext<String, String> countryCache;

    public DelOutboundCacheImportContext(List<DelOutboundImportDto> dataList, List<BasSubWrapperVO> subList, List<BasRegionSelectListVO> countryList) {
        super(dataList);
        this.orderTypeCache = new MapCacheContext<>();
        this.countryCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(subList)) {
            for (BasSubWrapperVO sub : subList) {
                this.orderTypeCache.put(sub.getSubName(), sub.getSubValue());
            }
        }
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
            }
        }
    }
}
