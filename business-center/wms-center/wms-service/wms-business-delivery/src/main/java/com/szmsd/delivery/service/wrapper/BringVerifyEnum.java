package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.codec.Base64;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 出库单提审步骤
 *
 * @author zhangyuyuan
 * @date 2021-04-01 16:08
 */
public enum BringVerifyEnum implements ApplicationState, ApplicationRegister {

    /**
     * 开始
     */
    BEGIN,

    // #1 PRC 计费        - 无回滚
    PRC_PRICING,

    // #2 冻结物流费用    - 有回滚
    FREEZE_BALANCE,

    // #3 获取产品信息    - 无回滚
    PRODUCT_INFO,

    // #4 新增/修改发货规则 - 无回滚
    SHIPMENT_RULE,

    // #5 创建承运商物流订单 - 有回滚
    SHIPMENT_ORDER,

    // #6 冻结库存          - 有回滚
    FREEZE_INVENTORY,

    // #7 冻结操作费用       - 有回滚
    FREEZE_OPERATION,

    // #8 推单WMS             - 无回滚，需要取消单据
    SHIPMENT_CREATE,

    // #9 更新标签             - 无回滚
    SHIPMENT_LABEL,

    /**
     * 结束
     */
    END,
    ;

    public static BringVerifyEnum get(String name) {
        for (BringVerifyEnum anEnum : BringVerifyEnum.values()) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 大于
     *
     * @param e1 e1为对照
     * @param e2 e2大于e1
     * @return boolean
     */
    public static boolean gt(BringVerifyEnum e1, BringVerifyEnum e2) {
        if (null == e1 || null == e2) {
            throw new CommonException("400", "枚举类型不能为空");
        }
        return e1.ordinal() < e2.ordinal();
    }

    @Override
    public Map<String, ApplicationHandle> register() {
        Map<String, ApplicationHandle> map = new HashMap<>();
        map.put(BEGIN.name(), new BeginHandle());
        map.put(PRC_PRICING.name(), new PrcPricingHandle());
        map.put(FREEZE_BALANCE.name(), new FreezeBalanceHandle());
        map.put(PRODUCT_INFO.name(), new ProductInfoHandle());
        map.put(SHIPMENT_RULE.name(), new ShipmentRuleHandle());
        map.put(SHIPMENT_ORDER.name(), new ShipmentOrderHandle());
        map.put(FREEZE_INVENTORY.name(), new FreezeInventoryHandle());
        map.put(FREEZE_OPERATION.name(), new FreezeOperationHandle());
        map.put(SHIPMENT_CREATE.name(), new ShipmentCreateHandle());
        map.put(SHIPMENT_LABEL.name(), new ShipmentLabelHandle());
        map.put(END.name(), new EndHandle());
        return map;
    }

    static abstract class CommonApplicationHandle extends ApplicationHandle.AbstractApplicationHandle {

        @Override
        public boolean condition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOrderTypeEnum orderTypeEnum = DelOutboundOrderTypeEnum.get(delOutbound.getOrderType());
            if (null == orderTypeEnum) {
                throw new CommonException("400", "不存在的类型[" + delOutbound.getOrderType() + "]");
            }
            boolean condition = ApplicationRuleConfig.bringVerifyCondition(orderTypeEnum, currentState.name());
            if (condition) {
                return otherCondition(context, currentState);
            }
            return false;
        }

        /**
         * 子级处理条件
         *
         * @param context      context
         * @param currentState currentState
         * @return boolean
         */
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            return true;
        }

        /**
         * 批量出库-自提出库
         *
         * @param context      context
         * @param currentState currentState
         * @return boolean
         */
        @SuppressWarnings({"all"})
        public boolean batchSelfPick(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            if (context instanceof DelOutboundWrapperContext) {
                DelOutbound delOutbound = ((DelOutboundWrapperContext) context).getDelOutbound();
                String orderType = StringUtils.nvl(delOutbound.getOrderType(), "");
                String shipmentChannel = StringUtils.nvl(delOutbound.getShipmentChannel(), "");
                if (orderType.equals(DelOutboundOrderTypeEnum.BATCH.getCode()) && "SelfPick".equals(shipmentChannel)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void errorHandler(ApplicationContext context, Throwable throwable, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setBringVerifyState(currentState.name());
            // 提审失败
            String exceptionMessage = Utils.defaultValue(throwable.getMessage(), "提审操作失败");
            updateDelOutbound.setExceptionMessage(exceptionMessage);
            // PRC计费
            updateDelOutbound.setLength(delOutbound.getLength());
            updateDelOutbound.setWidth(delOutbound.getWidth());
            updateDelOutbound.setHeight(delOutbound.getHeight());
            // 规格，长*宽*高
            updateDelOutbound.setSpecifications(delOutbound.getLength() + "*" + delOutbound.getWidth() + "*" + delOutbound.getHeight());
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            // 产品信息
            updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
            updateDelOutbound.setProductShipmentRule(delOutbound.getProductShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
            // 推单WMS
            updateDelOutbound.setRefOrderNo(delOutbound.getRefOrderNo());
            delOutboundService.bringVerifyFail(updateDelOutbound);
        }
    }

    static class BeginHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return BEGIN;
        }

        @Override
        public ApplicationState quoState() {
            return BEGIN;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return PRC_PRICING;
        }
    }

    static class PrcPricingHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return BEGIN;
        }

