package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.codec.Base64;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出库发货步骤
 *
 * @author zhangyuyuan
 * @date 2021-04-01 16:21
 */
public enum ShipmentEnum implements ApplicationState, ApplicationRegister {

    /**
     * 开始
     */
    BEGIN,

    // #1 创建承运商物流订单
    SHIPMENT_ORDER,

    // #2 更新挂号
    SHIPMENT_TRACKING,

    // #3 获取标签
    LABEL,

    // #4 更新标签
    SHIPMENT_LABEL,

    // #5 取消冻结费用
    THAW_BALANCE,

    // #6 PRC计费
    PRC_PRICING,

    // #7 冻结费用
    FREEZE_BALANCE,

    // #8 更新发货指令
    SHIPMENT_SHIPPING,

    /**
     * 结束
     */
    END,
    ;

    public static ShipmentEnum get(String name) {
        for (ShipmentEnum anEnum : ShipmentEnum.values()) {
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
        map.put(SHIPMENT_ORDER.name(), new ShipmentOrderHandle());
        map.put(SHIPMENT_TRACKING.name(), new ShipmentTrackingHandle());
        map.put(LABEL.name(), new LabelHandle());
        map.put(SHIPMENT_LABEL.name(), new ShipmentLabelHandle());
        map.put(THAW_BALANCE.name(), new ThawBalanceHandle());
        map.put(PRC_PRICING.name(), new PrcPricingHandle());
        map.put(FREEZE_BALANCE.name(), new FreezeBalanceHandle());
        map.put(SHIPMENT_SHIPPING.name(), new ShipmentShippingHandle());
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
            // 先判断规则
            boolean condition = ApplicationRuleConfig.shipmentCondition(orderTypeEnum, currentState.name());
            if (condition) {
                // 再判断子级规则
                return this.otherCondition(context, currentState);
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
            updateDelOutbound.setShipmentState(currentState.name());
            // 出库失败
            String exceptionMessage = Utils.defaultValue(throwable.getMessage(), "出库操作失败");
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            updateDelOutbound.setExceptionMessage(exceptionMessage);
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            // 冻结费用
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            delOutboundService.shipmentFail(updateDelOutbound);
            // 冻结费用失败 - OutboundNoMoney
            // 其余的都归类为 - OutboundGetTrackingFailed
            String exType;
            if (FREEZE_BALANCE.equals(currentState)) {
                exType = "OutboundNoMoney";
            } else {
                exType = "OutboundGetTrackingFailed";
            }
            // 更新消息到WMS
            ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
            shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
            shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
            shipmentUpdateRequestDto.setIsEx(true);
            shipmentUpdateRequestDto.setExType(exType);
            shipmentUpdateRequestDto.setExRemark(Utils.defaultValue(throwable.getMessage(), "操作失败"));
            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
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
            // 创建承运商物流订单
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
            String trackingNo = shipmentOrderResult.getMainTrackingNumber();
            String orderNumber = shipmentOrderResult.getOrderNumber();
            // 返回值
            delOutbound.setTrackingNo(trackingNo);
            delOutbound.setShipmentOrderNumber(orderNumber);
        }

        @Override
        public void rollback(ApplicationContext context) {
            // 取消承运商物流订单
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String shipmentOrderNumber = delOutbound.getShipmentOrderNumber();
            String trackingNo = delOutbound.getTrackingNo();
            if (StringUtils.isNotEmpty(shipmentOrderNumber) && StringUtils.isNotEmpty(trackingNo)) {
                String referenceNumber = String.valueOf(delOutbound.getId());
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                delOutboundBringVerifyService.cancellation(referenceNumber, shipmentOrderNumber, trackingNo);
            }
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 判断获取承运商信息
            return DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType());
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_TRACKING;
        }
    }

