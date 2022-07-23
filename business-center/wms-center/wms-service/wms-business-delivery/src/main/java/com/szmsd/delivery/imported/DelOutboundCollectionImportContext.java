package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.delivery.dto.DelOutboundCollectionImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundCollectionImportContext extends ImportContext<DelOutboundCollectionImportDto> {

    protected CacheContext<String, String> countryCache;

    protected CacheContext<String, String> countryCodeCache;


    public DelOutboundCollectionImportContext(List<DelOutboundCollectionImportDto> dataList,
                                              List<BasRegionSelectListVO> countryList) {
        super(dataList);
         this.countryCache = new MapCacheContext<>();
        this.countryCodeCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
                this.countryCodeCache.put(country.getAddressCode(), country.getName());

            }
        }
    }
}
