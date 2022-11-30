package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.feign.BasMeteringConfigFeignService;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.MessageUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundChargeData;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.util.BigDecimalUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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



    // #5 取消冻结费用
    THAW_BALANCE,

    // #6 PRC计费
    PRC_PRICING,

    // #7 冻结费用
    FREEZE_BALANCE,

    // #8 冻结库存（转运出库，集运出库）
    FREEZE_INVENTORY,


    // #1 创建承运商物流订单
    SHIPMENT_ORDER,

    // #2 更新挂号
    SHIPMENT_TRACKING,
    // #3 获取标签
    LABEL,

    // #4 更新标签
    SHIPMENT_LABEL,

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
                throw new CommonException("400", MessageUtil.to("不存在的类型[" + delOutbound.getOrderType() + "]", "Non-existent type ["+delOutbound. getOrderType()+"]"));
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
            String exceptionMessage = Utils.defaultValue(throwable.getMessage(), MessageUtil.to("出库操作失败", "Failed to issue"));
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            updateDelOutbound.setExceptionMessage(exceptionMessage);
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
            // PRC计费
            updateDelOutbound.setLength(delOutbound.getLength());
            updateDelOutbound.setWidth(delOutbound.getWidth());
            updateDelOutbound.setHeight(delOutbound.getHeight());
            updateDelOutbound.setSupplierCalcType(delOutbound.getSupplierCalcType());
            updateDelOutbound.setSupplierCalcId(delOutbound.getSupplierCalcId());

//            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());

            // 规格，长*宽*高
            updateDelOutbound.setSpecifications(delOutbound.getLength() + "*" + delOutbound.getWidth() + "*" + delOutbound.getHeight());
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());

            updateDelOutbound.setCurrencyDescribe(delOutbound.getCurrencyDescribe());
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
          if (delOutboundWrapperContext.isShipmentShipping() && !DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                String shipmentRule;
                if (StringUtils.isNotEmpty(delOutbound.getProductShipmentRule())) {
                    shipmentRule = delOutbound.getProductShipmentRule();
                } else {
                    shipmentRule = delOutbound.getShipmentRule();
                }
                shipmentUpdateRequestDto.setShipmentRule(shipmentRule);
                shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                shipmentUpdateRequestDto.setIsEx(true);
                shipmentUpdateRequestDto.setExType(exType);

                String  exRemark = throwable.getMessage();
                exRemark = ProblemDetails.getErrorMessageOrNullFormat(exRemark);
                logger.info("单号{}，shipmentShipping异常信息{}", delOutbound.getOrderNo(), exRemark);

                shipmentUpdateRequestDto.setExRemark(Utils.defaultValue(exRemark, "操作失败"));
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
            return THAW_BALANCE;
        }
    }

    static class ShipmentOrderHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return FREEZE_BALANCE;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_ORDER;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();


            DelOutbound oldDelOutbound = SpringUtils.getBean(IDelOutboundService.class).getById(delOutbound.getId());

            //8431 提审订单时，如果返回的物流服务是空的，跳过调用供应商
            if(StringUtils.isEmpty(delOutbound.getShipmentService())){
                return;
            }

            logger.info(">>>>>{}-核重时创建承运商订单{}===={}", oldDelOutbound.getShipmentService(), delOutbound.getShipmentService());
            //新的PRC发货服务和老的PRC服务一致并且订单挂号获取方式是“下单后获取”，就不跑供应商系统
            if(StringUtils.equals(oldDelOutbound.getShipmentService(), delOutbound.getShipmentService())){
                if(!DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType())){
                    return;
                }
            }

            IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

            // 创建承运商物流订单
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            logger.info(">>>>>{}-开始创建承运商订单", delOutbound.getOrderNo());
            ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
            if(shipmentOrderResult != null){
                delOutbound.setTrackingNo(shipmentOrderResult.getMainTrackingNumber());
                delOutbound.setShipmentOrderNumber(shipmentOrderResult.getOrderNumber());
                delOutbound.setShipmentOrderLabelUrl(shipmentOrderResult.getOrderLabelUrl());
                delOutbound.setReferenceNumber(shipmentOrderResult.getReferenceNumber());
            }

            DelOutbound delOutboundUpd = new DelOutbound();
            delOutboundUpd.setId(delOutbound.getId());
            delOutboundUpd.setReferenceNumber(delOutbound.getReferenceNumber());
            iDelOutboundService.updateById(delOutboundUpd);

            logger.info(">>>>>{}-承运商订单创建完成", delOutbound.getOrderNo());
            DelOutboundOperationLogEnum.SMT_SHIPMENT_ORDER.listener(delOutbound);


            //更新PRC发货服务
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setProductShipmentRule(delOutbound.getProductShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            delOutboundService.updateByIdTransactional(updateDelOutbound);

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
            //核重直接跑
            if(delOutboundWrapperContext.getExecShipmentShipping()){
                return true;
            }
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
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                return;
            }
            if(StringUtils.isEmpty(delOutbound.getTrackingNo())){
                return;
            }
            DelOutboundOperationLogEnum.SMT_SHIPMENT_TRACKING.listener(delOutbound);


            // 更新WMS挂号
            ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
            shipmentTrackingChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
            shipmentTrackingChangeRequestDto.setTrackingNo(delOutbound.getTrackingNo());
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            ResponseVO responseVO = htpOutboundClientService.shipmentTracking(shipmentTrackingChangeRequestDto);
            if (null == responseVO || null == responseVO.getSuccess()) {
                throw new CommonException("400", MessageUtil.to("更新挂号失败，请求无响应", "Failed to update registration, no response to the request"));
            }
            if (!responseVO.getSuccess()) {
                throw new CommonException("400", MessageUtil.to("更新挂号失败，", "Failed to update registration,")
                        + Utils.defaultValue(responseVO.getMessage(), ""));
            }
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();

            //核重直接跑
            if(delOutboundWrapperContext.getExecShipmentShipping()){
                return true;
            }
            
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
            // 修改为异步执行
