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
    private final CacheContext<String, String> warehouseCache;
    private final CacheContext<String, String> exceptionStateCache;

    public DelOutboundExportContext(BasWarehouseClientService basWarehouseClientService, List<BasSubWrapperVO> exceptionStateList) {
        this.warehouseLock = new ReentrantLock();
        this.basWarehouseClientService = basWarehouseClientService;
        this.warehouseCache = new CacheContext.MapCacheContext<>();
        this.exceptionStateCache = new CacheContext.MapCacheContext<>();
        if (CollectionUtils.isNotEmpty(exceptionStateList)) {
            for (BasSubWrapperVO vo : exceptionStateList) {
                this.exceptionStateCache.put(vo.getSubValue(), vo.getSubName());
            }
        }
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
    public String getExceptionStateName(String exceptionState) {
        return this.exceptionStateCache.get(exceptionState);
    }
}
