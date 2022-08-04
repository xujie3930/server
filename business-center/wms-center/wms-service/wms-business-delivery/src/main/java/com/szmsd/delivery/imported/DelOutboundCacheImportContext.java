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
    protected CacheContext<String, String> countryCodeCache;
    protected CacheContext<String, String> countryEnCache;
    protected CacheContext<String, String> deliveryMethodCache;

    public DelOutboundCacheImportContext(List<DelOutboundImportDto> dataList,
                                         List<BasSubWrapperVO> orderTypeList,
                                         List<BasRegionSelectListVO> countryList,
                                         List<BasSubWrapperVO> deliveryMethodList) {
        super(dataList);
        this.orderTypeCache = new MapCacheContext<>();
         this.countryCache = new MapCacheContext<>();
         this.countryEnCache = new MapCacheContext<>();        this.countryCodeCache = new MapCacheContext<>();
        this.deliveryMethodCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(orderTypeList)) {
            for (BasSubWrapperVO vo : orderTypeList) {
                this.orderTypeCache.put(vo.getSubName(), vo.getSubValue());
            }
        }
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
                this.countryEnCache.put(country.getEnName(), country.getAddressCode());
                this.countryCodeCache.put(country.getAddressCode(), country.getName());

            }
        }
        if (CollectionUtils.isNotEmpty(deliveryMethodList)) {
            for (BasSubWrapperVO vo : deliveryMethodList) {
                this.deliveryMethodCache.put(vo.getSubName(), vo.getSubValue());
            }
        }
    }
}
