package com.szmsd.delivery.imported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundBatchImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 */
public class DelOutboundBatchImportContext extends ImportContext<DelOutboundBatchImportDto> {

    protected CacheContext<String, String> countryCache;
    protected CacheContext<String, String> countryCodeCache;

    protected CacheContext<String, String> shipmentChannelCache;
    protected CacheContext<String, Boolean> confirmCache;

    public DelOutboundBatchImportContext(List<DelOutboundBatchImportDto> dataList,
                                         List<BasRegionSelectListVO> countryList,
                                         List<BasSubWrapperVO> shipmentChannelList) {
        super(dataList);
         this.countryCache = new MapCacheContext<>();
        this.countryCodeCache = new MapCacheContext<>();
        this.shipmentChannelCache = new MapCacheContext<>();
        this.confirmCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(countryList)) {
            for (BasRegionSelectListVO country : countryList) {
                this.countryCache.put(country.getName(), country.getAddressCode());
                this.countryCache.put(country.getEnName(), country.getAddressCode());
                this.countryCodeCache.put(country.getAddressCode(), country.getName());

            }
        }
        if (CollectionUtils.isNotEmpty(shipmentChannelList)) {
            for (BasSubWrapperVO vo : shipmentChannelList) {
                this.shipmentChannelCache.put(vo.getSubName(), vo.getSubCode());
            }
        }
        this.confirmCache.put("是", true);
        this.confirmCache.put("否", false);
    }


}