        @Override
        public ApplicationState quoState() {
            return PRC_PRICING;
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            return super.batchSelfPick(context, currentState);
        }

        @Override
        public void handle(ApplicationContext context) {
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.SKU);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("400", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    // 挂号服务
                    delOutbound.setShipmentService(data.getLogisticsRouteId());
                    // 包裹信息
                    Packing packing = packageInfo.getPacking();
                    delOutbound.setLength(Utils.valueOf(packing.getLength()));
                    delOutbound.setWidth(Utils.valueOf(packing.getWidth()));
                    delOutbound.setHeight(Utils.valueOf(packing.getHeight()));
                    // 计费重信息
                    Weight calcWeight = packageInfo.getCalcWeight();
                    delOutbound.setCalcWeight(calcWeight.getValue());
                    delOutbound.setCalcWeightUnit(calcWeight.getUnit());
                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    // 保存费用信息
                    List<DelOutboundCharge> delOutboundCharges = new ArrayList<>();
                    // 汇总费用
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    String totalCurrencyCode = charges.get(0).getMoney().getCurrencyCode();
                    for (ChargeItem charge : charges) {
                        DelOutboundCharge delOutboundCharge = new DelOutboundCharge();
                        ChargeCategory chargeCategory = charge.getChargeCategory();
                        delOutboundCharge.setOrderNo(delOutbound.getOrderNo());
                        delOutboundCharge.setBillingNo(chargeCategory.getBillingNo());
                        delOutboundCharge.setChargeNameCn(chargeCategory.getChargeNameCN());
                        delOutboundCharge.setChargeNameEn(chargeCategory.getChargeNameEN());
                        delOutboundCharge.setParentBillingNo(chargeCategory.getParentBillingNo());
                        Money money = charge.getMoney();
                        BigDecimal amount = Utils.valueOf(money.getAmount());
                        delOutboundCharge.setAmount(amount);
                        delOutboundCharge.setCurrencyCode(money.getCurrencyCode());
                        delOutboundCharge.setRemark(charge.getRemark());
                        delOutboundCharges.add(delOutboundCharge);
                        totalAmount = totalAmount.add(amount);
                    }
                    // 保存出库单费用信息
                    IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
                    delOutboundChargeService.saveCharges(delOutboundCharges);
                    // 更新值
                    delOutbound.setAmount(totalAmount);
                    delOutbound.setCurrencyCode(totalCurrencyCode);
                    DelOutboundOperationLogEnum.BRV_PRC_PRICING.listener(delOutbound);
                } else {
                    // 计算失败
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                    throw new CommonException("400", exceptionMessage);
                }
            }
        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setBringVerifyState(BEGIN.name());
            // PRC计费
            updateDelOutbound.setCalcWeight(BigDecimal.ZERO);
            updateDelOutbound.setCalcWeightUnit("");
            updateDelOutbound.setAmount(BigDecimal.ZERO);
            updateDelOutbound.setCurrencyCode("");
            // 产品信息
            updateDelOutbound.setTrackingAcquireType("");
            updateDelOutbound.setShipmentService("");
            updateDelOutbound.setProductShipmentRule("");
            updateDelOutbound.setPackingRule("");
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo("");
            updateDelOutbound.setShipmentOrderNumber("");
            updateDelOutbound.setShipmentOrderLabelUrl("");
            // 推单WMS
            updateDelOutbound.setRefOrderNo("");
            delOutboundService.updateById(updateDelOutbound);
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return FREEZE_BALANCE;
        }
    }

    static class FreezeBalanceHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return PRC_PRICING;
        }

        @Override
        public ApplicationState quoState() {
            return FREEZE_BALANCE;
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            return super.batchSelfPick(context, currentState);
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.BRV_FREEZE_BALANCE.listener(delOutbound);
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
            cusFreezeBalanceDTO.setOrderType("Freight");
            // 调用冻结费用接口
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO);
            if (null != freezeBalanceR) {
                if (Constants.SUCCESS != freezeBalanceR.getCode()) {
                    // 异常信息
                    String msg = Utils.defaultValue(freezeBalanceR.getMsg(), "冻结费用信息失败2");
                    throw new CommonException("400", msg);
                }
            } else {
                // 异常信息
                throw new CommonException("400", "冻结费用信息失败");
            }
        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.RK_BRV_FREEZE_BALANCE.listener(delOutbound);
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
            cusFreezeBalanceDTO.setOrderType("Freight");
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> thawBalanceR = rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
            if (null == thawBalanceR) {
                throw new CommonException("400", "取消冻结费用失败");
            }
            if (Constants.SUCCESS != thawBalanceR.getCode()) {
                throw new CommonException("400", Utils.defaultValue(thawBalanceR.getMsg(), "取消冻结费用失败2"));
            }
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return PRODUCT_INFO;
        }
    }

    static class ProductInfoHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return FREEZE_BALANCE;
        }

        @Override
        public ApplicationState quoState() {
            return PRODUCT_INFO;
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            return super.batchSelfPick(context, currentState);
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String productCode = delOutbound.getShipmentRule();
            // 获取产品信息
            IHtpPricedProductClientService htpPricedProductClientService = SpringUtils.getBean(IHtpPricedProductClientService.class);
            PricedProductInfo pricedProductInfo = htpPricedProductClientService.infoAndSubProducts(productCode);
            if (null != pricedProductInfo) {
                delOutbound.setShipmentService(pricedProductInfo.getLogisticsRouteId());
                delOutbound.setTrackingAcquireType(pricedProductInfo.getTrackingAcquireType());
                delOutbound.setProductShipmentRule(pricedProductInfo.getShipmentRule());
                delOutbound.setPackingRule(pricedProductInfo.getPackingRule());
                DelOutboundOperationLogEnum.BRV_PRODUCT_INFO.listener(delOutbound);
            } else {
                // 异常信息
                throw new CommonException("400", "查询产品[" + productCode + "]信息失败");
            }
        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            delOutbound.setTrackingAcquireType("");
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_RULE;
        }
    }

    static class ShipmentRuleHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return PRODUCT_INFO;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_RULE;
        }

        // 批量出库，自提出库需要创建发货规则，字段使用提货商/快递商
