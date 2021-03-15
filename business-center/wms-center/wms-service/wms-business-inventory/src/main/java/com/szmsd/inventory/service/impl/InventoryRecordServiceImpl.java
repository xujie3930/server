package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
public class InventoryRecordServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements IInventoryRecordService {

    /**
     * 保存入库操作日志
     * @param type
     * @param beforeInventory
     * @param afterInventory
     * @param receiptNo
     * @param operator
     * @param operateOn
     * @param quantity
     * @param placeholder
     */
    @Override
    public void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity, String placeholder) {
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
        inventoryRecord.setAfterTotalInventory(beforeInventory.getTotalInventory());
        inventoryRecord.setAfterAvailableInventory(beforeInventory.getAvailableInventory());
        inventoryRecord.setAfterFreezeInventory(beforeInventory.getFreezeInventory());
        inventoryRecord.setAfterTotalInbound(beforeInventory.getTotalInbound());
        inventoryRecord.setAfterTotalOutbound(beforeInventory.getTotalOutbound());
        String logs = getLogs(type, inventoryRecord.getReceiptNo(), operator, operateOn, quantity);
        inventoryRecord.setRemark(logs);
        inventoryRecord.setOperator(operator);
        inventoryRecord.setOperateOn(operateOn);
        inventoryRecord.setPlaceholder("".equals(placeholder) ? null : placeholder);
        log.info("保存入库操作日志：" + inventoryRecord);
        this.save(inventoryRecord);
        log.info("保存入库操作日志：操作完成");
    }

    @Override
    public List<InventoryRecordVO> selectList(InventoryRecordQueryDTO inventoryRecordQueryDTO) {
        return baseMapper.selectList(inventoryRecordQueryDTO);
    }

    private static String getLogs(String type, String receiptNo, String operator, String operateOn, Integer quantity) {
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
        switch (len) {
            case "en":
                return localLanguageEnum.getEhName();
        }
        switch (localLanguageEnum) {
            case INBOUND_INVENTORY_LOG:
                return MessageFormat.format(logs, operator, operateOn, receiptNo, quantity);
        }
        return logs;
    }

}

