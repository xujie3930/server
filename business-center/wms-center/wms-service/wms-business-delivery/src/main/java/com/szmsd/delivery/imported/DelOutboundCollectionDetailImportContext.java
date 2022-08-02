package com.szmsd.delivery.imported;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.dto.DelOutboundCollectionDetailImportDto2;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 20:07
 */
public class DelOutboundCollectionDetailImportContext extends ImportContext<DelOutboundCollectionDetailImportDto2> {

    protected CacheContext<String, String> productAttributeCache;
    protected CacheContext<String, String> electrifiedModeCache;
    protected CacheContext<String, String> batteryPackagingCache;

    public DelOutboundCollectionDetailImportContext(List<DelOutboundCollectionDetailImportDto2> dataList,
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

}
