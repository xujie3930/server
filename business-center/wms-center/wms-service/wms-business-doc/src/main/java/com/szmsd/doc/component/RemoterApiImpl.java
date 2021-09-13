package com.szmsd.doc.component;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName: RemoterApiImpl
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:23
 */
@Slf4j
@Component
public class RemoterApiImpl implements IRemoterApi {

    @Autowired
    private BaseProductClientService baseProductClientService;
    @Resource
    private BasWarehouseClientService basWarehouseClientService;

    @Override
    public boolean verifyWarehouse(String warehouse) {
        List<BasWarehouse> basWarehouses = basWarehouseClientService.queryByWarehouseCodes(Collections.singletonList(warehouse));
        if (CollectionUtils.isNotEmpty(basWarehouses)) return true;
        return false;
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, String sku) {
        return this.checkSkuBelong(sellerCode, warehouse, Collections.singletonList(sku));
    }

    @Override
    public boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku) {
        BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
        baseProductConditionQueryDto.setSkus(sku);
        baseProductConditionQueryDto.setSellerCode(sellerCode);
//        baseProductConditionQueryDto.setWarehouseCode(warehouse);
        List<BaseProduct> baseProducts = baseProductClientService.queryProductList(baseProductConditionQueryDto);
        if (CollectionUtils.isNotEmpty(baseProducts)) return true;
        return false;
    }
}
