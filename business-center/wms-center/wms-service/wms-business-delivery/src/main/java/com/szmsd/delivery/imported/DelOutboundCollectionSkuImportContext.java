package com.szmsd.delivery.imported;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundCollectionDetailImportDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 14:02
 */
public class DelOutboundCollectionSkuImportContext extends ImportContext<DelOutboundCollectionDetailImportDto> {

    protected CacheContext<String, String> productAttributeCache;
    protected CacheContext<String, String> electrifiedModeCache;
    protected CacheContext<String, String> batteryPackagingCache;

    public DelOutboundCollectionSkuImportContext(List<DelOutboundCollectionDetailImportDto> dataList,
                                                 List<BasSubWrapperVO> productAttributeList,
                                                 List<BasSubWrapperVO> electrifiedModeList,
                                                 List<BasSubWrapperVO> batteryPackagingList) {
        super(dataList);
        this.productAttributeCache = new MapCacheContext<>();
        this.electrifiedModeCache = new MapCacheContext<>();
        this.batteryPackagingCache = new MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(productAttributeList)) {
            for (BasSubWrapperVO vo : productAttributeList) {
                this.productAttributeCache.put(vo.getSubName(), vo.getSubValue());
            }
        }
        if (CollectionUtils.isNotEmpty(electrifiedModeList)) {
            for (BasSubWrapperVO vo : electrifiedModeList) {
                this.electrifiedModeCache.put(vo.getSubName(), vo.getSubValue());
            }
        }
        if (CollectionUtils.isNotEmpty(batteryPackagingList)) {
            for (BasSubWrapperVO vo : batteryPackagingList) {
                this.batteryPackagingCache.put(vo.getSubName(), vo.getSubValue());
            }
        }
    }

    public CacheContext<String, String> getProductAttributeCache() {
        return productAttributeCache;
    }

    public CacheContext<String, String> getElectrifiedModeCache() {
        return electrifiedModeCache;
    }

    public CacheContext<String, String> getBatteryPackagingCache() {
        return batteryPackagingCache;
    }
}
