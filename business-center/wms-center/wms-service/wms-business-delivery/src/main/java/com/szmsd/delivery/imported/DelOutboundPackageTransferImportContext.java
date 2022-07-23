package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundPackageTransferImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundPackageTransferImportContext extends ImportContext<DelOutboundPackageTransferImportDto> {

    protected CacheContext<String, String> countryCache;
    protected CacheContext<String, String> countryCodeCache;

    protected CacheContext<String, String> packageConfirmCache;

    public DelOutboundPackageTransferImportContext(List<DelOutboundPackageTransferImportDto> dataList,
                                                   List<BasRegionSelectListVO> countryList,
                                                   List<BasSubWrapperVO> packageConfirmList) {
        super(dataList);
        this.countryCache = new MapCacheContext<>();
        this.packageConfirmCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
                this.countryCodeCache.put(country.getAddressCode(), country.getName());

            }
        }
        if (CollectionUtils.isNotEmpty(packageConfirmList)) {
            for (BasSubWrapperVO vo : packageConfirmList) {
                this.packageConfirmCache.put(vo.getSubName(), vo.getSubCode());
            }
        }
    }
}
