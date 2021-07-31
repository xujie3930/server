package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.SkuVolumeVO;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryRecordServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements IInventoryRecordService {

    @Resource
    private RemoteComponent remoteComponent;

    @Resource
    private IInventoryService iInventoryService;

    /**
     * 保存入库操作日志
     *
     * @param type
     * @param beforeInventory
     * @param afterInventory
     * @param quantity
     */
    @Override
    public void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, Integer quantity) {
        this.saveLogs(type, beforeInventory, afterInventory, "", null, null, quantity, "");
    }

    @Override
    public void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, Integer quantity, String receiptNo) {
        this.saveLogs(type, beforeInventory, afterInventory, receiptNo, null, null, quantity, "");
    }

    @Override
    public void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity) {
        this.saveLogs(type, beforeInventory, afterInventory, receiptNo, operator, operateOn, quantity, "");
    }

    @Override
    public void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity, String... placeholder) {
        InventoryRecord inventoryRecord = new InventoryRecord();
        inventoryRecord.setType(type);
        inventoryRecord.setReceiptNo(receiptNo);
        inventoryRecord.setSku(afterInventory.getSku());
        inventoryRecord.setWarehouseCode(afterInventory.getWarehouseCode());
        inventoryRecord.setQuantity(quantity);
        inventoryRecord.setBeforeTotalInventory(beforeInventory.getTotalInventory());
        inventoryRecord.setBeforeAvailableInventory(beforeInventory.getAvailableInventory());
        inventoryRecord.setBeforeFreezeInventory(beforeInventory.getFreezeInventory());
        inventoryRecord.setBeforeTotalInbound(beforeInventory.getTotalInbound());
        inventoryRecord.setBeforeTotalOutbound(beforeInventory.getTotalOutbound());
        inventoryRecord.setAfterTotalInventory(afterInventory.getTotalInventory());
        inventoryRecord.setAfterAvailableInventory(afterInventory.getAvailableInventory());
        inventoryRecord.setAfterFreezeInventory(afterInventory.getFreezeInventory());
        inventoryRecord.setAfterTotalInbound(afterInventory.getTotalInbound());
        inventoryRecord.setAfterTotalOutbound(afterInventory.getTotalOutbound());
        inventoryRecord.setCusCode(beforeInventory.getCusCode());
        String logs = getLogs(type, placeholder);
        inventoryRecord.setRemark(logs);
        inventoryRecord.setOperator(StringUtils.isEmpty(operator) ? beforeInventory.getCreateByName() : operator);
        inventoryRecord.setOperateOn(StringUtils.isEmpty(operateOn) ? DateUtils.parseDateToStr("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Date()) : operateOn);
        inventoryRecord.setPlaceholder(String.join(",", placeholder));
        log.info("保存入库操作日志：" + inventoryRecord);
        this.save(inventoryRecord);
        log.info("保存入库操作日志：操作完成");
    }

    @Override
    public List<InventoryRecordVO> selectList(InventoryRecordQueryDTO inventoryRecordQueryDTO) {
        return baseMapper.selectQueryList(inventoryRecordQueryDTO);
    }

    /**
     * 查询入库日志 - 按sku 仓库代码统计sku的体积
     *
     * @param inventorySkuVolumeQueryDTO
     * @return
     */
    @Override
    public List<InventorySkuVolumeVO> selectSkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
        inventorySkuVolumeQueryDTO = Optional.ofNullable(inventorySkuVolumeQueryDTO).orElse(new InventorySkuVolumeQueryDTO());

        // 获取库存
        List<InventorySkuVO> inventoryList = iInventoryService.selectList(new InventorySkuQueryDTO().setSku(inventorySkuVolumeQueryDTO.getSku()).setWarehouseCode(inventorySkuVolumeQueryDTO.getWarehouseCode()));
        inventoryList = inventoryList.stream().filter(item -> item.getAvailableInventory() > 0).collect(Collectors.toList());

        // 获取sku信息
        List<String> skuList = inventoryList.stream().map(InventorySkuVO::getSku).collect(Collectors.toList());
        List<BaseProductMeasureDto> skuDataList = remoteComponent.listSku(skuList);
        Map<String, BaseProductMeasureDto> skuData = skuDataList.stream().collect(Collectors.groupingBy(BaseProductMeasureDto::getCode, Collectors.collectingAndThen(Collectors.toList(), e -> e.get(0))));

        // 计算sku可用库存体积
        Map<String, List<InventorySkuVO>> collect = inventoryList.stream().collect(Collectors.groupingBy(InventorySkuVO::getWarehouseCode));
        return collect.entrySet().stream().map(item -> {
            InventorySkuVolumeVO inventorySkuVolumeVO = new InventorySkuVolumeVO();

            String warehouseCode = item.getKey();
            inventorySkuVolumeVO.setWarehouseCode(warehouseCode);

            List<SkuVolumeVO> skuVolumeVO = item.getValue().stream().map(skuR -> {

                String sku = skuR.getSku();
                BaseProductMeasureDto product = skuData.get(sku);
                BigDecimal skuVolume = BigDecimal.ZERO;
                String cusCode = null;
                if (product != null) {
                    BigDecimal initVolume = product.getInitVolume();
                    skuVolume = initVolume == null ? skuVolume : initVolume;
                    cusCode = product.getSellerCode();
                }

                // 可用数量
                Integer availableInventory = skuR.getAvailableInventory();

                // 查询进入库记录
                String startTime = DateUtils.parseDateToStr("yyyy-MM-dd'T'00:00:00.000'Z'", DateUtils.parseDate(DateUtils.getPastDate(90)));
                String endTime = DateUtils.parseDateToStr("yyyy-MM-dd'T'23:23:59.999'Z'", DateUtils.getNowDate());

                // 所有入库记录条数 避免死循环
                int count = this.count(new QueryWrapper<InventoryRecord>().lambda().eq(InventoryRecord::getSku, sku).eq(InventoryRecord::getWarehouseCode, warehouseCode).gt(InventoryRecord::getQuantity, 0));
                List<InventoryRecordVO> inventoryRecordVOS = recursionType1Record(sku, warehouseCode, startTime, endTime, new ArrayList<>(), count, availableInventory);
                String finalCusCode = cusCode;
                BigDecimal finalSkuVolume = skuVolume;
                List<SkuVolumeVO> result = inventoryRecordVOS.stream().map(record -> {
                    BigDecimal volume = new BigDecimal(record.getQuantity()).multiply(finalSkuVolume);
                    return new SkuVolumeVO().setSku(sku).setQty(record.getQuantity()).setSingleVolume(finalSkuVolume).setVolume(volume).setWarehouseNo(warehouseCode).setOperateOn(record.getOperateOn()).setCusCode(finalCusCode);
                }).collect(Collectors.collectingAndThen(Collectors.toList(), e -> {

                    // 符合条件的总入库数量
                    int totalQty = inventoryRecordVOS.stream().mapToInt(InventoryRecordVO::getQuantity).sum();

                    // 溢出数量
                    int overflowQty = totalQty - availableInventory;
                    if (overflowQty > 0) {
                        for (int i = e.size() - 1; i >= 0; i--) {
                            if (overflowQty > 0) {
                                SkuVolumeVO lastVolume = e.get(i);
                                overflowQty = lastVolume.getQty() - overflowQty;
                                if (overflowQty > 0) {
                                    lastVolume.setQty(overflowQty);
                                    BigDecimal volume = (lastVolume.getSingleVolume()).multiply(BigDecimal.valueOf(lastVolume.getQty()));
                                    lastVolume.setVolume(volume);
                                    break;
                                }
                                e = e.subList(0, i);
                            }
                        }
                    }
                    return e;
                }));
                return result;
            }).flatMap(Collection::stream).collect(Collectors.toList());
            inventorySkuVolumeVO.setSkuVolumes(skuVolumeVO);
            return inventorySkuVolumeVO;
        }).collect(Collectors.toList());
    }

    /**
     * 递归入库记录
     *
     * @param sku
     * @param warehouse
     * @param startTime yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     * @param endTime yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     * @param records
     * @param count 条数满足后跳出
     * @param qty records -> quantity >= records 跳出
     * @return
     */
    public List<InventoryRecordVO> recursionType1Record(String sku, String warehouse, String startTime, String endTime, List<InventoryRecordVO> records, Integer count, Integer qty) {
        if (CollectionUtils.isNotEmpty(records)) {
            startTime = DateUtils.parseDateToStr("yyyy-MM-dd'T'00:00:00.000'Z'", DateUtils.getPastDate(DateUtils.dateTime("yyyy-MM-dd", startTime), 90));
            endTime = DateUtils.parseDateToStr("yyyy-MM-dd'T'23:23:59.999'Z'", DateUtils.getPastDate(DateUtils.dateTime("yyyy-MM-dd", endTime), 90));
        }
        if (records == null) {
            records = new ArrayList<>();
        }
        Integer searchQty = records.stream().mapToInt(InventoryRecordVO::getQuantity).sum();
        List<InventoryRecordVO> inventoryRecords = this.selectList(new InventoryRecordQueryDTO().setSku(sku).setWarehouseCode(warehouse).setTimeType(InventoryRecordQueryDTO.TimeType.OPERATE_ON).setStartTime(startTime).setEndTime(endTime).setType("1"));
        for (InventoryRecordVO record : inventoryRecords) {
            if (record.getQuantity() < 1) {
                continue;
            }
            searchQty += record.getQuantity();
            records.add(record);
            if (searchQty >= qty) {
                return records;
            }
        }
        if (count == records.size()) {
            return records;
        }
        return recursionType1Record(sku, warehouse, startTime, endTime, records, count, qty);
    }

    private static String getLogs(String type, String... placeholder) {
        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(LocalLanguageTypeEnum.INVENTORY_RECORD_LOGS, type);
        if (localLanguageEnum == null) {
            log.error("没有维护[{}]枚举语言[{}]", "INVENTORY_RECORD_LOGS", type);
            return "";
        }
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        String logs = localLanguageEnum.getZhName();
        if ("en".equals(len)) {
            logs = localLanguageEnum.getEhName();
        }
        if (localLanguageEnum == LocalLanguageEnum.INBOUND_INVENTORY_LOG) {
            return MessageFormat.format(logs, placeholder);
        }
        return logs;
    }

}

