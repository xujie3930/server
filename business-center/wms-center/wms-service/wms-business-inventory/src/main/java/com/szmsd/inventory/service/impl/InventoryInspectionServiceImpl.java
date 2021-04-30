package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.AddSkuInspectionRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.domain.InventoryInspection;
import com.szmsd.inventory.domain.InventoryInspectionDetails;
import com.szmsd.inventory.domain.dto.InventoryInspectionDTO;
import com.szmsd.inventory.domain.dto.InventoryInspectionDetailsDTO;
import com.szmsd.inventory.domain.dto.InventoryInspectionQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryInspectionVo;
import com.szmsd.inventory.enums.InventoryStatusEnum;
import com.szmsd.inventory.mapper.InventoryInspectionMapper;
import com.szmsd.inventory.service.IInventoryInspectionDetailsService;
import com.szmsd.inventory.service.IInventoryInspectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryInspectionServiceImpl extends ServiceImpl<InventoryInspectionMapper, InventoryInspection> implements IInventoryInspectionService {

    @Resource
    private InventoryInspectionMapper iInventoryInspectionMapper;

    @Resource
    private SerialNumberClientService serialNumberClientService;

    @Resource
    private IInventoryInspectionDetailsService inventoryInspectionDetailsService;

    @Resource
    private HtpBasFeignService htpBasFeignService;

    @Transactional
    @Override
    public boolean add(List<InventoryInspectionDetailsDTO> dto) {
        Map<String, List<InventoryInspectionDetailsDTO>> collect = dto.stream().collect(Collectors.groupingBy(InventoryInspectionDetailsDTO::getWarehouseCode));
        collect.forEach((key, value) -> {
            InventoryInspection inventoryInspection = new InventoryInspection();
            String customCode = dto.get(0).getCustomCode();
            inventoryInspection.setCustomCode(customCode);
            inventoryInspection.setWarehouseCode(key);
            // 流水号规则：PD + 客户代码 + （年月日 + 5位流水）
            String inspectionNo = "PD" + customCode + serialNumberClientService.generateNumber("INVENTORY_CHECK");
            inventoryInspection.setInspectionNo(inspectionNo);
            this.saveDetails(value, inspectionNo);
            iInventoryInspectionMapper.insert(inventoryInspection);
        });
        return true;
    }

    /**
     * 保存详情
     *
     * @param value        value
     * @param inspectionNo 单号
     */
    private void saveDetails(List<InventoryInspectionDetailsDTO> value, String inspectionNo) {
        List<InventoryInspectionDetails> inventoryCheckDetails = BeanMapperUtil.mapList(value, InventoryInspectionDetails.class);
        BeanUtils.copyProperties(value, inventoryCheckDetails);
        for (InventoryInspectionDetails inventoryInspectionDetails : inventoryCheckDetails) {
            inventoryInspectionDetails.setInspectionNo(inspectionNo);
        }
        inventoryInspectionDetailsService.saveBatch(inventoryCheckDetails);
    }

    @Override
    public List<InventoryInspectionVo> findList(InventoryInspectionQueryDTO dto) {
        return iInventoryInspectionMapper.selectListPage(dto);
    }

    @Override
    public InventoryInspectionVo details(String inspectionNo) {
        return iInventoryInspectionMapper.selectDetails(inspectionNo);
    }

    @Transactional
    @Override
    public int audit(InventoryInspectionDTO dto) {
        if (!InventoryStatusEnum.checkStatus(dto.getStatus())) {
            throw new CommonException("999", "请检查单据审核状态");
        }
        InventoryInspection inventoryInspection = iInventoryInspectionMapper.selectById(dto.getId());
        if (inventoryInspection.getStatus() == 1) {
            throw new CommonException("999", "该单据已审核通过，请勿重复提交");
        }
        InventoryInspection map = BeanMapperUtil.map(dto, InventoryInspection.class);
        LambdaQueryWrapper<InventoryInspectionDetails> query = Wrappers.lambdaQuery();
        query.eq(InventoryInspectionDetails::getInspectionNo, dto.getInspectionNo());
        List<InventoryInspectionDetails> list = inventoryInspectionDetailsService.list(query);
        int result = iInventoryInspectionMapper.updateById(map);
        if (1 == map.getStatus()) {
            List<String> skuList = list.stream().map(InventoryInspectionDetails::getSku).collect(Collectors.toList());
            AddSkuInspectionRequest addSkuInspectionRequest = new AddSkuInspectionRequest(map.getWarehouseCode(), skuList);
            R<ResponseVO> response = htpBasFeignService.inspection(addSkuInspectionRequest);
            if (response.getCode() != 200 || !response.getData().getSuccess()) {
                map.setErrorCode(response.getCode());
                map.setErrorMessage(response.getMsg());
                iInventoryInspectionMapper.updateById(map);
            }
        }
        return result;
    }
}
