package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.AddSkuInspectionRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryInspection;
import com.szmsd.inventory.domain.InventoryInspectionDetails;
import com.szmsd.inventory.domain.dto.InboundInventoryInspectionDTO;
import com.szmsd.inventory.domain.dto.InventoryInspectionDTO;
import com.szmsd.inventory.domain.dto.InventoryInspectionDetailsDTO;
import com.szmsd.inventory.domain.dto.InventoryInspectionQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryInspectionVo;
import com.szmsd.inventory.enums.InventoryStatusEnum;
import com.szmsd.inventory.mapper.InventoryInspectionMapper;
import com.szmsd.inventory.service.IInventoryInspectionDetailsService;
import com.szmsd.inventory.service.IInventoryInspectionService;
import com.szmsd.inventory.service.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

    @Resource
    private BasSellerFeignService basSellerFeignService;

    @Resource
    private IInventoryService inventoryService;

    @Transactional
    @Override
    public boolean add(List<InventoryInspectionDetailsDTO> dto) {
        Map<String, List<InventoryInspectionDetailsDTO>> collect = dto.stream().collect(Collectors.groupingBy(InventoryInspectionDetailsDTO::getWarehouseCode));
        collect.forEach((key, value) -> {
            InventoryInspection inventoryInspection = new InventoryInspection();
            String customCode = dto.get(0).getCustomCode();
            inventoryInspection.setCustomCode(customCode);
            inventoryInspection.setWarehouseCode(key);
            String inspectionNo = getInspectionNo(customCode);
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
        map.setAuditTime(new Date());
        LambdaQueryWrapper<InventoryInspectionDetails> query = Wrappers.lambdaQuery();
        query.eq(InventoryInspectionDetails::getInspectionNo, dto.getInspectionNo());
        List<InventoryInspectionDetails> list = inventoryInspectionDetailsService.list(query);
        int result = iInventoryInspectionMapper.updateById(map);
        if (1 == map.getStatus()) {
            List<String> skuList = list.stream().map(InventoryInspectionDetails::getSku).collect(Collectors.toList());
            AddSkuInspectionRequest addSkuInspectionRequest = new AddSkuInspectionRequest(map.getWarehouseCode(), skuList);
            R<ResponseVO> response = htpBasFeignService.inspection(addSkuInspectionRequest);
            if (response.getCode() != 200) {
                map.setErrorCode(response.getCode());
                map.setErrorMessage(response.getMsg());
                iInventoryInspectionMapper.updateById(map);
            }
        }
        return result;
    }

    /**
     * 入库验货
     *
     * @param dto dto
     * @return result
     */
    @Transactional
    @Override
    public boolean inboundInventory(InboundInventoryInspectionDTO dto) {
        R<String> result = basSellerFeignService.getInspection(dto.getCusCode());
        if (result.getCode() != 200) {
            log.error("inboundInventory() code:{} message: {}", result.getMsg(), result.getMsg());
            return false;
        }
        String data = result.getData();
        if (data == null)  return true;

        if (data.equals("不验货")) return true;

        if (data.equals("新SKU必验")) {
            List<String> skus = dto.getSkus();
            List<String> skuList = new ArrayList<>();
            for (String sku : skus) {
                LambdaQueryWrapper<Inventory> query = Wrappers.lambdaQuery();
                query.eq(Inventory::getSku, sku);
                int count = inventoryService.count(query);
                if (count == 0) { // 入库的SKU在库存表里不存在为新SKU
                    skuList.add(sku);
                }
            }
            if (skuList.size() > 0)
                return createInventory(new InboundInventoryInspectionDTO(dto.getCusCode(), dto.getWarehouseNo(), dto.getWarehouseCode(), skuList));

            return true;
        }
        // 入库必验
        return createInventory(dto);
    }

    /**
     * 入库创建无需审批验货单
     *
     * @param dto dto
     * @return result
     */
    private boolean createInventory(InboundInventoryInspectionDTO dto) {
        InventoryInspection inventoryInspection = new InventoryInspection();
        String inspectionNo = setInventoryInspection(dto, inventoryInspection);

        List<InventoryInspectionDetailsDTO> inventoryInspectionDetailsDTO = dto.getSkus().stream()
                .map(sku -> new InventoryInspectionDetailsDTO(sku, dto.getWarehouseCode(), dto.getCusCode())).collect(Collectors.toList());

        this.saveDetails(inventoryInspectionDetailsDTO, inspectionNo);
        AddSkuInspectionRequest addSkuInspectionRequest = new AddSkuInspectionRequest(dto.getWarehouseCode(), dto.getWarehouseNo(), dto.getSkus());
        R<ResponseVO> response = htpBasFeignService.inspection(addSkuInspectionRequest);
        if (response.getCode() != 200) {
            inventoryInspection.setErrorCode(response.getCode());
            inventoryInspection.setErrorMessage(response.getMsg());
        }
        inventoryInspection.setStatus(3); //3为入库创建的验货单
        return iInventoryInspectionMapper.insert(inventoryInspection) > 0;
    }

    private String setInventoryInspection(InboundInventoryInspectionDTO dto, InventoryInspection inventoryInspection) {
        String operator = dto.getCusCode();
        String inspectionNo = getInspectionNo(operator);
        inventoryInspection.setInspectionNo(inspectionNo);
        inventoryInspection.setCustomCode(operator);
        inventoryInspection.setWarehouseCode(dto.getWarehouseCode());
        return inspectionNo;
    }

    /**
     * 流水号规则：YH + 客户代码 + （年月日 + 5位流水）
     *
     * @param operator 操作人
     * @return inspectionNo
     */
    private String getInspectionNo(String operator) {
        return "YH" + operator + serialNumberClientService.generateNumber("INVENTORY_INSPECTION");
    }

}
