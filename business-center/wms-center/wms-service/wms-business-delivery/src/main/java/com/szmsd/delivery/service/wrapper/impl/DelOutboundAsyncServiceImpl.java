package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.bas.api.service.BasePackingClientService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.http.enums.HttpRechargeConstants;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:29
 */
@Service
public class DelOutboundAsyncServiceImpl implements IDelOutboundAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private InventoryFeignClientService inventoryFeignClientService;
    @Autowired
    private RechargesFeignService rechargesFeignService;
    @Autowired
    private IDelOutboundChargeService delOutboundChargeService;
    @Autowired
    private OperationFeignService operationFeignService;
    @Autowired
    private BasePackingClientService basePackingClientService;

    @Transactional
    @Override
    public int shipmentPacking(Long id) {
        // 获取新的出库单信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        // 只处理状态为【WHSE_PROCESSING】的记录
        if (!DelOutboundStateEnum.WHSE_PROCESSING.getCode().equals(delOutbound.getState())) {
            return 0;
        }
        ApplicationContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        ShipmentEnum currentState;
        String shipmentState = delOutbound.getShipmentState();
        if (StringUtils.isEmpty(shipmentState)) {
            currentState = ShipmentEnum.BEGIN;
        } else {
            currentState = ShipmentEnum.get(shipmentState);
        }
        ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, ShipmentEnum.END, ShipmentEnum.BEGIN);
        applicationContainer.action();
        return 1;
    }

    @Override
    public void completed(String orderNo) {
        // 处理阶段
        // 1.扣减库存              DE
        // 2.1扣减费用             FEE_DE
        // 2.2扣减操作费用         OP_FEE_DE
        // 2.3扣减物料费           PM_FEE_DE
        // 3.更新状态为已完成       MODIFY
        // 4.完成                  END
        DelOutbound delOutbound = this.delOutboundService.getByOrderNo(orderNo);
        if (null == delOutbound) {
            return;
        }
        // 订单完成
        if (DelOutboundStateEnum.COMPLETED.getCode().equals(delOutbound.getState())) {
            return;
        }
        // 获取到处理状态
        String completedState = delOutbound.getCompletedState();
        try {
            // 空值默认处理
            if (StringUtils.isEmpty(completedState)) {
                // 执行扣减库存
                this.deduction(orderNo, delOutbound.getWarehouseCode(), delOutbound.getOrderType());
                completedState = "FEE_DE";
            }
            // 销毁，自提，新SKU不扣物流费用
            boolean fee = true;
            if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
                fee = false;
            }
            if ("FEE_DE".equals(completedState)) {
                if (fee) {
                    // 扣减费用
                    CustPayDTO custPayDTO = new CustPayDTO();
                    custPayDTO.setCusCode(delOutbound.getSellerCode());
                    custPayDTO.setCurrencyCode(delOutbound.getCurrencyCode());
                    custPayDTO.setAmount(delOutbound.getAmount());
                    custPayDTO.setNo(delOutbound.getOrderNo());
                    custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
                    // 查询费用明细
                    List<DelOutboundCharge> chargeList = this.delOutboundChargeService.listCharges(orderNo);
                    if (CollectionUtils.isNotEmpty(chargeList)) {
                        List<AccountSerialBillDTO> serialBillInfoList = new ArrayList<>(chargeList.size());
                        for (DelOutboundCharge charge : chargeList) {
                            AccountSerialBillDTO serialBill = new AccountSerialBillDTO();
                            serialBill.setNo(orderNo);
                            serialBill.setTrackingNo(delOutbound.getTrackingNo());
                            serialBill.setCusCode(delOutbound.getSellerCode());
                            serialBill.setCurrencyCode(charge.getCurrencyCode());
                            serialBill.setAmount(charge.getAmount());
                            serialBill.setWarehouseCode(delOutbound.getWarehouseCode());
                            serialBill.setChargeCategory(charge.getChargeNameCn());
                            serialBill.setChargeType(charge.getChargeNameCn());
                            serialBill.setOrderTime(delOutbound.getCreateTime());
                            serialBill.setPaymentTime(delOutbound.getShipmentsTime());
                            serialBill.setProductCode(delOutbound.getShipmentRule());
                            serialBillInfoList.add(serialBill);
                        }
                        custPayDTO.setSerialBillInfoList(serialBillInfoList);
                    }
                    custPayDTO.setOrderType("Freight");
                    R<?> r = this.rechargesFeignService.feeDeductions(custPayDTO);
                    if (null == r || Constants.SUCCESS != r.getCode()) {
                        throw new CommonException("999", "扣减费用失败");
                    }
                }
                completedState = "OP_FEE_DE";
            }
            if ("OP_FEE_DE".equals(completedState)) {
                DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
                delOutboundOperationVO.setOrderType(delOutbound.getOrderType());
                delOutboundOperationVO.setOrderNo(orderNo);
                R<?> r = this.operationFeignService.delOutboundCharge(delOutboundOperationVO);
                DelOutboundServiceImplUtil.chargeOperationThrowCommonException(r);
                completedState = "PM_FEE_DE";
            }
            if ("PM_FEE_DE".equalsIgnoreCase(completedState)) {
                // 根据出库单上的包材类型进行扣去物料费。
                String packingMaterial = delOutbound.getPackingMaterial();
                if (StringUtils.isNotEmpty(packingMaterial)) {
                    BasePackingConditionQueryDto conditionQueryDto = new BasePackingConditionQueryDto();
                    conditionQueryDto.setCode(packingMaterial);
                    // 查询包材信息
                    BasePacking basePacking = this.basePackingClientService.queryByCode(conditionQueryDto);
                    if (null != basePacking && null != basePacking.getPrice()) {
                        CustPayDTO custPayDTO = new CustPayDTO();
                        custPayDTO.setCusCode(delOutbound.getSellerCode());
                        custPayDTO.setPayType(BillEnum.PayType.PAYMENT_NO_FREEZE);
                        custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
                        custPayDTO.setCurrencyCode(HttpRechargeConstants.RechargeCurrencyCode.CNY.name());
                        custPayDTO.setAmount(BigDecimal.valueOf(basePacking.getPrice()));
                        custPayDTO.setNo(delOutbound.getOrderNo());
                        custPayDTO.setOrderType(delOutbound.getOrderType());
                        List<AccountSerialBillDTO> list = new ArrayList<>();
                        AccountSerialBillDTO dto = new AccountSerialBillDTO();
                        dto.setCurrencyCode(custPayDTO.getCurrencyCode());
                        dto.setAmount(custPayDTO.getAmount());
                        dto.setProductCategory(BillEnum.PayMethod.BALANCE_DEDUCTIONS.getPaymentName());
                        dto.setChargeCategory("物料费");
                        dto.setChargeType(BillEnum.PayMethod.BALANCE_DEDUCTIONS.getPaymentName());
                        list.add(dto);
                        custPayDTO.setSerialBillInfoList(list);
                        R<?> r = this.rechargesFeignService.feeDeductions(custPayDTO);
                        DelOutboundServiceImplUtil.chargeOperationThrowCommonException(r);
                    }
                }
                completedState = "MODIFY";
            }
            if ("MODIFY".equals(completedState)) {
                // 更新出库单状态为已完成
                this.delOutboundService.completed(delOutbound.getId());
                // 处理异常修复
                if (DelOutboundExceptionStateEnum.ABNORMAL.getCode().equals(delOutbound.getExceptionState())) {
                    this.delOutboundService.exceptionFix(delOutbound.getId());
                }
                completedState = "END";
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            // 记录异常
            this.delOutboundService.exceptionMessage(delOutbound.getId(), e.getMessage());
            // 抛异常
            throw e;
        } finally {
            // 记录处理状态
            this.delOutboundService.updateCompletedState(delOutbound.getId(), completedState);
        }
    }

    /**
     * 扣减库存
     *
     * @param orderNo       orderNo
     * @param warehouseCode warehouseCode
     * @param orderType     orderType
     */
    private void deduction(String orderNo, String warehouseCode, String orderType) {
        if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
            return;
        }
        // 查询明细
        List<DelOutboundDetail> details = this.delOutboundDetailService.listByOrderNo(orderNo);
        InventoryOperateListDto inventoryOperateListDto = new InventoryOperateListDto();
        Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
        for (DelOutboundDetail detail : details) {
            DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
        }
        inventoryOperateListDto.setInvoiceNo(orderNo);
        inventoryOperateListDto.setWarehouseCode(warehouseCode);
        List<InventoryOperateDto> operateList = new ArrayList<>(inventoryOperateDtoMap.values());
        inventoryOperateListDto.setOperateList(operateList);
        // 扣减库存
        Integer deduction = this.inventoryFeignClientService.deduction(inventoryOperateListDto);
        if (null == deduction || deduction < 1) {
            throw new CommonException("999", "扣减库存失败");
        }
    }

    @Override
    public void cancelled(String orderNo) {
        // 处理阶段
        // 1.取消冻结库存                                 UN_FREEZE
        // 2.1取消冻结费用                                UN_FEE
        // 2.2取消冻结操作费用                             UN_OP_FEE
        // 3.如果是，下单后供应商获取，需要取消承运商物流订单 UN_CARRIER
        // 4.更新状态为已取消                              MODIFY
        // 5.完成                                         END
        DelOutbound delOutbound = this.delOutboundService.getByOrderNo(orderNo);
        if (null == delOutbound) {
            return;
        }
        // 订单完成
        if (DelOutboundStateEnum.COMPLETED.getCode().equals(delOutbound.getState())) {
            return;
        }
        // 订单取消
        if (DelOutboundStateEnum.CANCELLED.getCode().equals(delOutbound.getState())) {
            return;
        }
        // 获取到处理状态
        String cancelledState = delOutbound.getCancelledState();
        try {
            if (StringUtils.isEmpty(cancelledState)) {
                this.delOutboundService.unFreeze(delOutbound.getOrderType(), orderNo, delOutbound.getWarehouseCode());
                cancelledState = "UN_FEE";
            }
            // 销毁，自提，新SKU不扣物流费用
            boolean fee = true;
            if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
                fee = false;
            }
            if ("UN_FEE".equals(cancelledState)) {
                if (fee) {
                    // 存在费用
                    if (null != delOutbound.getAmount() && delOutbound.getAmount().doubleValue() > 0.0D) {
                        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                        cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
                        cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
                        cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                        cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
                        cusFreezeBalanceDTO.setOrderType("Freight");
                        R<?> thawBalanceR = this.rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
                        if (null == thawBalanceR) {
                            throw new CommonException("999", "取消冻结费用失败");
                        }
                        if (Constants.SUCCESS != thawBalanceR.getCode()) {
                            throw new CommonException("999", Utils.defaultValue(thawBalanceR.getMsg(), "取消冻结费用失败2"));
                        }
                    }
                }
                cancelledState = "UN_OP_FEE";
            }
            if ("UN_OP_FEE".equals(cancelledState)) {
                DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
                delOutboundOperationVO.setOrderType(delOutbound.getOrderType());
                delOutboundOperationVO.setOrderNo(orderNo);
                R<?> r = this.operationFeignService.delOutboundThaw(delOutboundOperationVO);
                DelOutboundServiceImplUtil.thawOperationThrowCommonException(r);
                cancelledState = "UN_CARRIER";
            }
            if ("UN_CARRIER".equals(cancelledState)) {
                // 取消承运商物流订单
                String shipmentOrderNumber = delOutbound.getShipmentOrderNumber();
                String trackingNo = delOutbound.getTrackingNo();
                if (StringUtils.isNotEmpty(shipmentOrderNumber) && StringUtils.isNotEmpty(trackingNo)) {
                    String referenceNumber = String.valueOf(delOutbound.getId());
                    this.delOutboundBringVerifyService.cancellation(delOutbound.getWarehouseCode(), referenceNumber, shipmentOrderNumber, trackingNo);
                }
                cancelledState = "MODIFY";
            }
            if ("MODIFY".equals(cancelledState)) {
                // 更新出库单状态
                this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.CANCELLED);
                // 处理异常修复
                if (DelOutboundExceptionStateEnum.ABNORMAL.getCode().equals(delOutbound.getExceptionState())) {
                    this.delOutboundService.exceptionFix(delOutbound.getId());
                }
                cancelledState = "END";
            }
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
            // 记录异常
            this.delOutboundService.exceptionMessage(delOutbound.getId(), e.getMessage());
            // 抛异常
            throw e;
        } finally {
            this.delOutboundService.updateCancelledState(delOutbound.getId(), cancelledState);
        }
    }

}
