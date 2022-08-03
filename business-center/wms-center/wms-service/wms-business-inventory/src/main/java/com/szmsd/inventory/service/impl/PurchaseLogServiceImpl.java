package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.component.RemoteRequest;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.PurchaseLog;
import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseLogAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseStorageDetailsAddDTO;
import com.szmsd.inventory.domain.vo.PurchaseLogVO;
import com.szmsd.inventory.enums.PurchaseEnum;
import com.szmsd.inventory.mapper.PurchaseLogMapper;
import com.szmsd.inventory.service.IPurchaseLogService;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购单日志 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Slf4j
@Service
public class PurchaseLogServiceImpl extends ServiceImpl<PurchaseLogMapper, PurchaseLog> implements IPurchaseLogService {
    @Resource
    private RemoteComponent remoteComponent;
    /**
     * 查询采购单日志模块列表
     *
     * @param
     * @return 采购单日志模块
     */
    @Override
    public List<PurchaseLogVO> selectPurchaseLogList(String id) {
        return baseMapper.selectPurchaseLogList(id);
    }

    /**
     * 新增采购单日志模块
     *
     * @param purchaseLog 采购单日志模块
     * @return 结果
     */
    @Override
    public int insertPurchaseLog(PurchaseLogAddDTO purchaseLog) {
        purchaseLog.formatLogDetails();
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        String sellerCode = loginUserInfo.getSellerCode();
        purchaseLog.setCreateByName(sellerCode);
        PurchaseLog purchaseLogAdd = new PurchaseLog();
        BeanUtils.copyProperties(purchaseLog, purchaseLogAdd);
        return baseMapper.insert(purchaseLogAdd);
    }

    /**
     * 取消采购单
     *
     * @param warehouseNo
     * @param rollBackStorage
     * @param associationId
     * @param purchase
     */
    @Override
    public void addLog(String warehouseNo, List<PurchaseStorageDetails> rollBackStorage, Integer associationId, Purchase purchase) {
        StringBuilder stringBuilder = new StringBuilder();
        rollBackStorage.forEach(x -> stringBuilder
                .append(String.format(PurchaseEnum.CANCEL_STORAGE.getTemplate(), x.getDeliveryNo(), x.getDeclareQty())));
        PurchaseLogAddDTO purchaseLogAddDTO = new PurchaseLogAddDTO();
        purchaseLogAddDTO
                .setPurchaseNo(purchase.getPurchaseNo())
                .setType(PurchaseEnum.CANCEL_STORAGE)
                .setWarehouseNo(warehouseNo)
                .setDeliveryNo(stringBuilder.toString())
                .setAssociationId(associationId)
                .setOrderNo(String.join(",", purchase.getOrderNo()));
        log.info("新增采购取消入库日志 {}", purchaseLogAddDTO);
        this.insertPurchaseLog(purchaseLogAddDTO);
    }

    /**
     * 添加采购->入库日志
     *
     * @param associationId
     * @param purchaseAddDTO
     * @param warehouseNo
     */
    @Override
    public void addLog(Integer associationId, PurchaseAddDTO purchaseAddDTO, String warehouseNo) {
        PurchaseLogAddDTO purchaseLogAddDTO = new PurchaseLogAddDTO();
        List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsList = purchaseAddDTO.getPurchaseStorageDetailsAddList();
        List<String> collect = purchaseStorageDetailsList.stream().map(PurchaseStorageDetailsAddDTO::getDeliveryNo).collect(Collectors.toList());
        purchaseLogAddDTO
                .setPurchaseNo(purchaseAddDTO.getPurchaseNo())
                .setType(PurchaseEnum.WAREHOUSING_LIST)
                .setWarehouseNo(warehouseNo)
                .setDeliveryNo(String.join(",", collect))
                .setAssociationId(associationId)
                .setOrderNo(String.join(",", purchaseAddDTO.getOrderNo()));
        log.info("新增采购入库日志 {}", purchaseLogAddDTO);
        this.insertPurchaseLog(purchaseLogAddDTO);
    }

    /**
     * 添加采购单流程
     *
     * @param associationId
     * @param purchaseAddDTO
     */
    @Override
    public void addLog(Integer associationId, PurchaseAddDTO purchaseAddDTO) {
        if (null != purchaseAddDTO.getId()) {
            return;
        }
        PurchaseLogAddDTO purchaseLogAddDTO = new PurchaseLogAddDTO();
        purchaseLogAddDTO
                .setPurchaseNo(purchaseAddDTO.getPurchaseNo())
                .setType(PurchaseEnum.PURCHASE_ORDER)
                .setAssociationId(associationId)
                .setOrderNo(String.join(",", purchaseAddDTO.getOrderNo()));
        log.info("新增采购日志 {}", purchaseLogAddDTO);
        this.insertPurchaseLog(purchaseLogAddDTO);
    }
}

