package com.szmsd.chargerules.runnable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IBaseInfoService;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.SkuVolumeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RunnableExecute {

    @Resource
    private InventoryFeignService inventoryFeignService;

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private IWarehouseOperationService warehouseOperationService;

    @Resource
    private IBaseInfoService baseInfoService;

    /**
     * 定时任务：普通操作计价扣费；每天12点，23点执行一次
     */
    @Scheduled(cron = "0 0 13,23 * * *")
    public void executeOperation() {
        System.out.println(123);
    }

    /**
     * 定时任务：储存仓租计价扣费；每周日晚上8点执行
     */
//    @Scheduled(cron = "0 0 20 * * 7")
    @Scheduled(cron = "0/60 * * * * *")
    public void executeWarehouse() {
        // 获取当前全量库存
        R<List<InventorySkuVolumeVO>> result = inventoryFeignService.querySkuVolume(new InventorySkuVolumeQueryDTO());
        List<InventorySkuVolumeVO> data = result.getData();
        if(result.getCode() == 200 && !data.isEmpty()) {
            // 获取仓库的计费规则列表
            QueryWrapper<WarehouseOperation> query = new QueryWrapper<>();
            List<WarehouseOperation> warehouseOperations = warehouseOperationMapper.selectList(query);
            data.forEach(warehouseOperation-> {
                List<SkuVolumeVO> skuVolumes = warehouseOperation.getSkuVolumes();
                skuVolumes.forEach(skuVolume -> {
                    String datePoor = DateUtils.getDatePoor(new Date(), DateUtils.parseDate(skuVolume.getOperateOn()));
                    int days = Integer.parseInt(datePoor.substring(0, datePoor.indexOf("天")));
                    // 根据存放天数、存放体积计算应收取的费用
                    BigDecimal charge = warehouseOperationService.charge(days, skuVolume.getVolume(), warehouseOperation.getWarehouseCode(), warehouseOperations);
                    baseInfoService.pay("",charge);
                });
            });
        }
    }

}
