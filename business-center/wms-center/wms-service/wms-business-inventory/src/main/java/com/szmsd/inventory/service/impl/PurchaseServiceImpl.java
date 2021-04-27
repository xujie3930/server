package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.config.IBOConvert;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.PurchaseDetails;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import com.szmsd.inventory.enums.PurchaseEnum;
import com.szmsd.inventory.mapper.PurchaseDetailsMapper;
import com.szmsd.inventory.mapper.PurchaseMapper;
import com.szmsd.inventory.service.IPurchaseDetailsService;
import com.szmsd.inventory.service.IPurchaseLogService;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.inventory.service.IPurchaseStorageDetailsService;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.system.api.domain.SysUser;
import jodd.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购单 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Slf4j
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements IPurchaseService {
    @Resource
    private RemoteComponent remoteComponent;
    @Resource
    private SerialNumberClientService serialNumberClientService;
    @Resource
    private IPurchaseLogService iPurchaseLogService;
    @Resource
    private IPurchaseDetailsService iPurchaseDetailsService;
    @Resource
    private IPurchaseStorageDetailsService iPurchaseStorageDetailsService;

    @Override
    public PurchaseInfoVO selectPurchaseByPurchaseNo(String purchaseNo) {
        return baseMapper.selectPurchaseByPurchaseNo(purchaseNo);
    }

    /**
     * 查询采购单模块列表
     *
     * @param
     * @return 采购单模块
     */
    @Override
    public List<PurchaseInfoListVO> selectPurchaseList(PurchaseQueryDTO purchaseQueryDTO) {
        return baseMapper.selectPurchaseList(purchaseQueryDTO);
    }

    @Override
    public List<PurchaseInfoListVO> selectPurchaseListClient(PurchaseQueryDTO purchaseQueryDTO) {
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        purchaseQueryDTO.setCustomCode(loginUserInfo.getSellerCode());
        return selectPurchaseList(purchaseQueryDTO);
    }

    @Override
    public int cancelByWarehouseNo(String warehouseNo) {
        log.info("入库单取消{}，回滚相应的提交入库数量", warehouseNo);
        return 0;
    }

    /**
     * 批量删除采购单模块
     *
     * @param ids 需要删除的采购单模块ID
     * @return 结果
     */
    @Override
    public int deletePurchaseByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurchaseBatch(PurchaseAddDTO purchaseAddDTO) {
        log.info("新增采购单数据 {}", purchaseAddDTO);
        AssertUtil.notNull(purchaseAddDTO, ExceptionMessageEnum.CANNOTBENULL);
        Integer associationId;

        boolean present = Optional.of(purchaseAddDTO).map(PurchaseAddDTO::getPurchaseNo).isPresent();
        if (present) {
//            String purchaseNo = serialNumberClientService.generateNumber("PURCHASE_ORDER");
            String purchaseNo = "CG000000";
            SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
            String customCode = loginUserInfo.getSellerCode();
            purchaseAddDTO.setCustomCode(customCode);
            purchaseAddDTO.setPurchaseNo(purchaseNo);
        }
        //计算采购数量
        purchaseAddDTO.insertHandle();
        Purchase purchase = purchaseAddDTO.convertThis(Purchase.class);
        log.info("采购单新增信息{}", purchase);
        boolean saveBoolean = this.saveOrUpdate(purchase);
        associationId = purchase.getId();

        //插入采购单数据
        List<PurchaseDetailsAddDTO> purchaseDetailsAddList = purchaseAddDTO.getPurchaseDetailsAddList();
        Optional.ofNullable(purchaseDetailsAddList).filter(CollectionUtils::isNotEmpty).ifPresent(
                purchaseDetailList -> {
                    List<PurchaseDetails> entityList = IBOConvert.copyListProperties(purchaseDetailList, PurchaseDetails::new);
                    entityList.forEach(x -> x.setAssociationId(associationId));
                    iPurchaseDetailsService.saveOrUpdateBatch(entityList);
                });
        //添加采购单创建流程
        addLog(associationId, purchaseAddDTO);
        //调用批量入库
        purchaseOrderStorage(purchaseAddDTO);
        return saveBoolean ? 1 : 0;
    }

    private void purchaseOrderStorage(PurchaseAddDTO purchaseAddDTO) {
        log.info("开始批量入库");
        //待入库数据
        List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsAddList = purchaseAddDTO.getPurchaseStorageDetailsAddList();
        List<PurchaseStorageDetailsAddDTO> waitStorage = purchaseStorageDetailsAddList.stream().filter(x -> null != x.getId() && x.getId() > 0).collect(Collectors.toList());
        //封装请求参数
        CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();

        remoteComponent.orderStorage(createInboundReceiptDTO);
        log.info("开始入库完成");
        //添加采购日志

    }

    /**
     * 添加采购单流程
     *
     * @param associationId
     * @param purchaseAddDTO
     */
    private void addLog(Integer associationId, PurchaseAddDTO purchaseAddDTO) {
        if (null != purchaseAddDTO.getId()) {
            return;
        }
        PurchaseLogAddDTO purchaseLogAddDTO = new PurchaseLogAddDTO();
        purchaseLogAddDTO
                .setPurchaseNo(purchaseAddDTO.getPurchaseNo())
                .setType(PurchaseEnum.PURCHASE_ORDER)
                .setAssociationId(associationId)
                .setOrderNo(purchaseAddDTO.getOrderNo());
        log.info("新增采购日志 {}", purchaseLogAddDTO);
        iPurchaseLogService.insertPurchaseLog(purchaseLogAddDTO);
    }
}

