package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dto.BaseProductMeasureDto;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.SkuVolumeVO;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryRecordServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements IInventoryRecordService {

    @Resource
    private RemoteComponent remoteComponent;

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
        inventoryRecord.setAfterTotalInventory(afterInventory.getTotalInventory());
        inventoryRecord.setAfterAvailableInventory(afterInventory.getAvailableInventory());
        inventoryRecord.setAfterFreezeInventory(afterInventory.getFreezeInventory());
        inventoryRecord.setAfterTotalInbound(afterInventory.getTotalInbound());
        inventoryRecord.setAfterTotalOutbound(afterInventory.getTotalOutbound());
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

    /**
     * 查询入库日志 - 按sku 仓库代码统计sku的体积
     * @param inventorySkuVolumeQueryDTO
     * @return
     */
    @Override
    public List<InventorySkuVolumeVO> selectSkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
        inventorySkuVolumeQueryDTO = Optional.ofNullable(inventorySkuVolumeQueryDTO).orElse(new InventorySkuVolumeQueryDTO());

        List<InventoryRecordVO> inventoryRecordVOS = this.selectList(new InventoryRecordQueryDTO().setSku(inventorySkuVolumeQueryDTO.getSku()).setWarehouseCode(inventorySkuVolumeQueryDTO.getWarehouseCode()).setType("1"));
        if (CollectionUtils.isEmpty(inventoryRecordVOS)) {
            return new ArrayList<>();
        }

        List<String> skuList = inventoryRecordVOS.stream().map(InventoryRecordVO::getSku).collect(Collectors.toList());
        List<BaseProductMeasureDto> skuDataList = remoteComponent.listSku(skuList);
        Map<String, List<BaseProductMeasureDto>> skuData = skuDataList.stream().collect(Collectors.groupingBy(BaseProductMeasureDto::getCode));
        Map<String, List<InventoryRecordVO>> collect = inventoryRecordVOS.stream().collect(Collectors.groupingBy(InventoryRecordVO::getWarehouseCode));

        List<InventorySkuVolumeVO> inventorySkuVolumeVOS = collect.entrySet().stream().map(item -> {
            InventorySkuVolumeVO inventorySkuVolumeVO = new InventorySkuVolumeVO();
            inventorySkuVolumeVO.setWarehouseCode(item.getKey());
            List<SkuVolumeVO> skuVolumeVO = item.getValue().stream().map(skuR -> {
                List<BaseProductMeasureDto> sku = skuData.get(skuR.getSku());
                BigDecimal skuVolume = BigDecimal.ZERO;
                String cusCode = null;
                if (CollectionUtils.isNotEmpty(sku)) {
                    BigDecimal initVolume = sku.get(0).getInitVolume();
                    skuVolume = initVolume == null ? skuVolume : initVolume;
                    cusCode = sku.get(0).getSellerCode();
                }
                BigDecimal multiply = new BigDecimal(skuR.getQuantity()).multiply(skuVolume);
                return new SkuVolumeVO().setSku(skuR.getSku()).setVolume(multiply).setWarehouseNo(skuR.getWarehouseCode()).setOperateOn(skuR.getOperateOn()).setCusCode(cusCode);
            }).collect(Collectors.toList());
            inventorySkuVolumeVO.setSkuVolumes(skuVolumeVO);
            return inventorySkuVolumeVO;
        }).collect(Collectors.toList());
        return inventorySkuVolumeVOS;
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

