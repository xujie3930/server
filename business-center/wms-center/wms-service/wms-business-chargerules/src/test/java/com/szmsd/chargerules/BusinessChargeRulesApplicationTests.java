package com.szmsd.chargerules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class BusinessChargeRulesApplicationTests {

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private IWarehouseOperationService warehouseOperationService;

    @Test
    public void test() {
        int days = 66;
        BigDecimal cbm = new BigDecimal("12");
        String warehouseCode = "1";
        LambdaQueryWrapper<WarehouseOperation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(WarehouseOperation::getWarehouseCode,warehouseCode);
        List<WarehouseOperation> warehouseOperations = warehouseOperationMapper.selectList(queryWrapper);
        BigDecimal charge = warehouseOperationService.charge(days, cbm, warehouseCode, warehouseOperations);
        System.out.println(charge);
    }
}
