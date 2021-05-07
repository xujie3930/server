package com.szmsd.delivery.exported;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.delivery.imported.CacheContext;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public DelOutboundExportContext(BasWarehouseClientService basWarehouseClientService) {
        this.warehouseLock = new ReentrantLock();
        this.basWarehouseClientService = basWarehouseClientService;
        this.stateCache = new CacheContext.MapCacheContext<>();
        this.warehouseCache = new CacheContext.MapCacheContext<>();
        this.orderTypeCache = new CacheContext.MapCacheContext<>();
        this.exceptionStateCache = new CacheContext.MapCacheContext<>();
    }

    private void setBySubValue(CacheContext<String, String> cacheContext, List<BasSubWrapperVO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (BasSubWrapperVO vo : list) {
                cacheContext.put(vo.getSubValue(), vo.getSubName());
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
        // 线程访问开放
        if (this.warehouseCache.containsKey(warehouseCode)) {
            return this.warehouseCache.get(warehouseCode);
        } else {
            String warehouseName = null;
            try {
                // 锁
                this.warehouseLock.lock();
                // 双层判断
                if (this.warehouseCache.containsKey(warehouseCode)) {
                    warehouseName = this.warehouseCache.get(warehouseCode);
                } else {
                    BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
                    if (null != basWarehouse) {
                        warehouseName = basWarehouse.getWarehouseNameCn();
                    }
                    // 当结果集是空的时候，这里赋值也是空
                    this.warehouseCache.put(warehouseCode, warehouseName);
                }
            } finally {
                // 释放锁
                this.warehouseLock.unlock();
            }
            return warehouseName;
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
}
