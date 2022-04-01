package com.szmsd.delivery.exported;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.imported.CacheContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:14
 */
public class DelOutboundExportContext implements ExportContext {

    private final Lock warehouseLock;
    private final BasWarehouseClientService basWarehouseClientService;
    private final CacheContext<String, String> stateCache;
    private final CacheContext<String, String> warehouseCache;
    private final CacheContext<String, String> orderTypeCache;
    private final CacheContext<String, String> exceptionStateCache;
    private final BasRegionFeignService basRegionFeignService;
    private final CacheContext<String, String> countryCache;
    private final Lock countryLock;
    private String len;

    public DelOutboundExportContext(BasWarehouseClientService basWarehouseClientService, BasRegionFeignService basRegionFeignService, String... len) {
        this.basRegionFeignService = basRegionFeignService;
        this.warehouseLock = new ReentrantLock();
        this.basWarehouseClientService = basWarehouseClientService;
        this.stateCache = new CacheContext.MapCacheContext<>();
        this.warehouseCache = new CacheContext.MapCacheContext<>();
        this.orderTypeCache = new CacheContext.MapCacheContext<>();
        this.exceptionStateCache = new CacheContext.MapCacheContext<>();
        this.countryCache = new CacheContext.MapCacheContext<>();
        this.countryLock = new ReentrantLock();
        if(len != null && len.length > 0){
            this.len = len[0];
        }
    }

    private void setBySubValue(CacheContext<String, String> cacheContext, List<BasSubWrapperVO> list) {
        if (CollectionUtils.isNotEmpty(list)) {

            if("en".equals(len)) {
                for (BasSubWrapperVO vo : list) {
                    cacheContext.put(vo.getSubValue(), vo.getSubNameEn());
                }
            }else{
                for (BasSubWrapperVO vo : list) {
                    cacheContext.put(vo.getSubValue(), vo.getSubName());
                }
            }

        }
    }

    public void setStateCacheAdapter(List<BasSubWrapperVO> list) {
        this.setBySubValue(this.stateCache, list);
    }

    public void setOrderTypeCacheAdapter(List<BasSubWrapperVO> list) {
        this.setBySubValue(this.orderTypeCache, list);
    }

    public void setExceptionStateCacheAdapter(List<BasSubWrapperVO> list) {
        this.setBySubValue(this.exceptionStateCache, list);
    }

    @Override
    public String getStateName(String state) {
        return this.stateCache.get(state);
    }

    @Override
    public String getWarehouseName(String warehouseCode) {
        return this.getLockValue(warehouseCode, this.warehouseLock, this.warehouseCache, (key) -> {
            BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(key);
            if (null != basWarehouse) {
                if("en".equals(len)){
                    return basWarehouse.getWarehouseNameEn();
                }
                return basWarehouse.getWarehouseNameCn();
            }
            return null;
        });
    }

    private <K, V> V getLockValue(K key, Lock lock, CacheContext<K, V> cacheContext, Function<K, V> function) {
        // 线程访问开放
        if (cacheContext.containsKey(key)) {
            return cacheContext.get(key);
        } else {
            V value;
            try {
                // 锁
                lock.lock();
                // 双层判断
                if (cacheContext.containsKey(key)) {
                    value = cacheContext.get(key);
                } else {
                    value = function.apply(key);
                    // 当结果集是空的时候，这里赋值也是空
                    cacheContext.put(key, value);
                }
            } finally {
                // 释放锁
                lock.unlock();
            }
            return value;
        }
    }

    @Override
    public String getOrderTypeName(String orderType) {
        return this.orderTypeCache.get(orderType);
    }

    @Override
    public String getExceptionStateName(String exceptionState) {
        return this.exceptionStateCache.get(exceptionState);
    }

    @Override
    public String getCountry(String countryCode) {
        return this.getLockValue(countryCode, this.countryLock, this.countryCache, (key) -> {
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(key);
            if (null != countryR) {
                BasRegionSelectListVO data = countryR.getData();
                if (null != data) {
                    if("en".equals(len)) {
                        return data.getEnName();
                    }
                    return data.getName();
                }
            }
            return null;
        });
    }

    @Override
    public String len() {
        return this.len;
    }
}
