package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseLogAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import com.szmsd.inventory.enums.PurchaseEnum;
import com.szmsd.inventory.mapper.PurchaseMapper;
import com.szmsd.inventory.service.IPurchaseDetailsService;
import com.szmsd.inventory.service.IPurchaseLogService;
import com.szmsd.inventory.service.IPurchaseService;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public PurchaseInfoVO selectPurchaseByPurchaseNo(String id) {
        /*SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        PurchaseInfoDetailVO purchaseInfoDetailVO = new PurchaseInfoDetailVO();
        List<Purchase> purchases = baseMapper.selectList(Wrappers.<Purchase>lambdaQuery()
                .eq(StringUtils.isNotBlank(loginUserInfo.getSellerCode()), Purchase::getCustomCode, loginUserInfo.getSellerCode())
                .eq(Purchase::getPurchaseNo, id)
        );
        List<PurchaseGoodsInfoVO> purchaseInfoVOS = new ArrayList<>(purchases.size());
        purchases.forEach(x -> {
            PurchaseGoodsInfoVO purchaseInfoVO = new PurchaseGoodsInfoVO();
            BeanUtils.copyProperties(x, purchaseInfoVO);
            purchaseInfoVOS.add(purchaseInfoVO);
        });
        purchaseInfoDetailVO.setPurchaseInfoList(purchaseInfoVOS);
        String remark = purchases.stream().map(BaseEntity::getRemark).findAny().orElse("");
        purchaseInfoDetailVO.setRemark(remark);
        String purchaseNo = purchases.stream().map(Purchase::getPurchaseNo).findAny().orElse("");
        purchaseInfoDetailVO.setPurchaseNo(purchaseNo);
        return purchaseInfoDetailVO;*/
        return null;
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
    public int insertPurchaseBatch(PurchaseAddDTO purchaseAddList) {
       /* List<PurchaseAddDTO> purchase = purchaseAddList.getPurchaseInfoAddDTOList();
        if (CollectionUtils.isEmpty(purchase)) {
            return 0;
        }
        log.info("新增采购单数据");
        String purchaseNo = serialNumberClientService.generateNumber("PURCHASE_ORDER");
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        String sellerCode = loginUserInfo.getSellerCode();
        String remark = purchaseAddList.getRemark();
        ArrayList<Purchase> purchasesAdd = new ArrayList<>(purchase.size());
        for (PurchaseAddDTO x : purchase) {
            x.setPurchaseNo(purchaseNo);
            Purchase purchase1 = new Purchase();
            x.setCustomCode(sellerCode);
            BeanUtils.copyProperties(x, purchase1);
            purchase1.setRemark(remark);
            purchasesAdd.add(purchase1);
        }
        boolean b = saveBatch(purchasesAdd);
        //添加日志
        addLog(purchaseNo, purchasesAdd.get(0).getId(), purchaseAddList.getOrderNoList());*/
        return true ? 1 : 0;
    }

    private void addLog(String purchaseNo, Integer assId, List<String> orderNoList) {
        PurchaseLogAddDTO purchaseLogAddDTO = new PurchaseLogAddDTO();
        purchaseLogAddDTO.setPurchaseNo(purchaseNo);
        purchaseLogAddDTO.setType(PurchaseEnum.PURCHASE_ORDER);
        purchaseLogAddDTO.setAssociationId(assId);
        purchaseLogAddDTO.setOrderNoList(orderNoList);
        iPurchaseLogService.insertPurchaseLog(purchaseLogAddDTO);
    }
}

