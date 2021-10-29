package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.codec.Base64;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.util.PdfUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    // #8 冻结库存（转运出库，集运出库）
    FREEZE_INVENTORY,

    // #9 更新发货指令
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
        map.put(FREEZE_INVENTORY.name(), new FreezeInventoryHandle());
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
                throw new CommonException("400", "不存在的类型[" + delOutbound.getOrderType() + "]");
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
                String orderType = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getOrderType(), "");
                String shipmentChannel = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getShipmentChannel(), "");
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
            updateDelOutbound.setShipmentState(currentState.name());
            // 出库失败
            String exceptionMessage = Utils.defaultValue(throwable.getMessage(), "出库操作失败");
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            updateDelOutbound.setExceptionMessage(exceptionMessage);
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
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
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            delOutboundService.shipmentFail(updateDelOutbound);
            delOutbound.setExceptionState(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
            delOutbound.setExceptionMessage(exceptionMessage);
            DelOutboundOperationLogEnum.SMT_SHIPMENT_SHIPPING.listener(delOutbound);
            // 冻结费用失败 - OutboundNoMoney
            // 其余的都归类为 - OutboundGetTrackingFailed
            String exType;
            if (FREEZE_BALANCE.equals(currentState)) {
                exType = "OutboundNoMoney";
            } else {
                exType = "OutboundGetTrackingFailed";
            }
            // 更新消息到WMS
            if (delOutboundWrapperContext.isShipmentShipping()) {
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
            return SHIPMENT_ORDER;
        }
    }

    static class ShipmentOrderHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return BEGIN;
        }

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
            DelOutboundOperationLogEnum.SMT_SHIPMENT_ORDER.listener(delOutbound);
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
                delOutboundBringVerifyService.cancellation(delOutbound.getWarehouseCode(), referenceNumber, shipmentOrderNumber, trackingNo);
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
        public ApplicationState preState() {
            return SHIPMENT_ORDER;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_TRACKING;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.SMT_SHIPMENT_TRACKING.listener(delOutbound);
            // 更新WMS挂号
            ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
            shipmentTrackingChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
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
        public ApplicationState preState() {
            return SHIPMENT_TRACKING;
        }

        @Override
        public ApplicationState quoState() {
            return LABEL;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String orderNumber = delOutbound.getShipmentOrderNumber();
            DelOutboundOperationLogEnum.SMT_LABEL.listener(delOutbound);
            // 获取标签
            IHtpCarrierClientService htpCarrierClientService = SpringUtils.getBean(IHtpCarrierClientService.class);
            CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
            command.setWarehouseCode(delOutbound.getWarehouseCode());
            command.setOrderNumber(orderNumber);
            // 获取标签文件增加重试机制
            // 重试间隔：30s
            int retryCount = 0;
            boolean requestLabel = false;
            // 记录最后一次的异常信息
            CommonException commonException = null;
            do {
                logger.info("正在获取标签文件，单号：{}，重试次数：{}", delOutbound.getOrderNo(), retryCount);
                if (null != commonException) {
                    logger.info("打印上一次的异常信息，", commonException);
                }
                ResponseObject<FileStream, ProblemDetails> responseObject = htpCarrierClientService.label(command);
                if (null != responseObject) {
                    if (responseObject.isSuccess()) {
                        FileStream fileStream = responseObject.getObject();
                        String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound);
                        File file = new File(pathname);
                        if (!file.exists()) {
                            try {
                                FileUtils.forceMkdir(file);
                            } catch (IOException e) {
                                // 内部异常，不再重试，直接抛出去
                                throw new CommonException("500", "创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage());
                            }
                        }
                        byte[] inputStream;
                        if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                            File labelFile = new File(file.getPath() + "/" + orderNumber);
                            try {
                                FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                                requestLabel = true;
                            } catch (IOException e) {
                                // 内部异常，不再重试，直接抛出去
                                throw new CommonException("500", "保存标签文件失败，Error：" + e.getMessage());
                            }
                        }
                    } else {
                        // 接口响应异常，继续重试
                        String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "获取标签文件流失败2");
                        logger.error(exceptionMessage);
                        commonException = new CommonException("500", exceptionMessage);
                    }
                } else {
                    // 接口响应异常继续重试
                    logger.error("获取标签文件流失败");
                    commonException = new CommonException("500", "获取标签文件流失败");
                }
                if (!requestLabel) {
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    retryCount++;
                }
            } while (!requestLabel && retryCount < 6);
            if (null != commonException) {
                throw commonException;
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
        public ApplicationState preState() {
            return LABEL;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_LABEL;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.SMT_SHIPMENT_LABEL.listener(delOutbound);
            String pathname = null;
            // 如果是批量出库，将批量出库上传的文件和标签文件合并在一起传过去
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())
                    && delOutbound.getIsLabelBox()) {
                // 判断文件是否已经创建
                String mergeFileDirPath = DelOutboundServiceImplUtil.getBatchMergeFilePath(delOutbound);
                File mergeFileDir = new File(mergeFileDirPath);
                if (!mergeFileDir.exists()) {
                    try {
                        FileUtils.forceMkdir(mergeFileDir);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new CommonException("500", "创建文件夹失败，" + e.getMessage());
                    }
                }
                String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
                File mergeFile = new File(mergeFilePath);
                if (!mergeFile.exists()) {
                    // 合并文件
                    // 查询上传文件
                    // 查询上传文件信息
                    RemoteAttachmentService remoteAttachmentService = SpringUtils.getBean(RemoteAttachmentService.class);
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isNotEmpty(attachmentList)) {
                            BasAttachment attachment = attachmentList.get(0);
                            // 箱标文件 - 上传的
                            String boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                            String labelFilePath = "";
                            if ((DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel()))) {
                                // 批量出库的自提出库标签是上传的
                                // 查询上传的文件
                                basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
                                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                                R<List<BasAttachment>> documentListR = remoteAttachmentService.list(basAttachmentQueryDTO);
                                if (null != documentListR && null != documentListR.getData()) {
                                    List<BasAttachment> documentList = documentListR.getData();
                                    if (CollectionUtils.isNotEmpty(documentList)) {
                                        BasAttachment basAttachment = documentList.get(0);
                                        labelFilePath = basAttachment.getAttachmentPath() + "/" + basAttachment.getAttachmentName() + basAttachment.getAttachmentFormat();
                                    }
                                }
                            } else {
                                // 标签文件 - 从承运商物流那边获取的
                                labelFilePath = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber();
                            }
                            // 合并文件
                            try {
                                if (PdfUtil.merge(mergeFilePath, boxFilePath, labelFilePath)) {
                                    pathname = mergeFilePath;
                                }
                            } catch (IOException e) {
                                logger.error(e.getMessage(), e);
                                throw new CommonException("500", "合并箱标文件，标签文件失败");
                            }
                        } else {
                            throw new CommonException("500", "箱标文件未上传");
                        }
                    } else {
                        throw new CommonException("500", "箱标文件未上传");
                    }
                }
            }
            if (null == pathname) {
                pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber();
            }
            File labelFile = new File(pathname);
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
                    throw new CommonException("400", "更新标签失败");
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "更新标签失败2"));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("500", "读取标签文件失败");
            }
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            return StringUtils.isNotEmpty(delOutbound.getShipmentOrderNumber())
                    || (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel()));
        }

        @Override
        public ApplicationState nextState() {
            return THAW_BALANCE;
        }
    }

    static class ThawBalanceHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return SHIPMENT_LABEL;
        }

        @Override
        public ApplicationState quoState() {
            return THAW_BALANCE;
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
            DelOutboundOperationLogEnum.SMT_THAW_BALANCE.listener(delOutbound);
            // 取消冻结费用
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
            cusFreezeBalanceDTO.setOrderType("Freight");
            // 调用冻结费用接口
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> thawBalanceR = rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
            if (null == thawBalanceR) {
                throw new CommonException("400", "取消冻结费用失败");
            }
            if (Constants.SUCCESS != thawBalanceR.getCode()) {
                // 异常信息
                String msg = thawBalanceR.getMsg();
                if (StringUtils.isEmpty(msg)) {
                    msg = "取消冻结费用失败";
                }
                throw new CommonException("400", msg);
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
        public ApplicationState preState() {
            return THAW_BALANCE;
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
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            // 获取运费信息
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.PACKAGE);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("400", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    // 包裹信息
                    Packing packing = packageInfo.getPacking();
                    delOutbound.setLength(Utils.valueOf(packing.getLength()));
                    delOutbound.setWidth(Utils.valueOf(packing.getWidth()));
                    delOutbound.setHeight(Utils.valueOf(packing.getHeight()));
                    // 费用信息
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
                    DelOutboundOperationLogEnum.SMT_PRC_PRICING.listener(delOutbound);
                } else {
                    // 计算失败
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                    throw new CommonException("400", exceptionMessage);
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
            DelOutboundOperationLogEnum.SMT_FREEZE_BALANCE.listener(delOutbound);
            // 冻结费用
            CusFreezeBalanceDTO cusFreezeBalanceDTO2 = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO2.setAmount(delOutbound.getAmount());
            cusFreezeBalanceDTO2.setCurrencyCode(delOutbound.getCurrencyCode());
            cusFreezeBalanceDTO2.setCusCode(delOutbound.getSellerCode());
            cusFreezeBalanceDTO2.setNo(delOutbound.getOrderNo());
            cusFreezeBalanceDTO2.setOrderType("Freight");
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO2);
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
        public ApplicationState nextState() {
            return SHIPMENT_SHIPPING;
        }
    }

    static class FreezeInventoryHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_SHIPPING;
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
            // 只有转运出库，集运出库业务才可以处理
            if (DelOutboundServiceImplUtil.noOperationInventory(orderType)) {
                return;
            }
            List<DelOutboundDetail> details = delOutboundWrapperContext.getDetailList();
            if (CollectionUtils.isEmpty(details)) {
                return;
            }
            InventoryOperateListDto operateListDto = new InventoryOperateListDto();
            operateListDto.setInvoiceNo(delOutbound.getOrderNo());
            operateListDto.setWarehouseCode(delOutbound.getWarehouseCode());
            Map<String, InventoryOperateDto> inventoryOperateDtoMap = new HashMap<>();
            for (DelOutboundDetail detail : details) {
                DelOutboundServiceImplUtil.handlerInventoryOperate(detail, inventoryOperateDtoMap);
            }
            Collection<InventoryOperateDto> inventoryOperateDtos = inventoryOperateDtoMap.values();
            ArrayList<InventoryOperateDto> operateList = new ArrayList<>(inventoryOperateDtos);
            operateListDto.setOperateList(operateList);
            // 集运出库特殊处理
            if (DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType)) {
                operateListDto.setFreeType(1);
                operateListDto.setCusCode(delOutbound.getSellerCode());
            }
            try {
                DelOutboundOperationLogEnum.BRV_FREEZE_INVENTORY.listener(new Object[]{delOutbound, operateList});
                InventoryFeignClientService inventoryFeignClientService = SpringUtils.getBean(InventoryFeignClientService.class);
                inventoryFeignClientService.freeze(operateListDto);
            } catch (CommonException e) {
                logger.error(e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("400", "冻结库存操作失败");
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
            return SHIPMENT_SHIPPING;
        }
    }

    static class ShipmentShippingHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return FREEZE_INVENTORY;
        }

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
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
                shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
            } else {
                shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
            }
            shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
            shipmentUpdateRequestDto.setIsEx(false);
            shipmentUpdateRequestDto.setExType(null);
            shipmentUpdateRequestDto.setExRemark(null);
            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            ResponseVO responseVO = htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
            if (null == responseVO || null == responseVO.getSuccess()) {
                throw new CommonException("400", "更新发货指令失败");
            }
            if (!responseVO.getSuccess()) {
                throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "更新发货指令失败2"));
            }
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setShipmentState(END.name());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setOrderNo(delOutbound.getOrderNo());
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
            delOutboundService.shipmentSuccess(updateDelOutbound);
            delOutbound.setExceptionState(DelOutboundExceptionStateEnum.NORMAL.getCode());
            DelOutboundOperationLogEnum.SMT_SHIPMENT_SHIPPING.listener(delOutbound);
        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }

    static class EndHandle extends BringVerifyEnum.CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_SHIPPING;
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
