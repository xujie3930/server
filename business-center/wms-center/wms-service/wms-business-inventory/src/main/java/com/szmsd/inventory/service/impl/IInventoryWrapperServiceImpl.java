package com.szmsd.inventory.service.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.IInventoryWrapperService;
import com.szmsd.inventory.service.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;
import java.util.function.BiFunction;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 19:21
 */
@Service
public class IInventoryWrapperServiceImpl implements IInventoryWrapperService {
    private final Logger logger = LoggerFactory.getLogger(IInventoryWrapperServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IInventoryService inventoryService;

    /**
     * 冻结库存
     * 可用库存 = 可用库存 - num
     * 冻结库存 = 冻结库存 + num
     *
     * @param operateListDto operateListDto
     * @return int
     */
    @Override
    public int freeze(InventoryOperateListDto operateListDto) {
        // 成功：冻结所有的SKU
        // 失败：回滚冻结过的SKU
        return this.doWorker(operateListDto,
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "freeze"),
                        () -> this.inventoryService.freeze(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getFreeType(), operateListDto.getCusCode())),
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "unFreeze"),
                        () -> inventoryService.unFreeze(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getFreeType(), operateListDto.getCusCode())));
    }

    /**
     * 取消冻结库存
     * 可用库存 = 可用库存 + num
     * 冻结库存 = 冻结库存 - num
     *
     * @param operateListDto operateListDto
     * @return int
     */
    @Override
    public int unFreeze(InventoryOperateListDto operateListDto) {
        // 成功：取消冻结所有的SKU
        // 失败：回滚取消冻结过的SKU
        return this.doWorker(operateListDto,
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "unFreeze"),
                        () -> this.inventoryService.unFreeze(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getFreeType(), operateListDto.getCusCode())),
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "freeze"),
                        () -> inventoryService.freeze(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getFreeType(), operateListDto.getCusCode())));
    }

    @Override
    public int unFreezeAndFreeze(InventoryOperateListDto operateListDto) {
        if (CollectionUtils.isNotEmpty(operateListDto.getUnOperateList())) {
            InventoryOperateListDto unFreezeDto = new InventoryOperateListDto();
            unFreezeDto.setInvoiceNo(operateListDto.getInvoiceNo());
            unFreezeDto.setWarehouseCode(operateListDto.getWarehouseCode());
            unFreezeDto.setOperateList(operateListDto.getUnOperateList());
            this.unFreeze(unFreezeDto);
        }
        if (CollectionUtils.isNotEmpty(operateListDto.getOperateList())) {
            this.freeze(operateListDto);
        }
        return 0;
    }

    /**
     * 扣减库存
     * 总库存 = 总库存 - num
     * 冻结库存 = 冻结库存 - num
     * 总出库 = 总出库 + num
     *
     * @param operateListDto operateListDto
     * @return int
     */
    @Override
    public int deduction(InventoryOperateListDto operateListDto) {
        return this.doWorker(operateListDto,
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "deduction"),
                        () -> this.inventoryService.deduction(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getCusCode())),
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "unDeduction"),
                        () -> inventoryService.unDeduction(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getCusCode())));
    }

    /**
     * 取消扣减库存
     * 总库存 = 总库存 + num
     * 冻结库存 = 冻结库存 + num
     * 总出库 = 总出库 - num
     *
     * @param operateListDto operateListDto
     * @return int
     */
    @Override
    public int unDeduction(InventoryOperateListDto operateListDto) {
        return this.doWorker(operateListDto,
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "unDeduction"),
                        () -> this.inventoryService.unDeduction(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getCusCode())),
                (dto, listDto) -> new LockerUtil<Integer>(redissonClient).doExecute(this.builderOnlyKey(listDto.getWarehouseCode(), dto.getSku(), "deduction"),
                        () -> inventoryService.deduction(listDto.getInvoiceNo(), listDto.getWarehouseCode(), dto.getSku(), dto.getNum(), operateListDto.getCusCode())));
    }

    @Override
    public int unDeductionAndDeduction(InventoryOperateListDto operateListDto) {
        if (CollectionUtils.isNotEmpty(operateListDto.getUnOperateList())) {
            InventoryOperateListDto unFreezeDto = new InventoryOperateListDto();
            unFreezeDto.setInvoiceNo(operateListDto.getInvoiceNo());
            unFreezeDto.setWarehouseCode(operateListDto.getWarehouseCode());
            unFreezeDto.setOperateList(operateListDto.getUnOperateList());
            this.unDeduction(unFreezeDto);
        }
        if (CollectionUtils.isNotEmpty(operateListDto.getOperateList())) {
            this.deduction(operateListDto);
        }
        return 0;
    }

    private int doWorker(InventoryOperateListDto operateListDto,
                         BiFunction<InventoryOperateDto, InventoryOperateListDto, Integer> consumer,
                         BiFunction<InventoryOperateDto, InventoryOperateListDto, Integer> rollbackConsumer) {
        String invoiceNo = operateListDto.getInvoiceNo();
        String warehouseCode = operateListDto.getWarehouseCode();
        List<InventoryOperateDto> freezeList = operateListDto.getOperateList();
        if (StringUtils.isEmpty(invoiceNo)
                || StringUtils.isEmpty(warehouseCode)
                || CollectionUtils.isEmpty(freezeList)) {
            throw new CommonException("999", "参数不全");
        }
        Stack<InventoryOperateDto> stack = new Stack<>();
        int count = 0;
        try {
            for (InventoryOperateDto dto : freezeList) {
                try {
                    if (consumer.apply(dto, operateListDto) > 0) {
                        // 操作成功++
                        count++;
                        // 操作成功的记录起来，当其中一个失败的时候，做回滚操作
                        stack.push(dto);
                    } else {
                        throw new CommonException("999", "[" + dto.getSku() + "]库存操作失败");
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    // 异常up
                    throw e;
                }
            }
            return count;
        } catch (Exception e) {
            // 开始回滚
            while (!stack.empty()) {
                rollbackConsumer.apply(stack.pop(), operateListDto);
            }
            // up
            throw e;
        }
    }

    private String builderOnlyKey(String warehouseCode, String sky, String method) {
        return applicationName + ":" + method + ":" + warehouseCode + "_" + sky;
    }
}
