package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.delivery.dto.DelOutboundCollectionImportDto;
import com.szmsd.delivery.dto.DelOutboundPackageTransferImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundPackageTransferImportContext extends ImportContext<DelOutboundPackageTransferImportDto> {

    protected CacheContext<String, String> countryCache;

    public DelOutboundPackageTransferImportContext(List<DelOutboundPackageTransferImportDto> dataList,
                                                   List<BasRegionSelectListVO> countryList) {
        super(dataList);
        this.countryCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
            }
        }
    }
}
