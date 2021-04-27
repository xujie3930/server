package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.config.IBOConvert;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.PurchaseDetails;
import com.szmsd.inventory.domain.PurchaseStorageDetails;
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
import com.szmsd.putinstorage.domain.dto.InboundReceiptDetailDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.system.api.domain.SysUser;
import io.swagger.models.auth.In;
import jodd.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
        //取消改批次的单 回滚数量
        List<PurchaseStorageDetails> rollBackStorage = iPurchaseStorageDetailsService.list(Wrappers.<PurchaseStorageDetails>lambdaQuery()
                .eq(PurchaseStorageDetails::getWarehousingNo, warehouseNo)
        );
        //更新对应sku的数量 总数量

        List<String> skuList = rollBackStorage.stream().map(PurchaseStorageDetails::getSku).collect(Collectors.toList());
        //商品详情
        List<PurchaseDetails> detailsList = iPurchaseDetailsService.list(Wrappers.<PurchaseDetails>lambdaQuery().in(PurchaseDetails::getSku, skuList));
        //计算需要回滚的数量
        Map<String, List<PurchaseStorageDetails>> collect1 = rollBackStorage.stream().collect(Collectors.groupingBy(PurchaseStorageDetails::getSku));

        collect1.forEach((o1, o2) -> {

        });


        iPurchaseStorageDetailsService.update(Wrappers.<PurchaseStorageDetails>lambdaUpdate()
                .set(PurchaseStorageDetails::getDelFlag, 2)
                .eq(PurchaseStorageDetails::getWarehousingNo, warehouseNo)
        );

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
            String purchaseNo = serialNumberClientService.generateNumber("PURCHASE_ORDER");
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
        purchaseOrderStorage(associationId, purchaseAddDTO);
        return saveBoolean ? 1 : 0;
    }

    private void purchaseOrderStorage(Integer associationId, PurchaseAddDTO purchaseAddDTO) {
        log.info("开始批量采购入库--");
        List<PurchaseDetailsAddDTO> purchaseDetailsAddList = purchaseAddDTO.getPurchaseDetailsAddList();

        //待入库数据
        List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsAddList = purchaseAddDTO.getPurchaseStorageDetailsAddList();
        List<PurchaseStorageDetailsAddDTO> waitStorage = purchaseStorageDetailsAddList.stream().filter(x -> !(null != x.getId() && x.getId() > 0)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(waitStorage)) {
            log.info("终止批量采购入库,待入库的数据为空");
        }
        //组合数据 快递单号：该单号下的数据
        Map<String, List<PurchaseStorageDetailsAddDTO>> collect = waitStorage.stream().collect(Collectors.groupingBy(PurchaseStorageDetailsAddDTO::getDeliveryNo));
        collect.forEach((no, addList) -> {
            if (CollectionUtils.isEmpty(addList)) {
                log.info("该{}快递单号下,无sku数据", no);
                return;
            }
            // 叠加相同sku数据
            addList = merge(addList);
            int sum = addList.stream().mapToInt(PurchaseStorageDetailsAddDTO::getDeclareQty).sum();
            // 封装请求参数
            CreateInboundReceiptDTO createInboundReceiptDTO = new CreateInboundReceiptDTO();
            createInboundReceiptDTO
                    .setDeliveryNo(no)
                    .setOrderNo(purchaseAddDTO.getPurchaseNo())
                    .setCusCode(purchaseAddDTO.getCustomCode())
                    .setVat(purchaseAddDTO.getVat())
                    .setWarehouseCode(purchaseAddDTO.getWarehouseCode())
                    .setOrderType(purchaseAddDTO.getOrderType())
                    .setWarehouseCategoryCode(purchaseAddDTO.getWarehouseCategoryCode())
                    .setDeliveryWayCode(purchaseAddDTO.getDeliveryWay())
                    .setTotalDeclareQty(sum)
                    .setTotalPutQty(0)
                    .setRemark(purchaseAddDTO.getRemark())
            ;
            //设置SKU列表数据
            ArrayList<InboundReceiptDetailDTO> inboundReceiptDetailAddList = new ArrayList<>();
            addList.forEach(addSku -> {
                InboundReceiptDetailDTO inboundReceiptDetailDTO = new InboundReceiptDetailDTO();
                inboundReceiptDetailDTO
                        .setDeclareQty(addSku.getDeclareQty())
                        .setSku(addSku.getSku())
                        .setSkuName(addSku.getSku())
                ;
                inboundReceiptDetailAddList.add(inboundReceiptDetailDTO);
            });
            createInboundReceiptDTO.setInboundReceiptDetails(inboundReceiptDetailAddList);

            InboundReceiptInfoVO inboundReceiptInfoVO = remoteComponent.orderStorage(createInboundReceiptDTO);
            String warehouseNo = inboundReceiptInfoVO.getWarehouseNo();
            //插入采购入库数据 + 日志
            Optional.of(addList).filter(CollectionUtils::isNotEmpty).ifPresent(
                    purchaseStorageDetailsList -> {
                        List<PurchaseStorageDetails> entityList = IBOConvert.copyListProperties(purchaseStorageDetailsList, PurchaseStorageDetails::new);
                        entityList.forEach(x -> {
                            x.setAssociationId(associationId);
                            x.setWarehousingNo(warehouseNo);
                        });
                        iPurchaseStorageDetailsService.saveOrUpdateBatch(entityList);
                        //添加采购日志

                    });
        });

        log.info("开始入库完成");


    }

    /**
     * 将id进行合并nums, sums 相加道回合并后的集合使用Java8的流进行处理
     */
    public static List<PurchaseStorageDetailsAddDTO> merge(List<PurchaseStorageDetailsAddDTO> list) {
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(PurchaseStorageDetailsAddDTO::getSku, a -> a, (o1, o2) -> {
                    o1.setDeclareQty(o1.getDeclareQty() + o2.getDeclareQty());
                    return o1;
                })).values());
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

    @Override
    public int transportWarehousingSubmit(TransportWarehousingAddDTO transportWarehousingAddDTO) {
        return 0;
    }
}

