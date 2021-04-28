package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.codec.Base64;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpIBasClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.BaseOperationResponse;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // #1 PRC 计费
    PRC_PRICING,

    // #2 冻结费用
    FREEZE_BALANCE,

    // #3 获取产品信息
    PRODUCT_INFO,

    // #4 新增/修改发货规则
    SHIPMENT_RULE,

    // #5 创建承运商物流订单
    SHIPMENT_ORDER,

    // #6 推单WMS
    SHIPMENT_CREATE,

    // #7 更新标签
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

    @Override
    public Map<String, ApplicationHandle> register() {
        Map<String, ApplicationHandle> map = new HashMap<>();
        map.put(BEGIN.name(), new BeginHandle());
        map.put(PRC_PRICING.name(), new PrcPricingHandle());
        map.put(FREEZE_BALANCE.name(), new FreezeBalanceHandle());
        map.put(PRODUCT_INFO.name(), new ProductInfoHandle());
        map.put(SHIPMENT_RULE.name(), new ShipmentRuleHandle());
        map.put(SHIPMENT_ORDER.name(), new ShipmentOrderHandle());
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
                throw new CommonException("999", "不存在的类型[" + delOutbound.getOrderType() + "]");
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
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            // 产品信息
            updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            // 推单WMS
            updateDelOutbound.setRefOrderNo(delOutbound.getRefOrderNo());
            delOutboundService.bringVerifyFail(updateDelOutbound);
        }
    }

    static class BeginHandle extends CommonApplicationHandle {

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
        public ApplicationState quoState() {
            return PRC_PRICING;
        }

        @Override
        public void handle(ApplicationContext context) {
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    DelOutbound updateDelOutbound = new DelOutbound();
                    updateDelOutbound.setId(delOutbound.getId());
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    Weight calcWeight = packageInfo.getCalcWeight();
                    updateDelOutbound.setCalcWeight(calcWeight.getValue());
                    updateDelOutbound.setCalcWeightUnit(calcWeight.getUnit());
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
                } else {
                    // 计算失败
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                    throw new CommonException("999", exceptionMessage);
                }
            }
        }

        @Override
        public ApplicationState nextState() {
            return FREEZE_BALANCE;
        }
    }

    static class FreezeBalanceHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return FREEZE_BALANCE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
            // 调用冻结费用接口
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO);
            if (null != freezeBalanceR) {
                if (Constants.SUCCESS != freezeBalanceR.getCode()) {
                    // 异常信息
                    String msg = Utils.defaultValue(freezeBalanceR.getMsg(), "冻结费用信息失败2");
                    throw new CommonException("999", msg);
                }
            } else {
                // 异常信息
                throw new CommonException("999", "冻结费用信息失败");
            }
        }

        @Override
        public ApplicationState nextState() {
            return PRODUCT_INFO;
        }
    }

    static class ProductInfoHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return PRODUCT_INFO;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String productCode = delOutbound.getShipmentRule();
            // 获取产品信息
            IHtpPricedProductClientService htpPricedProductClientService = SpringUtils.getBean(IHtpPricedProductClientService.class);
            PricedProductInfo pricedProductInfo = htpPricedProductClientService.info(productCode);
            if (null != pricedProductInfo) {
                delOutbound.setTrackingAcquireType(pricedProductInfo.getTrackingAcquireType());
                delOutbound.setShipmentService(pricedProductInfo.getLogisticsRouteId());
            } else {
                // 异常信息
                throw new CommonException("999", "查询产品[" + productCode + "]信息失败");
            }
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_RULE;
        }
    }

    static class ShipmentRuleHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_RULE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 调用新增/修改发货规则
            AddShipmentRuleRequest addShipmentRuleRequest = new AddShipmentRuleRequest();
            addShipmentRuleRequest.setShipmentRule(delOutbound.getShipmentRule());
            addShipmentRuleRequest.setGetLabelType(delOutbound.getTrackingAcquireType());
            IHtpIBasClientService htpIBasClientService = SpringUtils.getBean(IHtpIBasClientService.class);
            BaseOperationResponse baseOperationResponse = htpIBasClientService.shipmentRule(addShipmentRuleRequest);
            if (null == baseOperationResponse || null == baseOperationResponse.getSuccess()) {
                throw new CommonException("999", "新增/修改发货规则失败");
            }
            if (!baseOperationResponse.getSuccess()) {
                String msg = baseOperationResponse.getMessage();
                if (StringUtils.isEmpty(msg)) {
                    msg = baseOperationResponse.getErrors();
                }
                String message = Utils.defaultValue(msg, "新增/修改发货规则失败");
                throw new CommonException("999", message);
            }
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_ORDER;
        }
    }

    static class ShipmentOrderHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_ORDER;
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
            }
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_CREATE;
        }
    }

    static class ShipmentCreateHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_CREATE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 推单到WMS
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            String refOrderNo = delOutboundBringVerifyService.shipmentCreate(delOutboundWrapperContext, delOutbound.getTrackingNo());
            // 保存信息
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setBringVerifyState(END.name());
            // PRC计费
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            // 产品信息
            updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
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
        public ApplicationState quoState() {
            return SHIPMENT_LABEL;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
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
                throw new CommonException("999", "标签文件不存在");
            }
            try {
                byte[] byteArray = FileUtils.readFileToByteArray(labelFile);
                String encode = Base64.encode(byteArray);
                ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
                shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
                shipmentLabelChangeRequestDto.setLabel(encode);
                IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
                ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                if (null == responseVO || null == responseVO.getSuccess()) {
                    throw new CommonException("999", "更新标签失败");
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("999", Utils.defaultValue(responseVO.getMessage(), "更新标签失败2"));
                }
            } catch (IOException e) {
                logger.error("读取标签文件失败, {}", e.getMessage(), e);
                throw new CommonException("999", "读取标签文件失败");
            }
        }

        @Override
        public boolean condition(ApplicationContext context, ApplicationState currentState) {
            boolean condition = super.condition(context, currentState);
            if (condition) {
                DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
                DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
                // 自提出库
                return DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType());
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
