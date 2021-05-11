package com.szmsd.finance.service.impl;

import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.domain.R;
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

    @Override
    public String getCurrencyNameByCode(String currencyCode) {
        List<BasSub> basSubs = basSubFeignService.listApi("008", currencyCode);
        if (CollectionUtils.isEmpty(basSubs)) {
            return "";
        }
        return basSubs.get(0).getSubName();
    }

    @Override
    public String getWarehouseNameByCode(String warehouseCode) {
        R<BasWarehouse> result = basWarehouseFeignService.queryByWarehouseCode(warehouseCode);
        if (result.getCode() == 200 && result.getData() != null) {
            return result.getData().getWarehouseNameCn();
        }
        return null;
    }

}