//        @Override
//        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
//            //批量出库->自提出库 不做prc
//            return super.batchSelfPick(context, currentState);
//        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            delOutboundBringVerifyService.shipmentRule(delOutbound);
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_ORDER;
        }
    }

    static class ShipmentOrderHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_RULE;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_ORDER;
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            return super.batchSelfPick(context, currentState);
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 判断是否需要创建物流订单
            if (DelOutboundTrackingAcquireTypeEnum.ORDER_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType())) {
                // 创建承运商物流订单
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
                delOutbound.setTrackingNo(shipmentOrderResult.getMainTrackingNumber());
                delOutbound.setShipmentOrderNumber(shipmentOrderResult.getOrderNumber());
                delOutbound.setShipmentOrderLabelUrl(shipmentOrderResult.getOrderLabelUrl());
                DelOutboundOperationLogEnum.BRV_SHIPMENT_ORDER.listener(delOutbound);
            }
        }

        @Override
        public void rollback(ApplicationContext context) {
            // 取消承运商物流订单
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.RK_BRV_SHIPMENT_ORDER.listener(delOutbound);
            String shipmentOrderNumber = delOutbound.getShipmentOrderNumber();
            String trackingNo = delOutbound.getTrackingNo();
            if (StringUtils.isNotEmpty(shipmentOrderNumber) && StringUtils.isNotEmpty(trackingNo)) {
                String referenceNumber = String.valueOf(delOutbound.getId());
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                delOutboundBringVerifyService.cancellation(delOutbound.getWarehouseCode(), referenceNumber, shipmentOrderNumber, trackingNo);
                delOutbound.setTrackingNo("");
                delOutbound.setShipmentOrderNumber("");
                delOutbound.setShipmentOrderLabelUrl("");
            }
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return FREEZE_INVENTORY;
        }
    }

    static class FreezeInventoryHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_ORDER;
        }

        @Override
        public ApplicationState quoState() {
            return FREEZE_INVENTORY;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String orderType = delOutbound.getOrderType();
            if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
                return;
            }
            // 重派出库单不扣库存
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                return;
            }
            List<DelOutboundDetail> details = delOutboundWrapperContext.getDetailList();
            if (CollectionUtils.isEmpty(details)) {
                return;
            }
            InventoryOperateListDto operateListDto = new InventoryOperateListDto();
            operateListDto.setInvoiceNo(delOutbound.getOrderNo());
            operateListDto.setWarehouseCode(delOutbound.getWarehouseCode());
            ArrayList<InventoryOperateDto> operateList;
            // 拆分SKU特殊处理，拆分是主SKU出库，明细入库，这里逻辑调整一下
            if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
                InventoryOperateDto inventoryOperateDto = new InventoryOperateDto();
                inventoryOperateDto.setSku(delOutbound.getNewSku());
                inventoryOperateDto.setNum(Math.toIntExact(delOutbound.getBoxNumber()));
                operateList = new ArrayList<>(1);
                operateList.add(inventoryOperateDto);
            } else {
                Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
                for (DelOutboundDetail detail : details) {
                    DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
                }
                Collection<InventoryOperateDto> inventoryOperateDtos = inventoryOperateDtoMap.values();
                operateList = new ArrayList<>(inventoryOperateDtos);
            }
            operateListDto.setOperateList(operateList);
            // 集运出库特殊处理
            if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType)) {
                operateListDto.setFreeType(1);
            }
            operateListDto.setCusCode(delOutbound.getSellerCode());
            try {
                DelOutboundOperationLogEnum.BRV_FREEZE_INVENTORY.listener(new Object[]{delOutbound, operateList});
                InventoryFeignClientService inventoryFeignClientService = SpringUtils.getBean(InventoryFeignClientService.class);
                inventoryFeignClientService.freeze(operateListDto);
            } catch (CommonException e) {
                logger.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("400", "冻结库存操作失败，" + e.getMessage());
            }
        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.RK_BRV_FREEZE_INVENTORY.listener(delOutbound);
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            delOutboundService.unFreeze(delOutbound);
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return FREEZE_OPERATION;
        }
    }

    static class FreezeOperationHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return FREEZE_INVENTORY;
        }

        @Override
        public ApplicationState quoState() {
            return FREEZE_OPERATION;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
            delOutboundOperationVO.setOrderType(delOutbound.getOrderType());
            delOutboundOperationVO.setOrderNo(delOutbound.getOrderNo());
            delOutboundOperationVO.setWarehouseCode(delOutbound.getWarehouseCode());
            delOutboundOperationVO.setCustomCode(delOutbound.getCustomCode());
            // 处理明细
            List<DelOutboundDetail> details = delOutboundWrapperContext.getDetailList();
            List<DelOutboundOperationDetailVO> detailVOList = new ArrayList<>(details.size());
            if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
                DelOutboundOperationDetailVO detailVO = new DelOutboundOperationDetailVO();
                // 统计sku的数量
                long qty = details.stream().mapToLong(DelOutboundDetail::getQty).sum();
                detailVO.setQty(qty);
                detailVO.setWeight(delOutbound.getWeight());
                detailVOList.add(detailVO);
            } else {
                // 统计包材信息
                Set<String> skus = new HashSet<>();
                for (DelOutboundDetail detail : details) {
                    // sku信息
                    skus.add(detail.getSku());
                    // sku包材信息
                    if (StringUtils.isNotEmpty(detail.getBindCode())) {
                        skus.add(detail.getBindCode());
                    }
                }
                Map<String, BaseProduct> productMap = null;
                if (CollectionUtils.isNotEmpty(skus)) {
                    BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
                    baseProductConditionQueryDto.setSkus(new ArrayList<>(skus));
                    BaseProductClientService baseProductClientService = SpringUtils.getBean(BaseProductClientService.class);
                    List<BaseProduct> productList = baseProductClientService.queryProductList(baseProductConditionQueryDto);
                    if (CollectionUtils.isNotEmpty(productList)) {
                        productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (v1, v2) -> v1));
                    }
                }
                // 没有查询到SKU信息
                if (null == productMap) {
                    throw new CommonException("400", "查询SKU信息失败");
                }
                // 处理操作费用参数
                for (DelOutboundDetail detail : details) {
                    String sku = detail.getSku();
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", "SKU[" + sku + "]信息不存在");
                    }
                    // 操作费对象
                    DelOutboundOperationDetailVO detailVO = new DelOutboundOperationDetailVO();
                    detailVO.setSku(sku);
                    detailVO.setQty(detail.getQty());
                    detailVO.setWeight(Utils.defaultValue(product.getWeight()));
                    detailVOList.add(detailVO);
                    // 判断有没有包材
                    String bindCode = detail.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode) && productMap.containsKey(bindCode)) {
                        BaseProduct baseProduct = productMap.get(bindCode);
                        DelOutboundOperationDetailVO vo = new DelOutboundOperationDetailVO();
                        vo.setSku(bindCode);
                        vo.setQty(detail.getQty());
                        vo.setWeight(Utils.defaultValue(baseProduct.getWeight()));
                        detailVOList.add(vo);
                    }
                }
            }
            delOutboundOperationVO.setDetails(detailVOList);
            DelOutboundOperationLogEnum.BRV_FREEZE_OPERATION.listener(new Object[]{delOutbound, detailVOList});
            OperationFeignService operationFeignService = SpringUtils.getBean(OperationFeignService.class);
            R<?> r = operationFeignService.delOutboundFreeze(delOutboundOperationVO);
            DelOutboundServiceImplUtil.freezeOperationThrowErrorMessage(r);
        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.RK_BRV_FREEZE_OPERATION.listener(delOutbound);
            DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
            delOutboundOperationVO.setOrderType(delOutbound.getOrderType());
            delOutboundOperationVO.setOrderNo(delOutbound.getOrderNo());
            OperationFeignService operationFeignService = SpringUtils.getBean(OperationFeignService.class);
            R<?> r = operationFeignService.delOutboundThaw(delOutboundOperationVO);
            DelOutboundServiceImplUtil.thawOperationThrowCommonException(r);
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_CREATE;
        }
    }

    static class ShipmentCreateHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return FREEZE_OPERATION;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_CREATE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 推单到WMS
            // 重派出库单不扣库存
            String refOrderNo = "";
            if (!DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                refOrderNo = delOutboundBringVerifyService.shipmentCreate(delOutboundWrapperContext, delOutbound.getTrackingNo());
                delOutbound.setRefOrderNo(refOrderNo);
                DelOutboundOperationLogEnum.BRV_SHIPMENT_CREATE.listener(delOutbound);
            }
            // 保存信息
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setBringVerifyState(END.name());
            // PRC计费
            updateDelOutbound.setLength(delOutbound.getLength());
            updateDelOutbound.setWidth(delOutbound.getWidth());
            updateDelOutbound.setHeight(delOutbound.getHeight());
            // 规格，长*宽*高
            updateDelOutbound.setSpecifications(delOutbound.getLength() + "*" + delOutbound.getWidth() + "*" + delOutbound.getHeight());
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            // 产品信息
            updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
            updateDelOutbound.setProductShipmentRule(delOutbound.getProductShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
            // 推单WMS
            updateDelOutbound.setRefOrderNo(refOrderNo);
            delOutboundService.bringVerifySuccess(updateDelOutbound);
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_LABEL;
        }
    }

    static class ShipmentLabelHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return SHIPMENT_CREATE;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_LABEL;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.BRV_SHIPMENT_LABEL.listener(delOutbound);
            // 查询上传文件信息
            RemoteAttachmentService remoteAttachmentService = SpringUtils.getBean(RemoteAttachmentService.class);
            BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
            basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
            basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
            R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
            if (null == listR || null == listR.getData()) {
                return;
            }
            List<BasAttachment> attachmentList = listR.getData();
            if (CollectionUtils.isEmpty(attachmentList)) {
                return;
            }
            BasAttachment attachment = attachmentList.get(0);
            String filePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
            File labelFile = new File(filePath);
            if (!labelFile.exists()) {
                throw new CommonException("500", "标签文件不存在");
            }
            try {
                byte[] byteArray = FileUtils.readFileToByteArray(labelFile);
                String encode = Base64.encode(byteArray);
                ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                shipmentLabelChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
                shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
                shipmentLabelChangeRequestDto.setLabel(encode);
                IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
                ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                if (null == responseVO || null == responseVO.getSuccess()) {
                    throw new CommonException("500", "更新标签失败");
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("500", Utils.defaultValue(responseVO.getMessage(), "更新标签失败2"));
                }
            } catch (IOException e) {
                logger.error("读取标签文件失败, {}", e.getMessage(), e);
                throw new CommonException("500", "读取标签文件失败");
            }
        }

        @Override
        public boolean condition(ApplicationContext context, ApplicationState currentState) {
            boolean condition = super.condition(context, currentState);
            if (condition) {
                DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
                DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
                // 自提出库 或者 批量出库（渠道是自提出库）
                return DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())
                        || (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel()));
            }
            return false;
        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }

    static class EndHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_LABEL;
        }

        @Override
        public ApplicationState quoState() {
            return END;
        }

        @Override
        public void handle(ApplicationContext context) {

        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }
}
