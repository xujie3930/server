package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import com.szmsd.inventory.domain.vo.InventoryVO;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Resource
    private IInventoryRecordService iInventoryRecordService;

    @Resource
    private RemoteComponent remoteComponent;
    @Autowired
    private BaseProductClientService baseProductClientService;

    /**
     * 上架入库 - Inbound - /api/inbound/receiving #B1 接收入库上架 - 修改库存
     *
     * @param inboundInventoryDTO
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void inbound(InboundInventoryDTO inboundInventoryDTO) {
        log.info("上架入库：{}", inboundInventoryDTO);
        Lock lock = new ReentrantLock(true);
        try {
            // 获取锁
            lock.lock();
            // 获取库存 仓库代码 + SKU
            String sku = inboundInventoryDTO.getSku();
            String warehouseCode = inboundInventoryDTO.getWarehouseCode();
            Integer qty = inboundInventoryDTO.getQty();
            // before inventory
            Inventory beforeInventory = baseMapper.selectOne(new QueryWrapper<Inventory>().eq("warehouse_code", warehouseCode).eq("sku", sku));
            Inventory afterInventory = new Inventory();
            if (beforeInventory == null) {
                beforeInventory = new Inventory().setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(0).setAvailableInventory(0).setAvailableInventory(0).setTotalInbound(0);
                BaseProduct sku1 = remoteComponent.getSku(sku);
                afterInventory.setCusCode(sku1.getSellerCode());
            }

            // after inventory
            int afterTotalInventory = beforeInventory.getTotalInventory() + qty;
            int afterAvailableInventory = beforeInventory.getAvailableInventory() + qty;
            int afterTotalInbound = beforeInventory.getTotalInbound() + qty;
            afterInventory.setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
            this.saveOrUpdate(afterInventory);

            // 记录库存日志
            iInventoryRecordService.saveLogs(
                    LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), beforeInventory, afterInventory, inboundInventoryDTO.getOrderNo(), inboundInventoryDTO.getOperator(), inboundInventoryDTO.getOperateOn(), qty,
                    inboundInventoryDTO.getOperator(), inboundInventoryDTO.getOperateOn(), inboundInventoryDTO.getOrderNo(), inboundInventoryDTO.getSku(), inboundInventoryDTO.getWarehouseCode(), (inboundInventoryDTO.getQty() + "")
            );
        } finally {
            lock.unlock();
        }

    }

    /**
     * 库存列表查询 - 库存管理 - 查询
     *
     * @param inventorySkuQueryDTO
     * @return
     */
    @Override
    public List<InventorySkuVO> selectList(InventorySkuQueryDTO inventorySkuQueryDTO) {
        String sku = inventorySkuQueryDTO.getSku();
        if (StringUtils.isNotEmpty(sku)) {
            List<String> skuSplit = Arrays.asList(sku.split(","));
            List<String> skuList = ListUtils.emptyIfNull(inventorySkuQueryDTO.getSkuList());
            inventorySkuQueryDTO.setSkuList(Stream.of(skuSplit, skuList).flatMap(Collection::stream).distinct().collect(Collectors.toList()));
        }
        return baseMapper.selectListVO(inventorySkuQueryDTO);
    }

    private void handlerQueryWrapper(QueryWrapper<InventoryAvailableQueryDto> queryWrapper, InventoryAvailableQueryDto queryDto) {
        queryWrapper.eq("warehouse_code", queryDto.getWarehouseCode());
        queryWrapper.eq("cus_code", queryDto.getCusCode());
        if (StringUtils.isNotEmpty(queryDto.getSku())) {
            queryWrapper.like("sku", queryDto.getSku());
        }
        if (StringUtils.isNotEmpty(queryDto.getEqSku())) {
            queryWrapper.eq("sku", queryDto.getEqSku());
        }
        if (CollectionUtils.isNotEmpty(queryDto.getSkus())) {
            queryWrapper.in("sku", queryDto.getSkus());
        }
    }

    @Override
    public List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto) {
        if (StringUtils.isEmpty(queryDto.getWarehouseCode())) {
            return Collections.emptyList();
        }
        QueryWrapper<InventoryAvailableQueryDto> queryWrapper = Wrappers.query();
        this.handlerQueryWrapper(queryWrapper, queryDto);
        List<InventoryAvailableListVO> voList = this.baseMapper.queryAvailableList(queryWrapper);
        if (CollectionUtils.isNotEmpty(voList)) {
            // 填充SKU属性信息
            List<String> skus = voList.stream().map(InventoryAvailableListVO::getSku).collect(Collectors.toList());
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            Map<String, BaseProduct> productMap;
            if (CollectionUtils.isNotEmpty(productList)) {
                productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            } else {
                productMap = Collections.emptyMap();
            }
            for (InventoryAvailableListVO vo : voList) {
                this.setFieldValue(vo, productMap.get(vo.getSku()));
            }
        }
        return voList;
    }

    @Override
    public InventoryAvailableListVO queryOnlyAvailable(InventoryAvailableQueryDto queryDto) {
        if (StringUtils.isEmpty(queryDto.getWarehouseCode())) {
            return null;
        }
        QueryWrapper<InventoryAvailableQueryDto> queryWrapper = Wrappers.query();
        this.handlerQueryWrapper(queryWrapper, queryDto);
        InventoryAvailableListVO vo = this.baseMapper.queryOnlyAvailable(queryWrapper);
        if (Objects.nonNull(vo)) {
            // 填充SKU属性信息
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(Collections.singletonList(vo.getSku()));
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            Map<String, BaseProduct> productMap;
            if (CollectionUtils.isNotEmpty(productList)) {
                productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            } else {
                productMap = Collections.emptyMap();
            }
            this.setFieldValue(vo, productMap.get(vo.getSku()));
        }
        return vo;
    }

    private void setFieldValue(InventoryAvailableListVO vo, BaseProduct product) {
        if (Objects.nonNull(product)) {
            vo.setCode(product.getCode());
            vo.setProductName(product.getProductName());
            vo.setInitWeight(product.getInitWeight());
            vo.setInitLength(product.getInitLength());
            vo.setInitWidth(product.getInitWidth());
            vo.setInitHeight(product.getInitHeight());
            vo.setInitVolume(product.getInitVolume());
            vo.setWeight(product.getWeight());
            vo.setLength(product.getLength());
            vo.setWidth(product.getWidth());
            vo.setHeight(product.getHeight());
            vo.setVolume(product.getVolume());
            vo.setBindCode(product.getBindCode());
            vo.setBindCodeName(product.getBindCodeName());
        }
    }

    @Override
    public List<InventoryVO> querySku(InventoryAvailableQueryDto queryDto) {
        if (StringUtils.isEmpty(queryDto.getWarehouseCode())) {
            return Collections.emptyList();
        }
        QueryWrapper<InventoryAvailableQueryDto> queryWrapper = Wrappers.query();
        this.handlerQueryWrapper(queryWrapper, queryDto);
        return this.baseMapper.querySku(queryWrapper);
    }

    @Override
    public InventoryVO queryOnlySku(InventoryAvailableQueryDto queryDto) {
        if (StringUtils.isEmpty(queryDto.getWarehouseCode())) {
            return null;
        }
        QueryWrapper<InventoryAvailableQueryDto> queryWrapper = Wrappers.query();
        this.handlerQueryWrapper(queryWrapper, queryDto);
        return this.baseMapper.queryOnlySku(queryWrapper);
    }

    @Transactional
    @Override
    public int freeze(String invoiceNo, String warehouseCode, String sku, Integer num) {
        return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
            // >=
            queryWrapperConsumer.ge(Inventory::getAvailableInventory, num);
        }, (updateConsumer) -> {
            updateConsumer.setAvailableInventory(updateConsumer.getAvailableInventory() - num);
            updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() + num);
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_3);
    }

    @Transactional
    @Override
    public int unFreeze(String invoiceNo, String warehouseCode, String sku, Integer num) {
        return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
            // >=
            queryWrapperConsumer.ge(Inventory::getFreezeInventory, num);
        }, (updateConsumer) -> {
            updateConsumer.setAvailableInventory(updateConsumer.getAvailableInventory() + num);
            updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() - num);
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_3);
    }

    @Transactional
    @Override
    public int deduction(String invoiceNo, String warehouseCode, String sku, Integer num) {
        return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
            // >=
            queryWrapperConsumer.ge(Inventory::getFreezeInventory, num);
        }, (updateConsumer) -> {
            updateConsumer.setTotalInventory(updateConsumer.getTotalInventory() - num);
            updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() - num);
            updateConsumer.setTotalOutbound(updateConsumer.getTotalOutbound() + num);
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_2);
    }

    @Transactional
    @Override
    public int unDeduction(String invoiceNo, String warehouseCode, String sku, Integer num) {
        return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
        }, (updateConsumer) -> {
            updateConsumer.setTotalInventory(updateConsumer.getTotalInventory() + num);
            updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() + num);
            updateConsumer.setTotalOutbound(updateConsumer.getTotalOutbound() - num);
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_2);
    }

    private int doWorker(String invoiceNo, String warehouseCode, String sku, Integer num,
                         Consumer<LambdaQueryWrapper<Inventory>> queryWrapperConsumer,
                         Consumer<Inventory> updateConsumer,
                         LocalLanguageEnum type) {
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            return 0;
        }
        LambdaQueryWrapper<Inventory> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Inventory::getWarehouseCode, warehouseCode);
        queryWrapper.eq(Inventory::getSku, sku);
        queryWrapperConsumer.accept(queryWrapper);
        List<Inventory> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        Inventory inventory = list.get(0);
        if (null == inventory) {
            return 0;
        }
        Inventory updateInventory = new Inventory();
        updateInventory.setWarehouseCode(inventory.getWarehouseCode());
        updateInventory.setSku(inventory.getSku());
        updateInventory.setTotalInventory(inventory.getTotalInventory());
        updateInventory.setAvailableInventory(inventory.getAvailableInventory());
        updateInventory.setFreezeInventory(inventory.getFreezeInventory());
        updateInventory.setTotalInbound(inventory.getTotalInbound());
        updateInventory.setTotalOutbound(inventory.getTotalOutbound());
        updateInventory.setId(inventory.getId());
        updateConsumer.accept(updateInventory);
        int update = baseMapper.updateById(updateInventory);
        if (update > 0) {
            iInventoryRecordService.saveLogs(type.getKey(), inventory, updateInventory, invoiceNo, null, null, num, "");
        }
        return update;
    }

}