//            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
//            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
//            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
//            delOutboundBringVerifyService.getShipmentLabel(delOutbound);
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
            // 修改为异步执行
            /*DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
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
            }*/
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
            return SHIPMENT_SHIPPING;
        }
    }

    static class ThawBalanceHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return BEGIN;
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

            logger.info("出库单{}-开始取消冻结费用：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));

            IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
            RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
            List<DelOutboundCharge> delOutboundChargeList = delOutboundChargeService.listCharges(delOutbound.getOrderNo());
            Map<String, List<DelOutboundCharge>> groupByCharge =
                    delOutboundChargeList.stream().collect(Collectors.groupingBy(DelOutboundCharge::getCurrencyCode));
            for (String currencyCode: groupByCharge.keySet()) {
                BigDecimal bigDecimal = new BigDecimal(0);
                for (DelOutboundCharge c : groupByCharge.get(currencyCode)) {
                    if (c.getAmount() != null) {
                        bigDecimal = bigDecimal.add(c.getAmount());
                    }
                }

                // 取消冻结费用
                CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                cusFreezeBalanceDTO.setAmount(bigDecimal);
                cusFreezeBalanceDTO.setCurrencyCode(currencyCode);
                cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
                cusFreezeBalanceDTO.setOrderType("Freight");
                // 调用冻结费用接口
                R<?> thawBalanceR = rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
                if (null == thawBalanceR) {
                    throw new CommonException("400", MessageUtil.to("取消冻结费用失败", "Failed to cancel freezing expenses"));
                }
                if (Constants.SUCCESS != thawBalanceR.getCode()) {
                    // 异常信息
                    String msg = thawBalanceR.getMsg();
                    if (StringUtils.isEmpty(msg)) {
                        msg = MessageUtil.to("取消冻结费用失败", "Failed to cancel freezing expenses");
                    }
                    throw new CommonException("400", msg);
                }

            }



            // 清除费用信息
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


            logger.info("计算包裹运费开始,单号：{}",delOutbound.getOrderNo());
            // 获取运费信息
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.PACKAGE);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("400", MessageUtil.to("计算包裹费用失败", "Failed to calculate the package fee"));
            }

            if (!responseObject.isSuccess()) {
                // 计算失败
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                throw new CommonException("400", exceptionMessage);
            }

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
            delOutbound.setSupplierCalcType(data.getSupplierCalcType());
            delOutbound.setSupplierCalcId(data.getSupplierCalcId());

            delOutbound.setShipmentService(data.getLogisticsRouteId());
            delOutbound.setPackingRule(data.getPackingRule());
            delOutbound.setShipmentRule(data.getShipmentRule());
            delOutbound.setProductShipmentRule(data.getShipmentRule());
            delOutbound.setLogisticsProviderCode(data.getLogisticsProviderCode());

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

            //判断计泡拦截
            BasMeteringConfigFeignService basMeteringConfigFeignService = SpringUtils.getBean(BasMeteringConfigFeignService.class);

            BasMeteringConfigDto dto = new BasMeteringConfigDto()
                    .setOrderType(delOutbound.getOrderType())
                    .setCustomerCode(delOutbound.getSellerCode())
                    .setLogisticsErvicesCode(chargeWrapper.getData().getProductCode())
                    .setLogisticsErvicesName(chargeWrapper.getData().getProductName())
                    .setCustomerCode(delOutbound.getSellerCode())
                    //下单重量
                    .setWeight(delOutbound.getForecastWeight() != null ? new BigDecimal(delOutbound.getForecastWeight()) : BigDecimal.ZERO)
                    //PRC返回的计费重
                    .setCalcWeight(packageInfo.getCalcWeight().getValue())
                    //仓库返回的实重
                    .setVolume(delOutbound.getWeight() != null ? new BigDecimal(delOutbound.getWeight()): null);

            if(delOutboundWrapperContext.getAddress() != null){
                dto.setCountryCode(delOutboundWrapperContext.getAddress().getCountryCode())
                        .setCountryName(delOutboundWrapperContext.getAddress().getCountry());
            }
            logger.info("出库单{}计泡拦截参数{}", delOutbound.getOrderNo(), JSONUtil.toJsonStr(dto));
            R r = basMeteringConfigFeignService.intercept(dto);

            if(r.getCode() != 200){
                logger.error("计泡拦截异常："+delOutboundWrapperContext.isShipmentShipping());
                delOutboundWrapperContext.setShipmentShipping(true);
                throw new CommonException("400", MessageUtil.to("计泡拦截异常", "Abnormal bubble counting interception")+"："+r.getMsg());
            }

            IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
            try {
                logger.info("开始保存出库单费用明细，数据条数：{}", delOutboundCharges.size());
                delOutboundChargeService.saveCharges(delOutboundCharges);
                logger.info("保存出库单费用明细完成");
            } catch (Exception e) {
                logger.info("保存出库单费用明细失败，{}", e.getMessage());
                throw e;
            }
            delOutbound.setAmount(totalAmount);
            delOutbound.setCurrencyCode(totalCurrencyCode);
            //分组计算货币金额
            Map<String, BigDecimal> currencyMap = new HashMap<String, BigDecimal>();

            for (DelOutboundCharge charge: delOutboundCharges){

                String currencyCode = charge.getCurrencyCode();
                BigDecimal amount = BigDecimalUtil.setScale(charge.getAmount(),3);

                if(currencyMap.containsKey(currencyCode)){
                    BigDecimal chargeamount = currencyMap.get(currencyCode).add(amount);
                    currencyMap.put(currencyCode, chargeamount);
                }else{
                    currencyMap.put(currencyCode, amount);
                }
            }

            String currencyDescribe = ArrayUtil.join(currencyMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(e -> e.getValue() + e.getKey()).collect(Collectors.toList()).toArray(), "；");

            delOutbound.setCurrencyDescribe(currencyDescribe);

            /**
             * 特殊化日志记录，分币别
             */
            DelOutboundChargeData delOutboundChargeData = new DelOutboundChargeData();
            BeanUtil.copyProperties(delOutbound, delOutboundChargeData);
            delOutboundChargeData.setDelOutboundCharges(delOutboundCharges);
            DelOutboundOperationLogEnum.SMT_PRC_PRICING.listener(delOutboundChargeData);
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

        public static final TimeUnit unit = TimeUnit.SECONDS;

        public static final long time = 3L;

        @Override
        public void handle(ApplicationContext context) {


            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.SMT_FREEZE_BALANCE.listener(delOutbound);

            logger.info(">>>>>[发货后出库单{}]开始冻结费用, 数据:{}",delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));

