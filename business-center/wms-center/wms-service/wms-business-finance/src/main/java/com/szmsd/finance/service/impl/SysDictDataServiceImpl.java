package com.szmsd.finance.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.service.ISysDictDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liulei
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    @Resource
    BasSubFeignService basSubFeignService;

    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;
    private final TimedCache<String, String> currencyCodeCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 30);
    private final TimedCache<String, String> warehouseCodeCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis() * 30);

    @Override
    public String getCurrencyNameByCode(String currencyCode) {
        String subName = currencyCodeCache.get(currencyCode);
        if (StringUtils.isNotBlank(subName)) {
            return subName;
        }
        List<BasSub> basSubs = basSubFeignService.listApi("008", currencyCode);
        if (CollectionUtils.isEmpty(basSubs)) {
            return "";
        }
        subName = basSubs.get(0).getSubName();
        currencyCodeCache.put(currencyCode, subName);
        return subName;
    }

    @Override
    public String getWarehouseNameByCode(String warehouseCode) {
        String warehouseName = warehouseCodeCache.get(warehouseCode);
        if (StringUtils.isNotBlank(warehouseName)) {
            return warehouseName;
        }
        R<BasWarehouse> result = basWarehouseFeignService.queryByWarehouseCode(warehouseCode);
        if (result.getCode() == 200 && result.getData() != null) {
            warehouseName = result.getData().getWarehouseNameCn();
            warehouseCodeCache.put(warehouseCode, warehouseName);
            return warehouseName;
        }
        return null;
    }

}