    static class ShipmentTrackingHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState quoState() {
            return SHIPMENT_TRACKING;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 更新WMS挂号
            ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
            shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getRefOrderNo());
            shipmentTrackingChangeRequestDto.setTrackingNo(delOutbound.getTrackingNo());
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            htpOutboundClientService.shipmentTracking(shipmentTrackingChangeRequestDto);
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 判断获取承运商信息
            return DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType());
        }

        @Override
        public ApplicationState nextState() {
            return LABEL;
        }
    }

    static class LabelHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState quoState() {
            return LABEL;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String orderNumber = delOutbound.getShipmentOrderNumber();
            // 获取标签
            IHtpCarrierClientService htpCarrierClientService = SpringUtils.getBean(IHtpCarrierClientService.class);
            ResponseObject<FileStream, ProblemDetails> responseObject = htpCarrierClientService.label(orderNumber);
            if (null != responseObject) {
                if (responseObject.isSuccess()) {
                    FileStream fileStream = responseObject.getObject();
                    String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
                    // 保存文件
                    // 单据类型/年/月/日/单据号
                    String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
                    File file = new File(basedir + "/shipment/label/" + labelBizPath);
                    if (!file.exists()) {
                        try {
                            FileUtils.forceMkdir(file);
                        } catch (IOException e) {
                            throw new CommonException("999", "创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage());
                        }
                    }
                    byte[] inputStream;
                    if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                        File labelFile = new File(file.getPath() + "/" + orderNumber);
                        try {
                            FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                        } catch (IOException e) {
                            throw new CommonException("999", "保存标签文件失败，Error：" + e.getMessage());
                        }
                    }
                } else {
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "获取标签文件流失败2");
                    throw new CommonException("999", exceptionMessage);
                }
            } else {
                throw new CommonException("999", "获取标签文件流失败");
            }
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            return StringUtils.isNotEmpty(delOutbound.getShipmentOrderNumber());
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
            String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
            String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
            File labelFile = new File(basedir + "/shipment/label/" + labelBizPath + "/" + delOutbound.getShipmentOrderNumber());
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
                throw new CommonException("999", "读取标签文件失败");
            }
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            return StringUtils.isNotEmpty(delOutbound.getShipmentOrderNumber());
        }

        @Override
        public ApplicationState nextState() {
            return THAW_BALANCE;
        }
    }

    static class ThawBalanceHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState quoState() {
            return THAW_BALANCE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 取消冻结费用
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
            // 调用冻结费用接口
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> thawBalanceR = rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
            if (null == thawBalanceR) {
                throw new CommonException("999", "取消冻结费用失败");
            }
            if (Constants.SUCCESS != thawBalanceR.getCode()) {
                // 异常信息
                String msg = thawBalanceR.getMsg();
                if (StringUtils.isEmpty(msg)) {
                    msg = "取消冻结费用失败";
                }
                throw new CommonException("999", msg);
            }
            // 清除费用信息
            IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
            delOutboundChargeService.clearCharges(delOutbound.getOrderNo());
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
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 获取运费信息
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    Weight calcWeight = packageInfo.getCalcWeight();
                    delOutbound.setCalcWeight(calcWeight.getValue());
                    delOutbound.setCalcWeightUnit(calcWeight.getUnit());
                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    // 保存新的费用信息
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
                    IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
                    delOutboundChargeService.saveCharges(delOutboundCharges);
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
            // 冻结费用
            CusFreezeBalanceDTO cusFreezeBalanceDTO2 = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO2.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO2.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO2.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO2.setNo(delOutbound.getOrderNo());
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO2);
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
            return SHIPMENT_SHIPPING;
        }
    }

    static class ShipmentShippingHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState quoState() {
            return SHIPMENT_SHIPPING;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
            shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
            shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
            shipmentUpdateRequestDto.setIsEx(false);
            shipmentUpdateRequestDto.setExType(null);
            shipmentUpdateRequestDto.setExRemark(null);
            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            ResponseVO responseVO = htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
            if (null == responseVO || null == responseVO.getSuccess()) {
                throw new CommonException("999", "更新发货指令失败");
            }
            if (!responseVO.getSuccess()) {
                throw new CommonException("999", Utils.defaultValue(responseVO.getMessage(), "更新发货指令失败2"));
            }
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setShipmentState(END.name());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setOrderNo(delOutbound.getOrderNo());
            // 冻结费用
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            delOutboundService.shipmentSuccess(updateDelOutbound);
        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }

    static class EndHandle extends BringVerifyEnum.CommonApplicationHandle {

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