//            RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);
//
//            String key = "deloutbound-fss-freeze-balance" + delOutbound.getCustomCode() + ":" + delOutbound.getOrderNo();
//
//            RLock lock = redissonClient.getLock(key);
//
            try {
//                lock.tryLock(time, unit);

                /**
                 *  获取要冻结的费用数据，并按货币分组冻结
                 */
                IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
                RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
                List<DelOutboundCharge> delOutboundChargeList = delOutboundChargeService.listCharges(delOutbound.getOrderNo());
                if(delOutboundChargeList.isEmpty()){
                    throw new CommonException("400", MessageUtil.to("冻结费用信息失败，没有要冻结的费用明细", "Failed to freeze expense information. No expense details to be frozen"));
                }
                Map<String, List<DelOutboundCharge>> groupByCharge = delOutboundChargeList.stream().collect(Collectors.groupingBy(DelOutboundCharge::getCurrencyCode));
                for (String currencyCode: groupByCharge.keySet()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    List<DelOutboundCharge> delOutboundCharges = groupByCharge.get(currencyCode);
                    for (DelOutboundCharge c : delOutboundCharges) {
                        if (c.getAmount() != null) {
                            amount = amount.add(c.getAmount());
                        }
                    }
                    // 冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO2 = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO2.setAmount(amount);
                    cusFreezeBalanceDTO2.setCurrencyCode(currencyCode);
                    cusFreezeBalanceDTO2.setCusCode(delOutbound.getSellerCode());
                    cusFreezeBalanceDTO2.setNo(delOutbound.getOrderNo());
                    cusFreezeBalanceDTO2.setOrderType("Freight");
                    R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO2);

                    if (null == freezeBalanceR) {
                        throw new CommonException("400", "冻结费用失败，请求无响应");
                    }

                    if (Constants.SUCCESS != freezeBalanceR.getCode()) {
                        // 异常信息
                        String msg = Utils.defaultValue(freezeBalanceR.getMsg(), "");
                        throw new CommonException("400", MessageUtil.to("冻结费用失败，", "Failed to freeze expenses,") + msg);
                    }

                    logger.info(">>>>>[发货后出库单{}]冻结费用, 数据:{}",delOutbound.getOrderNo(), JSONObject.toJSONString(cusFreezeBalanceDTO2));
                }

            } catch (Exception e) {

                logger.info("冻结费用异常，加锁失败");
                logger.info("异常信息:" + e.getMessage());

                throw new RuntimeException(e.getMessage());

            }finally {
            }
        }

        @Override
        public ApplicationState nextState() {
            return SHIPMENT_ORDER;
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
                throw new CommonException("400", MessageUtil.to("冻结库存操作失败，" + e.getMessage(), "Failed to freeze the inventory,"+e.getMessage()));
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
            return SHIPMENT_ORDER;
        }
    }

    static class ShipmentShippingHandle extends CommonApplicationHandle {
        @Override
        public ApplicationState preState() {
            return SHIPMENT_ORDER;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_SHIPPING;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();


            boolean bool = false;
            if(delOutboundWrapperContext.getExecShipmentShipping() && !DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())){
                logger.info("核重更新发货指令{}", delOutbound.getOrderNo());
                // 修改为异步执行
                ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
                    shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
                } else {
                    String shipmentRule;
                    if (StringUtils.isNotEmpty(delOutbound.getProductShipmentRule())) {
                        shipmentRule = delOutbound.getProductShipmentRule();
                    } else {
                        shipmentRule = delOutbound.getShipmentRule();
                    }
                    shipmentUpdateRequestDto.setShipmentRule(shipmentRule);
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
                bool = true;
            }

            // 修改出库单相关信息
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setShipmentState(END.name());

            updateDelOutbound.setLogisticsProviderCode(delOutbound.getLogisticsProviderCode());


            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
            // PRC计费
            updateDelOutbound.setLength(delOutbound.getLength());
            updateDelOutbound.setWidth(delOutbound.getWidth());
            updateDelOutbound.setHeight(delOutbound.getHeight());
            updateDelOutbound.setSupplierCalcType(delOutbound.getSupplierCalcType());
            updateDelOutbound.setSupplierCalcId(delOutbound.getSupplierCalcId());
            // 规格，长*宽*高
            updateDelOutbound.setSpecifications(delOutbound.getLength() + "*" + delOutbound.getWidth() + "*" + delOutbound.getHeight());
            updateDelOutbound.setCalcWeight(delOutbound.getCalcWeight());
            updateDelOutbound.setCalcWeightUnit(delOutbound.getCalcWeightUnit());
            updateDelOutbound.setAmount(delOutbound.getAmount());
            updateDelOutbound.setCurrencyCode(delOutbound.getCurrencyCode());
            updateDelOutbound.setCurrencyDescribe(delOutbound.getCurrencyDescribe());

            if(delOutboundWrapperContext.getExecShipmentShipping()) {
                updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
//                updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
//                updateDelOutbound.setShipmentRule(delOutbound.getShipmentRule());
            }



            delOutboundService.shipmentSuccess(updateDelOutbound);
            // 提交一个获取标签的任务
            IDelOutboundRetryLabelService delOutboundRetryLabelService = SpringUtils.getBean(IDelOutboundRetryLabelService.class);


            String productCode = delOutbound.getShipmentRule();
            String prcProductCode = delOutboundWrapperContext.getPrcProductCode();
            if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(prcProductCode)) {
                productCode = prcProductCode;
            }
            // 查询发货条件

            /*boolean shippingConditions = false;
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(delOutbound.getWarehouseCode())
                    && org.apache.commons.lang3.StringUtils.isNotEmpty(productCode)) {

                PackageDeliveryConditions packageDeliveryConditions = new PackageDeliveryConditions();
                packageDeliveryConditions.setWarehouseCode(delOutbound.getWarehouseCode());
                packageDeliveryConditions.setProductCode(productCode);
                PackageDeliveryConditionsFeignService packageDeliveryConditionsFeignService = SpringUtils.getBean(PackageDeliveryConditionsFeignService.class);
                R<PackageDeliveryConditions> packageDeliveryConditionsR = packageDeliveryConditionsFeignService.info(packageDeliveryConditions);
                PackageDeliveryConditions packageDeliveryConditionsRData = null;
                if (null != packageDeliveryConditionsR && Constants.SUCCESS == packageDeliveryConditionsR.getCode()) {
                    packageDeliveryConditionsRData = packageDeliveryConditionsR.getData();
                }
                if (null != packageDeliveryConditionsRData && "AfterMeasured".equals(packageDeliveryConditionsRData.getCommandNodeCode())) {
                    //出库测量后接收发货指令 就不调用标签接口
                    shippingConditions = true;
                    return;
                }
            }*/
            /*if(shippingConditions){
                if(bool){
                    //只推送发货指令
                    IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                    delOutboundBringVerifyService.shipmentShipping(delOutbound);
                }
            }else{*/
                if(bool){
                    //只推送标签，不执行发货指令
                    delOutboundRetryLabelService.saveAndPushLabel(delOutbound.getOrderNo(), "pushLabel", null);
                }else{
                    //只推送标签， 推送发货指令
                    delOutboundRetryLabelService.saveAndPushLabel(delOutbound.getOrderNo(), null, null);
                }
//            }



            // 记录日志
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
