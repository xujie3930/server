package com.szmsd.inventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.BeanUtils;
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
    @Resource
    private IPurchaseService iPurchaseService;

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
                beforeInventory.setCusCode(sku1.getSellerCode());
                afterInventory.setCusCode(sku1.getSellerCode());
            }

            //07-16 集运出库转采购入库后，第一次采购入库完成后，同步处理出库的库存冻结
            String orderNo = inboundInventoryDTO.getOrderNo();

            //根据入库单查询是否有采购单号
            InboundReceiptInfoVO receiptInfo = remoteComponent.getReceiptInfo(orderNo);
            String purchaseOrder = receiptInfo.getOrderNo();
            if (StringUtils.isNotBlank(purchaseOrder)) {
                log.info("采购单 入库 -- 修改 库存 {}", purchaseOrder);
                List<InboundReceiptVO> receiptInfoList = remoteComponent.getReceiptInfoList(purchaseOrder);
                List<String> warehouseNoList = receiptInfoList.stream().map(InboundReceiptVO::getWarehouseNo).collect(Collectors.toList());

                //根据采购单号查询所有的入库单 统计之前该入库单 关联这个sku的冻结的数量
                List<InventoryRecord> inventoryRecordVOS = iInventoryRecordService.getBaseMapper()
                        .selectList(Wrappers.<InventoryRecord>lambdaQuery().eq(InventoryRecord::getSku,sku).in(InventoryRecord::getReceiptNo,warehouseNoList));

                //之前冻结的数量
                int beforeFreeze = inventoryRecordVOS.stream().map(InventoryRecord::getOperator).mapToInt(Integer::parseInt).reduce(Integer::sum).orElse(0);
                //查询该订单的采购单明细 根据sku查询出库单该sku数量
                PurchaseInfoVO purchaseInfoVO = iPurchaseService.selectPurchaseByPurchaseNo(purchaseOrder);
                List<PurchaseInfoDetailVO> purchaseDetailsAddList = purchaseInfoVO.getPurchaseDetailsAddList();
                //总采购数量 = 出库的sku数量
                Integer purchaseNum = purchaseDetailsAddList.stream().filter(x -> x.getSku().equals(sku)).findAny()
                        .map(PurchaseInfoDetailVO::getPurchaseQuantity).orElse(0);
                // 集运出库单出库A  SKU   M个，转入库单，WMS上架N个，此时SKU的库存情况：总库存+N，可用库存：+N-M，冻结库存：+M，总入库+N
                //还能冻结的数量 = 总采购数-之前的冻结数
                int canFreeze = purchaseNum - beforeFreeze;
                int afterTotalInventory = beforeInventory.getTotalInventory() + qty;
                int afterTotalInbound = beforeInventory.getTotalInbound() + qty;
                int afterAvailableInventory = beforeInventory.getAvailableInventory();
                if (canFreeze > 0) {
                    //之前的冻结库存
                    int freezeInventory = Optional.ofNullable(afterInventory.getFreezeInventory()).orElse(0) + qty;
                    //入库数 = 冻结++
                    if (qty >= canFreeze) {
                        //入库数>可冻结数 则 多余的 = 可用库存
                        afterAvailableInventory += (qty - canFreeze);
                        freezeInventory += canFreeze;
                    } else {
                        //入库数<可冻结数 之前的冻结库存 + 入库数
                        freezeInventory += qty;
                    }
                    afterInventory.setFreezeInventory(freezeInventory).setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
                    afterInventory.setLastInboundTime(DateUtils.dateTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", inboundInventoryDTO.getOperateOn()));
                    this.saveOrUpdate(afterInventory);
                } else {
                    //入库数 = 可用++
                    afterAvailableInventory += qty;
                    afterInventory.setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
                    this.saveOrUpdate(afterInventory);
                }
            } else {
                // after inventory
                int afterTotalInventory = beforeInventory.getTotalInventory() + qty;
                int afterAvailableInventory = beforeInventory.getAvailableInventory() + qty;
                int afterTotalInbound = beforeInventory.getTotalInbound() + qty;
                afterInventory.setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
                afterInventory.setLastInboundTime(DateUtils.dateTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", inboundInventoryDTO.getOperateOn()));
                this.saveOrUpdate(afterInventory);
            }

            // 记录库存日志
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), beforeInventory, afterInventory, inboundInventoryDTO.getOrderNo(), inboundInventoryDTO.getOperator(), inboundInventoryDTO.getOperateOn(), qty);
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

    private void handlerSkuQueryWrapper(QueryWrapper<InventoryAvailableQueryDto> queryWrapper, InventoryAvailableQueryDto queryDto) {
        queryWrapper.eq("t.warehouse_code", queryDto.getWarehouseCode());
        queryWrapper.eq("t.cus_code", queryDto.getCusCode());
        if (StringUtils.isNotEmpty(queryDto.getSku())) {
            queryWrapper.like("t.sku", queryDto.getSku());
        }
        if (StringUtils.isNotEmpty(queryDto.getEqSku())) {
            queryWrapper.eq("t.sku", queryDto.getEqSku());
        }
        if (CollectionUtils.isNotEmpty(queryDto.getSkus())) {
            queryWrapper.in("t.sku", queryDto.getSkus());
        }
        if (null != queryDto.getQueryType() && 1 == queryDto.getQueryType()) {
            // 可用库存大于0
            queryWrapper.gt("t.available_inventory", 0);
        }
        if ("SKU".equalsIgnoreCase(queryDto.getQuerySku())) {
            // 这里查询包括SKU的库存和包材的库存
            // 只查询SKU的库存
            queryWrapper.eq("sku.category", "SKU");
        }
        if ("084002".equals(queryDto.getSource())) {
            queryWrapper.eq("sku.source", "084002");
        }
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
        if (null != queryDto.getQueryType() && 1 == queryDto.getQueryType()) {
            // 可用库存大于0
            queryWrapper.gt("available_inventory", 0);
        }
    }

    @Override
    public List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto) {
        if (StringUtils.isEmpty(queryDto.getWarehouseCode()) || StringUtils.isEmpty(queryDto.getCusCode())) {
            return Collections.emptyList();
        }
        QueryWrapper<InventoryAvailableQueryDto> queryWrapper = Wrappers.query();
        this.handlerSkuQueryWrapper(queryWrapper, queryDto);
        List<InventoryAvailableListVO> voList = this.baseMapper.queryAvailableList(queryWrapper);
        /*if (CollectionUtils.isNotEmpty(voList)) {
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
        }*/
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
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_8);
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

    /**
     * 库存调整功能，只负责OMS的可用库存调整。记录日志，不传WMS
     *
     * @param inventoryAdjustmentDTO
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void adjustment(InventoryAdjustmentDTO inventoryAdjustmentDTO) {

        String sku = inventoryAdjustmentDTO.getSku();
        String warehouseCode = inventoryAdjustmentDTO.getWarehouseCode();
        Integer quantity = inventoryAdjustmentDTO.getQuantity();

        AssertUtil.isTrue(quantity != null && quantity > 0, warehouseCode + "仓[" + sku + "]调整数量不能小于1");

        String adjustment = inventoryAdjustmentDTO.getAdjustment();

        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(LocalLanguageTypeEnum.INVENTORY_RECORD_TYPE, adjustment);
        boolean increase = LocalLanguageEnum.INVENTORY_RECORD_TYPE_5 == localLanguageEnum;
        boolean reduce = LocalLanguageEnum.INVENTORY_RECORD_TYPE_6 == localLanguageEnum;
        AssertUtil.isTrue(increase || reduce, "调整类型有误");
        quantity = increase ? quantity : -quantity;
        if (null != inventoryAdjustmentDTO.getFormReturn() && inventoryAdjustmentDTO.getFormReturn())
            localLanguageEnum = LocalLanguageEnum.INVENTORY_RECORD_TYPE_7;
        Lock lock = new ReentrantLock(true);
        try {
            lock.lock();

            Inventory before = this.getOne(new QueryWrapper<Inventory>().lambda().eq(Inventory::getSku, sku).eq(Inventory::getWarehouseCode, warehouseCode));
            //AssertUtil.notNull(before, warehouseCode + "仓没有[" + sku + "]库存记录");
            if (null == before && increase) {
                //String loginSellerCode = Optional.ofNullable(remoteComponent.getLoginUserInfo()).map(SysUser::getSellerCode).orElseThrow(() -> new BaseException("获取用户信息失败!"));
                String loginSellerCode = inventoryAdjustmentDTO.getSellerCode();
                Integer addQut = inventoryAdjustmentDTO.getQuantity();
                Inventory inventory = new Inventory();
                inventory.setSku(inventoryAdjustmentDTO.getSku())
                        .setWarehouseCode(inventoryAdjustmentDTO.getWarehouseCode())
                        .setAvailableInventory(addQut)
                        .setCusCode(loginSellerCode)

                        .setTotalInventory(addQut)
                        .setTotalInbound(addQut);
                baseMapper.insert(inventory);

                log.info(warehouseCode + "仓没有[" + sku + "]库存记录 新增sku 信息 {}", JSONObject.toJSONString(inventory));
                before = new Inventory();
                BeanUtils.copyProperties(inventory, before);
                before.setTotalInventory(0).setTotalInbound(0);
                // 记录库存日志
                //iInventoryRecordService.saveLogs(localLanguageEnum.getKey(), before, inventory, quantity);
                iInventoryRecordService.saveLogs(localLanguageEnum.getKey(), before, inventory, quantity, inventoryAdjustmentDTO.getReceiptNo());
                return;
            }
            int afterTotalInventory = before.getTotalInventory() + quantity;
            int afterAvailableInventory = before.getAvailableInventory() + quantity;
            AssertUtil.isTrue(afterTotalInventory > 0 && afterAvailableInventory > 0, warehouseCode + "仓[" + sku + "]可用库存调减数量不足[" + before.getAvailableInventory() + "]");

            Inventory after = new Inventory();
            after.setId(before.getId()).setCusCode(before.getCusCode()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory);
            this.updateById(after);

            // 记录库存日志
            iInventoryRecordService.saveLogs(localLanguageEnum.getKey(), before, after, quantity, inventoryAdjustmentDTO.getReceiptNo());
        } finally {
            lock.unlock();
        }
    }

    private int doWorker(String invoiceNo, String warehouseCode, String sku, Integer num,
                         Consumer<LambdaQueryWrapper<Inventory>> queryWrapperConsumer,
                         Consumer<Inventory> updateConsumer,
                         LocalLanguageEnum type) {
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("999", "参数不全2");
        }
        LambdaQueryWrapper<Inventory> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Inventory::getWarehouseCode, warehouseCode);
        queryWrapper.eq(Inventory::getSku, sku);
        queryWrapperConsumer.accept(queryWrapper);
        List<Inventory> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new CommonException("999", "[" + sku + "]库存不足");
        }
        Inventory inventory = list.get(0);
        if (null == inventory) {
            throw new CommonException("999", "[" + sku + "]库存不存在");
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
        if (update < 1) {
            throw new CommonException("999", "[" + sku + "]库存操作失败");
        }
        // 添加日志
        iInventoryRecordService.saveLogs(type.getKey(), inventory, updateInventory, invoiceNo, null, null, num, "");
        return update;
    }

    @Override
    public List<Inventory> getWarehouseSku() {
        LambdaQueryWrapper<Inventory> query = Wrappers.lambdaQuery();
        query.gt(Inventory::getAvailableInventory, 0);
        return this.list(query);
    }

}