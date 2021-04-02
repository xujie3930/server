package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.http.api.feign.HtpInventoryCheckFeignService;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.InventoryCheckDetails;
import com.szmsd.inventory.domain.dto.InventoryCheckDetailsDTO;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryCheckVo;
import com.szmsd.inventory.enums.InventoryStatusEnum;
import com.szmsd.inventory.mapper.InventoryCheckMapper;
import com.szmsd.inventory.service.IInventoryCheckDetailsService;
import com.szmsd.inventory.service.IInventoryCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IInventoryCheckServiceImpl extends ServiceImpl<InventoryCheckMapper, InventoryCheck> implements IInventoryCheckService {

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;

    @Resource
    private IInventoryCheckDetailsService inventoryCheckDetailsService;

    @Resource
    private HtpInventoryCheckFeignService htpInventoryCheckFeignService;

    @Resource
    private SerialNumberClientService serialNumberClientService;

    @Transactional
    @Override
    public int add(List<InventoryCheckDetailsDTO> inventoryCheckDetailsList) {
        Map<String, List<InventoryCheckDetailsDTO>> collect = inventoryCheckDetailsList.stream().collect(Collectors.groupingBy(InventoryCheckDetailsDTO::getWarehouseCode));
        collect.forEach((key, value) -> {
            InventoryCheck inventoryCheck = new InventoryCheck();
            String customCode = inventoryCheckDetailsList.get(0).getCustomCode();
            inventoryCheck.setCustomCode(customCode);
            inventoryCheck.setWarehouseCode(key);
            // 流水号规则：PD + 客户代码 + （年月日 + 5位流水）
            String orderNo = "PD" + customCode + serialNumberClientService.generateNumber("INVENTORY_CHECK");
            inventoryCheck.setOrderNo(orderNo);
            saveDetails(value, orderNo);
            inventoryCheckMapper.insert(inventoryCheck);
        });
        return 1;
    }

    private void saveDetails(List<InventoryCheckDetailsDTO> value, String orderNo) {
        List<InventoryCheckDetails> inventoryCheckDetails = BeanMapperUtil.mapList(value, InventoryCheckDetails.class);
        BeanUtils.copyProperties(value, inventoryCheckDetails);
        for (InventoryCheckDetails inventoryCheckDetail : inventoryCheckDetails) {
            inventoryCheckDetail.setOrderNo(orderNo);
        }
        inventoryCheckDetailsService.saveBatch(inventoryCheckDetails);
    }

    @Override
    public List<InventoryCheckVo> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO) {
        return inventoryCheckMapper.findList(inventoryCheckQueryDTO);
    }

    @Override
    public InventoryCheckVo details(int id) {
        return inventoryCheckMapper.findDetails(id);
    }

    @Transactional
    @Override
    public int update(InventoryCheck inventoryCheck) {
        if (!InventoryStatusEnum.checkStatus(inventoryCheck.getStatus())) {
            throw new CommonException("999", "请检查单据审核状态");
        }
        InventoryCheck checkStatus = inventoryCheckMapper.selectById(inventoryCheck.getId());
        if (checkStatus.getStatus() == 1) {
            throw new CommonException("999", "该单据已审核通过，请勿重复提交");
        }
        LambdaQueryWrapper<InventoryCheckDetails> query = Wrappers.lambdaQuery();
        query.eq(InventoryCheckDetails::getOrderNo, checkStatus.getOrderNo());
        List<InventoryCheckDetails> list = inventoryCheckDetailsService.list(query);
        int result = inventoryCheckMapper.updateById(inventoryCheck);
        if (InventoryStatusEnum.PASS.getCode() == inventoryCheck.getStatus()) {
            List<String> skuList = list.stream().map(InventoryCheckDetails::getSku).collect(Collectors.toList());
            CountingRequest countingRequest = new CountingRequest(inventoryCheck.getWarehouseCode(),
                    inventoryCheck.getOrderNo(), inventoryCheck.getRemark(), skuList);
            R<ResponseVO> response = htpInventoryCheckFeignService.counting(countingRequest);
            if (response.getCode() != 200 || !response.getData().getSuccess()) {
                log.error("调用WMS创建盘点单失败: {}", response.toString());
                throw new CommonException("999", "调用WMS创建盘点单失败");
            }
        }
        return result;
    }

}
