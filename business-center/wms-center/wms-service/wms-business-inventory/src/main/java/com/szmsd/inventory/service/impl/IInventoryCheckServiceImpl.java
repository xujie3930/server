package com.szmsd.inventory.service.impl;

import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.api.feign.HtpInventoryCheckFeignService;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;
import com.szmsd.inventory.enums.InventoryStatusEnum;
import com.szmsd.inventory.mapper.InventoryCheckMapper;
import com.szmsd.inventory.service.IInventoryCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class IInventoryCheckServiceImpl implements IInventoryCheckService {

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;

    @Resource
    private HtpInventoryCheckFeignService htpInventoryCheckFeignService;

    @Resource
    private SerialNumberClientService serialNumberClientService;

    @Transactional
    @Override
    public int add(InventoryCheckDTO inventoryCheckDTO) {
        InventoryCheck inventoryCheck = BeanMapperUtil.map(inventoryCheckDTO, InventoryCheck.class);
        // 流水号规则：PD + 客户代码 + （年月日 + 5位流水）
        inventoryCheck.setOrderNo("PD" + inventoryCheckDTO.getCustomCode() + this.serialNumberClientService.generateNumber("INVENTORY_CHECK"));
        return inventoryCheckMapper.insert(inventoryCheck);
    }

    @Override
    public List<InventoryCheck> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO) {
        return inventoryCheckMapper.findList(inventoryCheckQueryDTO);
    }

    @Override
    public InventoryCheck details(int id) {
        return inventoryCheckMapper.selectById(id);
    }

    @Transactional
    @Override
    public int update(InventoryCheck inventoryCheck) {
        if (!InventoryStatusEnum.checkStatus(inventoryCheck.getStatus())) {
            throw new CommonException("999", "请检查单据审核状态");
        }
        InventoryCheck checkStatus = inventoryCheckMapper.selectById(inventoryCheck.getId());
        if(checkStatus != null && checkStatus.getStatus() == 1) {
            throw new CommonException("999", "该单据已审核通过，请勿重复提交");
        }
        int result = inventoryCheckMapper.updateById(inventoryCheck);
        if (InventoryStatusEnum.PASS.getCode() == inventoryCheck.getStatus()) {
            CountingRequest countingRequest = new CountingRequest(inventoryCheck.getWarehouseCode(),
                    inventoryCheck.getOrderNo(), inventoryCheck.getRemark(), Collections.singletonList(inventoryCheck.getSku()));
            R<ResponseVO> response = htpInventoryCheckFeignService.counting(countingRequest);
            if (response.getCode() != 200 || !response.getData().getSuccess()) {
                log.error("调用WMS创建盘点单失败: {}", response.toString());
                throw new CommonException("999", "调用WMS创建盘点单失败");
            }
        }
        return result;
    }

}
