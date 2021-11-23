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
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryOccupy;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.InventoryOccupyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.szmsd.common.core.language.enums.LocalLanguageEnum.*;

@Slf4j
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Lazy
    @Resource
    private IInventoryRecordService iInventoryRecordService;

    @Resource
    private RemoteComponent remoteComponent;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private InventoryOccupyService inventoryOccupyService;
    @Resource
    private RedissonClient redissonClient;
    private final static long WAIT_TIME = 10L;

    /**
     * 上架入库 - Inbound - /api/inbound/receiving #B1 接收入库上架 - 修改库存
     *
     * @param inboundInventoryDTO
     */
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void inbound(InboundInventoryDTO inboundInventoryDTO) {
        log.info("上架入库：{}", inboundInventoryDTO);
        String lockKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        RLock lock = redissonClient.getLock("InventoryServiceImpl#inbound" + lockKey);
        // 获取锁
        try {
            if (lock.tryLock(WAIT_TIME, TimeUnit.SECONDS)) {
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
                // 08-17 取消之前的提交
               /* //根据入库单查询是否有采购单号
                InboundReceiptInfoVO receiptInfo = remoteComponent.getReceiptInfo(orderNo);
                String purchaseOrder = receiptInfo.getOrderNo();
                if (StringUtils.isNotBlank(purchaseOrder)) {
                    log.info("采购单 入库 -- 修改 库存 {}", purchaseOrder);
                    List<InboundReceiptVO> receiptInfoList = remoteComponent.getReceiptInfoList(purchaseOrder);
                    List<String> warehouseNoList = receiptInfoList.stream().map(InboundReceiptVO::getWarehouseNo).collect(Collectors.toList());

                    //根据采购单号查询所有的入库单 统计之前该入库单 关联这个sku的冻结的数量
                    List<InventoryRecord> inventoryRecordVOS = iInventoryRecordService.getBaseMapper()
                            .selectList(Wrappers.<InventoryRecord>lambdaQuery().eq(InventoryRecord::getSku, sku).in(InventoryRecord::getReceiptNo, warehouseNoList));

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
                        afterInventory.setLastInboundTime(DateUtils.dateTime("yyyy-MM-dd'T'HH:mm:ss", inboundInventoryDTO.getOperateOn()));
                        this.saveOrUpdate(afterInventory);
                    } else {
                        //入库数 = 可用++
                        afterAvailableInventory += qty;
                        afterInventory.setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
                        this.saveOrUpdate(afterInventory);
                    }
                } else */
                {
                    // after inventory
                    int afterTotalInventory = beforeInventory.getTotalInventory() + qty;
                    int afterAvailableInventory = beforeInventory.getAvailableInventory() + qty;
                    int afterTotalInbound = beforeInventory.getTotalInbound() + qty;
                    afterInventory.setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
                    afterInventory.setLastInboundTime(DateUtils.dateTime("yyyy-MM-dd'T'HH:mm:ss", inboundInventoryDTO.getOperateOn()));
                    this.saveOrUpdate(afterInventory);
                }

                // 记录库存日志
                iInventoryRecordService.saveLogs(INVENTORY_RECORD_TYPE_1.getKey(), beforeInventory, afterInventory, inboundInventoryDTO.getOrderNo(), inboundInventoryDTO.getOperator(), inboundInventoryDTO.getOperateOn(), qty);
            } else {
                throw new RuntimeException("请求超时,请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上架入库异常：", e);
            throw new RuntimeException("上架入库异常,请稍后重试");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
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
    public int freeze(String invoiceNo, String warehouseCode, String sku, Integer num, Integer freeType, String cusCode) {
        /*return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
            // 库存可以为负数
            if (null == freeType) {
                // >=
                queryWrapperConsumer.ge(Inventory::getAvailableInventory, num);
            }
        }, (dbInventory) -> {
            // 库存可以为负数，但是库存是空的，新增一条库存记录
            if (null != freeType && null == dbInventory) {
                Inventory inventory = new Inventory();
                inventory.setCusCode(cusCode)
                        .setSku(sku)
                        .setWarehouseCode(warehouseCode)
                        .setTotalInventory(0)
                        .setAvailableInventory(0)
                        .setFreezeInventory(0)
                        .setTotalInbound(0)
                        .setTotalOutbound(0);
                baseMapper.insert(inventory);
                return inventory;
            }
            return dbInventory;
        }, (updateConsumer) -> {
            updateConsumer.setAvailableInventory(updateConsumer.getAvailableInventory() - num);
            updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() + num);
        }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_3);*/
        // 仓库，SKU，数量不能为空，并且数量不能小于1
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("500", "冻结库存参数不全");
        }
        try {
            // 判断这个单有没有占用记录
            InventoryOccupy occupyInfo = this.inventoryOccupyService.getOccupyInfo(cusCode, warehouseCode, sku, invoiceNo);
            if (null != occupyInfo) {
                return 1;
            }
            // 根据仓库，SKU查询库存记录
            LambdaQueryWrapper<Inventory> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(Inventory::getWarehouseCode, warehouseCode);
            queryWrapper.eq(Inventory::getSku, sku);
            // 额外的条件（如果freeType是空的，可用库存要大于冻结的数量，如果不为空，可用库存不做限制）
            if (null == freeType) {
                // >=
                queryWrapper.ge(Inventory::getAvailableInventory, num);
            }
            List<Inventory> list = this.list(queryWrapper);
            Inventory dbInventory = null;
            // 库存记录正常情况下，一个仓库下，一个SKU只有一条记录
            if (CollectionUtils.isNotEmpty(list)) {
                dbInventory = list.get(0);
            }
            // 冻结时，如果库存不存在记录，freeType不为空，就手动新增一条库存记录
            if (null != freeType && null == dbInventory) {
                dbInventory = new Inventory();
                dbInventory.setCusCode(cusCode)
                        .setSku(sku)
                        .setWarehouseCode(warehouseCode)
                        .setTotalInventory(0)
                        .setAvailableInventory(0)
                        .setFreezeInventory(0)
                        .setTotalInbound(0)
                        .setTotalOutbound(0);
                // 新增之后会返回id到dbInventory对象中
                baseMapper.insert(dbInventory);
            }
            // 没有库存记录信息
            if (null == dbInventory) {
                throw new CommonException("500", "[" + sku + "]冻结库存不足");
            }
            // 更新库存记录信息
            Inventory updateInventory = new Inventory();
            updateInventory.setWarehouseCode(dbInventory.getWarehouseCode());
            updateInventory.setSku(dbInventory.getSku());
            updateInventory.setTotalInventory(dbInventory.getTotalInventory());
            updateInventory.setAvailableInventory(dbInventory.getAvailableInventory());
            updateInventory.setFreezeInventory(dbInventory.getFreezeInventory());
            updateInventory.setTotalInbound(dbInventory.getTotalInbound());
            updateInventory.setTotalOutbound(dbInventory.getTotalOutbound());
            // 1.减可用库存。2.增加冻结库存
            updateInventory.setAvailableInventory(dbInventory.getAvailableInventory() - num);
            updateInventory.setFreezeInventory(dbInventory.getFreezeInventory() + num);
            updateInventory.setId(dbInventory.getId());
            int update = baseMapper.updateById(updateInventory);
            if (update < 1) {
                throw new CommonException("500", "[" + sku + "]冻结库存操作失败");
            }
            // 添加库存占用记录
            InventoryOccupy inventoryOccupy = new InventoryOccupy();
            inventoryOccupy.setCusCode(cusCode);
            inventoryOccupy.setWarehouseCode(warehouseCode);
            inventoryOccupy.setSku(sku);
            inventoryOccupy.setQuantity(num);
            inventoryOccupy.setReceiptNo(invoiceNo);
            inventoryOccupy.setInventoryId(dbInventory.getId());
            this.inventoryOccupyService.save(inventoryOccupy);
            // 添加日志
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_3.getKey(), dbInventory, updateInventory, invoiceNo, null, null, num, "");
            return update;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                throw e;
            }
            throw new CommonException("500", "冻结库存失败，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku + "，错误原因：" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public int unFreeze(String invoiceNo, String warehouseCode, String sku, Integer num, Integer freeType, String cusCode) {
        /*return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
                    if (null == freeType) {
                        // >=
                        queryWrapperConsumer.ge(Inventory::getFreezeInventory, num);
                    }
                }, null,
                (updateConsumer) -> {
                    updateConsumer.setAvailableInventory(updateConsumer.getAvailableInventory() + num);
                    updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() - num);
                }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_8);*/
        // 仓库，SKU，数量不能为空，并且数量不能小于1
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("500", "解冻库存参数不全");
        }
        try {
            InventoryOccupy occupyInfo = this.inventoryOccupyService.getOccupyInfo(cusCode, warehouseCode, sku, invoiceNo);
            if (null == occupyInfo) {
                // throw new CommonException("500", "解冻库存失败，无库存占用记录，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku);
                // 无库存占用记录，直接返回成功
                return 1;
            }
            // 验证数量是不是一样的
            if (!num.equals(occupyInfo.getQuantity())) {
                throw new CommonException("500", "解冻库存失败，占用数量与操作数量不一致");
            }
            // 修改库存记录信息
            Inventory dbInventory = super.getById(occupyInfo.getInventoryId());
            if (null == dbInventory) {
                throw new CommonException("500", "解冻库存失败，根据库存占用记录查询库存记录失败");
            }
            Inventory updateInventory = new Inventory();
            // 与冻结库存逻辑相反
            // 1.加可用库存。2.减少冻结库存
            updateInventory.setAvailableInventory(dbInventory.getAvailableInventory() + num);
            updateInventory.setFreezeInventory(dbInventory.getFreezeInventory() - num);
            updateInventory.setId(dbInventory.getId());
            int update = baseMapper.updateById(updateInventory);
            if (update < 1) {
                throw new CommonException("500", "[" + sku + "]解冻库存失败，修改库存数据失败");
            }
            // 删除库存占用记录
            this.inventoryOccupyService.removeById(occupyInfo.getId());
            // 填充日志记录，日志不能为空，需要设置一个值
            updateInventory.setWarehouseCode(dbInventory.getWarehouseCode());
            updateInventory.setSku(dbInventory.getSku());
            updateInventory.setTotalInventory(dbInventory.getTotalInventory());
            updateInventory.setTotalInbound(dbInventory.getTotalInbound());
            updateInventory.setTotalOutbound(dbInventory.getTotalOutbound());
            // 添加日志
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_8.getKey(), dbInventory, updateInventory, invoiceNo, null, null, num, "");
            return update;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                throw e;
            }
            throw new CommonException("500", "解冻失败，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku + "，错误原因：" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public int deduction(String invoiceNo, String warehouseCode, String sku, Integer num, String cusCode) {
        /*return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
                    // >=
                    queryWrapperConsumer.ge(Inventory::getFreezeInventory, num);
                }, null,
                (updateConsumer) -> {
                    updateConsumer.setTotalInventory(updateConsumer.getTotalInventory() - num);
                    updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() - num);
                    updateConsumer.setTotalOutbound(updateConsumer.getTotalOutbound() + num);
                }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_2);*/
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("500", "扣减库存参数不全");
        }
        try {
            InventoryOccupy occupyInfo = this.inventoryOccupyService.getOccupyInfo(cusCode, warehouseCode, sku, invoiceNo);
            if (null == occupyInfo) {
                // throw new CommonException("500", "扣减库存失败，无库存占用记录，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku);
                // 无占用记录，不处理扣减库存逻辑
                return 1;
            }
            // 验证数量是不是一样的
            if (!num.equals(occupyInfo.getQuantity())) {
                throw new CommonException("500", "扣减库存失败，占用数量与操作数量不一致");
            }
            // 修改库存记录信息
            Inventory dbInventory = super.getById(occupyInfo.getInventoryId());
            if (null == dbInventory) {
                throw new CommonException("500", "扣减库存失败，根据库存占用记录查询库存记录失败");
            }
            Inventory updateInventory = new Inventory();
            // 1.总库存减少，2.冻结库存减少，3.出库数量增加
            updateInventory.setTotalInventory(dbInventory.getTotalInventory() - num);
            updateInventory.setFreezeInventory(dbInventory.getFreezeInventory() - num);
            updateInventory.setTotalOutbound(dbInventory.getTotalOutbound() + num);
            updateInventory.setId(dbInventory.getId());
            int update = baseMapper.updateById(updateInventory);
            if (update < 1) {
                throw new CommonException("500", "[" + sku + "]扣减库存失败，修改库存数据失败");
            }
            // 删除库存占用记录
            this.inventoryOccupyService.removeById(occupyInfo.getId());
            // 填充日志记录，日志不能为空，需要设置一个值
            updateInventory.setWarehouseCode(dbInventory.getWarehouseCode());
            updateInventory.setSku(dbInventory.getSku());
            updateInventory.setAvailableInventory(dbInventory.getAvailableInventory());
            updateInventory.setTotalInbound(dbInventory.getTotalInbound());
            // 添加日志
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_2.getKey(), dbInventory, updateInventory, invoiceNo, null, null, num, "");
            return update;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                throw e;
            }
            throw new CommonException("500", "扣减库存失败，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku + "，错误原因：" + e.getMessage());
        }
    }

    @Transactional
    @Override
    public int unDeduction(String invoiceNo, String warehouseCode, String sku, Integer num, String cusCode) {
        /*return this.doWorker(invoiceNo, warehouseCode, sku, num, (queryWrapperConsumer) -> {
                }, null,
                (updateConsumer) -> {
                    updateConsumer.setTotalInventory(updateConsumer.getTotalInventory() + num);
                    updateConsumer.setFreezeInventory(updateConsumer.getFreezeInventory() + num);
                    updateConsumer.setTotalOutbound(updateConsumer.getTotalOutbound() - num);
                }, LocalLanguageEnum.INVENTORY_RECORD_TYPE_1);*/
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("500", "取消扣减库存参数不全");
        }
        try {
            // 根据仓库，SKU查询库存记录
            LambdaQueryWrapper<Inventory> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(Inventory::getWarehouseCode, warehouseCode);
            queryWrapper.eq(Inventory::getSku, sku);
            List<Inventory> list = this.list(queryWrapper);
            Inventory dbInventory = null;
            // 库存记录正常情况下，一个仓库下，一个SKU只有一条记录
            if (CollectionUtils.isNotEmpty(list)) {
                dbInventory = list.get(0);
            }
            // 没有库存记录信息
            if (null == dbInventory) {
                throw new CommonException("500", "[" + sku + "]取消扣减库存不足");
            }
            // 更新库存记录信息
            Inventory updateInventory = new Inventory();
            // 1.总库存加，2.冻结库存加，3.出库数量减
            updateInventory.setTotalInventory(dbInventory.getTotalInventory() + num);
            updateInventory.setFreezeInventory(dbInventory.getFreezeInventory() + num);
            updateInventory.setTotalOutbound(dbInventory.getTotalOutbound() - num);
            updateInventory.setId(dbInventory.getId());
            int update = baseMapper.updateById(updateInventory);
            if (update < 1) {
                throw new CommonException("500", "[" + sku + "]取消扣减库存失败，修改库存数据失败");
            }
            // 添加库存占用记录
            InventoryOccupy inventoryOccupy = new InventoryOccupy();
            inventoryOccupy.setCusCode(cusCode);
            inventoryOccupy.setWarehouseCode(warehouseCode);
            inventoryOccupy.setSku(sku);
            inventoryOccupy.setQuantity(num);
            inventoryOccupy.setReceiptNo(invoiceNo);
            inventoryOccupy.setInventoryId(dbInventory.getId());
            this.inventoryOccupyService.save(inventoryOccupy);
            // 填充日志记录，日志不能为空，需要设置一个值
            updateInventory.setWarehouseCode(dbInventory.getWarehouseCode());
            updateInventory.setSku(dbInventory.getSku());
            updateInventory.setAvailableInventory(dbInventory.getAvailableInventory());
            updateInventory.setTotalInbound(dbInventory.getTotalInbound());
            // 添加日志
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), dbInventory, updateInventory, invoiceNo, null, null, num, "");
            return update;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                throw e;
            }
            throw new CommonException("500", "取消扣减库存失败，单据号：" + invoiceNo + "，仓库：" + warehouseCode + "，SKU：" + sku + "，错误原因：" + e.getMessage());
        }
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
        boolean increase = INVENTORY_RECORD_TYPE_5 == localLanguageEnum;
        boolean reduce = LocalLanguageEnum.INVENTORY_RECORD_TYPE_6 == localLanguageEnum;
        AssertUtil.isTrue(increase || reduce, "调整类型有误");
        quantity = increase ? quantity : -quantity;
        if (null != inventoryAdjustmentDTO.getFormReturn() && inventoryAdjustmentDTO.getFormReturn())
            localLanguageEnum = INVENTORY_RECORD_TYPE_7;
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
                         Function<Inventory, Inventory> beforeHandler,
                         Consumer<Inventory> updateConsumer,
                         LocalLanguageEnum type) {
        // 仓库，SKU，数量不能为空，并且数量不能小于1
        if (StringUtils.isEmpty(warehouseCode)
                || StringUtils.isEmpty(sku)
                || Objects.isNull(num)
                || num < 1) {
            throw new CommonException("999", "参数不全2");
        }
        // 根据仓库，SKU查询库存记录
        LambdaQueryWrapper<Inventory> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Inventory::getWarehouseCode, warehouseCode);
        queryWrapper.eq(Inventory::getSku, sku);
        // 额外的条件（如果freeType是空的，可用库存要大于冻结的数量，如果不为空，可用库存不做限制）
        queryWrapperConsumer.accept(queryWrapper);
        List<Inventory> list = this.list(queryWrapper);
        Inventory dbInventory = null;
        // 库存记录正常情况下，一个仓库下，一个SKU只有一条记录
        if (CollectionUtils.isNotEmpty(list)) {
            dbInventory = list.get(0);
        }
        // 冻结时，如果库存不存在记录，freeType不为空，就手动新增一条库存记录
        if (null != beforeHandler) {
            dbInventory = beforeHandler.apply(dbInventory);
        }
        // 没有库存记录信息
        if (null == dbInventory) {
            throw new CommonException("999", "[" + sku + "]库存不足");
        }
        // 更新库存记录信息
        Inventory updateInventory = new Inventory();
        updateInventory.setWarehouseCode(dbInventory.getWarehouseCode());
        updateInventory.setSku(dbInventory.getSku());
        updateInventory.setTotalInventory(dbInventory.getTotalInventory());
        updateInventory.setAvailableInventory(dbInventory.getAvailableInventory());
        updateInventory.setFreezeInventory(dbInventory.getFreezeInventory());
        updateInventory.setTotalInbound(dbInventory.getTotalInbound());
        updateInventory.setTotalOutbound(dbInventory.getTotalOutbound());
        updateInventory.setId(dbInventory.getId());
        updateConsumer.accept(updateInventory);
        int update = baseMapper.updateById(updateInventory);
        if (update < 1) {
            throw new CommonException("999", "[" + sku + "]库存操作失败");
        }
        // 添加日志
        iInventoryRecordService.saveLogs(type.getKey(), dbInventory, updateInventory, invoiceNo, null, null, num, "");
        return update;
    }

    @Override
    public List<Inventory> getWarehouseSku() {
        LambdaQueryWrapper<Inventory> query = Wrappers.lambdaQuery();
        query.gt(Inventory::getAvailableInventory, 0);
        return this.list(query);
    }

    @Override
    public List<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO) {
        return baseMapper.queryFinishList(queryFinishListDTO);
    }

    /**
     * 可用库存 -（按入库时间 先进先出）总数 剩余的都是
     * 把入库记录 按 库龄 分组求和
     *
     * @param warehouseCode
     * @param sku
     * @return
     */
    @Override
    public List<SkuInventoryAgeVo> queryInventoryAgeBySku(String warehouseCode, String sku) {
        Inventory inventory = baseMapper.selectOne(Wrappers.<Inventory>lambdaQuery().eq(Inventory::getSku, sku).eq(Inventory::getWarehouseCode, warehouseCode));
        if (null == inventory) return new ArrayList<>();
        int availableInventory = Optional.ofNullable(inventory.getAvailableInventory()).orElse(0);
        log.info("可用库存 {}", availableInventory);
        if (availableInventory <= 0) return new ArrayList<>();
        //查询 库存记录信息
        List<InventoryRecord> inventoryRecords = iInventoryRecordService.getBaseMapper().selectList(Wrappers.<InventoryRecord>lambdaQuery()
                .eq(InventoryRecord::getWarehouseCode, warehouseCode)
                .eq(InventoryRecord::getSku, sku)
                .in(InventoryRecord::getType, INVENTORY_RECORD_TYPE_1.getKey(), INVENTORY_RECORD_TYPE_5.getKey(), INVENTORY_RECORD_TYPE_7.getKey())
                .orderByDesc(BaseEntity::getCreateTime));
        SkuInventoryAgeVo skuInventoryAgeVo = new SkuInventoryAgeVo();
        skuInventoryAgeVo.setSku(sku);
        if (CollectionUtils.isEmpty(inventoryRecords)) return new ArrayList<>();
        //构造一个入库队列 + 出库队列
        //构造初始化 历史记录 入库  1 5 7 是 加库存
        Queue<SkuInventoryAgeVo> input = inventoryRecords.stream().map(x -> {
            SkuInventoryAgeVo skuInventoryAge = new SkuInventoryAgeVo();
            //设置数量，库龄 类型
            skuInventoryAge.setSku(x.getSku()).setWarehouseCode(x.getWarehouseCode())
                    .setNum(x.getQuantity()).setType(x.getType())

                    .setCreateTime(x.getCreateTime());
            return skuInventoryAge;
        }).filter(x -> x.getNum() != null && x.getNum() > 0).collect(Collectors.toCollection(LinkedBlockingDeque::new));

        List<SkuInventoryAgeVo> skuInventoryAgeVos = new ArrayList<>(input.size());
        processingQuantity(input, availableInventory, skuInventoryAgeVos);
        Collection<SkuInventoryAgeVo> values = skuInventoryAgeVos.stream().collect(Collectors.toMap(SkuInventoryAgeVo::getStorageAge, x -> x, (x1, x2) -> {
            x1.setNum(x1.getNum() + x2.getNum());
            return x1;
        })).values();

        return new ArrayList<>(values);
    }

    /**
     * 处理数量
     *
     * @param queue
     * @param
     */
    public static void processingQuantity(Queue<SkuInventoryAgeVo> queue, int totalNum, List<SkuInventoryAgeVo> resultList) {
        SkuInventoryAgeVo element = queue.poll();
        if (null == element) {
            log.error("处理数量异常,总入库数 小于 当前库存");
            return;
        }
        Integer nowNum = element.getNum();
        if (nowNum < totalNum) {
            resultList.add(element);
            totalNum -= nowNum;
            processingQuantity(queue, totalNum, resultList);
        } else {
            element.setNum(nowNum);
            resultList.add(element);
        }
    }


}