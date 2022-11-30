package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.szmsd.common.core.utils.MessageUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.BasShipmentRulesDto;
import com.szmsd.delivery.dto.DelOutboundLabelDto;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.util.BigDecimalUtil;
import com.szmsd.delivery.util.PdfUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.ChargeCategory;
import com.szmsd.http.dto.ChargeItem;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.Money;
import com.szmsd.http.dto.Packing;
import com.szmsd.http.dto.PricingPackageInfo;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentChargeInfo;
import com.szmsd.http.dto.ShipmentLabelChangeRequestDto;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.dto.TaskConfigInfo;
import com.szmsd.http.dto.Weight;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.pack.api.feign.PackageDeliveryConditionsFeignService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    //# 1 IOSS 检查     - 有回滚
    IOSS_CHECK,

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
        map.put(IOSS_CHECK.name(), new IossChekcHandle());
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
                throw new CommonException("400", MessageUtil.to("不存在的类型[" + delOutbound.getOrderType() + "]", "Non-existent type ["+delOutbound. getOrderType()+"]"));
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
            // 提审失败
            String exceptionMessage = Utils.defaultValue(throwable.getMessage(), MessageUtil.to("提审操作失败", "Review operation failed"));

            if(BringVerifyEnum.SHIPMENT_CREATE.equals(currentState) || BringVerifyEnum.SHIPMENT_LABEL.equals(currentState)){
                // 推单WMS
                if(delOutbound.getRefOrderNo() == null){
                    delOutbound.setRefOrderNo("");
                }
                updateDelOutbound.setExceptionStateWms(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
                updateDelOutbound.setExceptionMessageWms(exceptionMessage);
                updateDelOutbound.setRefOrderNo(delOutbound.getRefOrderNo());
                delOutboundService.updateByIdTransactional(updateDelOutbound);
                return;
            }


            if(currentState.name().equals(SHIPMENT_ORDER.name())){
                //如果是供应商报错，从新开始执行
                updateDelOutbound.setBringVerifyState(BEGIN.name());
            }else if(currentState.name().equals(FREEZE_OPERATION.name())){
                updateDelOutbound.setBringVerifyState(FREEZE_INVENTORY.name());
            }else{
                updateDelOutbound.setBringVerifyState(currentState.name());
            }


            updateDelOutbound.setExceptionMessage(exceptionMessage);
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
            // 产品信息
            updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
            updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
            updateDelOutbound.setLogisticsProviderCode(delOutbound.getLogisticsProviderCode());
            updateDelOutbound.setProductShipmentRule(delOutbound.getProductShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
            updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
            updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());

            updateDelOutbound.setCurrencyDescribe(delOutbound.getCurrencyDescribe());


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
            StopWatch stopWatch = new StopWatch();

            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            logger.info(">>>>>[创建出库单{}]-开始执行Pricing", delOutbound.getOrderNo());

            PricingEnum pricingEnum;
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                // 核重逻辑处理
                pricingEnum = PricingEnum.PACKAGE;
            } else {
                pricingEnum = PricingEnum.SKU;
            }
            stopWatch.start();
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, pricingEnum);
            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]-Pricing计算返回结果：耗时{}, 内容:{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis(),
                    JSONObject.toJSONString(responseObject));
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("400", MessageUtil.to("计算包裹费用失败", "Failed to calculate the package fee"));
            }

            if (!responseObject.isSuccess()) {
                // 计算失败
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), MessageUtil.to("计算包裹费用失败", "Failed to calculate the package fee")+ "2");
                throw new CommonException("400", exceptionMessage);
            }

            // 计算成功了
            ChargeWrapper chargeWrapper = responseObject.getObject();
            ShipmentChargeInfo data = chargeWrapper.getData();
            PricingPackageInfo packageInfo = data.getPackageInfo();
            // 挂号服务
            delOutbound.setShipmentService(data.getLogisticsRouteId());
            // 物流商code
            delOutbound.setLogisticsProviderCode(data.getLogisticsProviderCode());
            // 发货规则，装箱规则
            delOutbound.setProductShipmentRule(data.getShipmentRule());
            delOutbound.setPackingRule(data.getPackingRule());

            // 临时传值
            delOutboundWrapperContext.setPrcProductCode(data.getProductCode());
            logger.info("记录临时传值字段，prcProductCode：{}", data.getProductCode());
            // 包裹信息
            Packing packing = packageInfo.getPacking();
            delOutbound.setLength(Utils.valueOf(packing.getLength()));
            delOutbound.setWidth(Utils.valueOf(packing.getWidth()));
            delOutbound.setHeight(Utils.valueOf(packing.getHeight()));
            delOutbound.setSupplierCalcType(data.getSupplierCalcType());
            delOutbound.setSupplierCalcId(data.getSupplierCalcId());

            if(StringUtils.isNotBlank(data.getAmazonLogisticsRouteId())){
                delOutbound.setAmazonLogisticsRouteId(data.getAmazonLogisticsRouteId());
            }
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
            stopWatch.start();
            delOutboundChargeService.saveCharges(delOutboundCharges);
            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]-Pricing保存出库单费用信息：耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());
            // 更新值
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


            //更新PRC发货服务
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setProductShipmentRule(data.getShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            delOutboundService.updateByIdTransactional(updateDelOutbound);

            DelOutboundOperationLogEnum.BRV_PRC_PRICING.listener(delOutbound);
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
            updateDelOutbound.setCurrencyDescribe("");

            updateDelOutbound.setSupplierCalcType("");
            updateDelOutbound.setSupplierCalcId("");
            // 产品信息
            updateDelOutbound.setTrackingAcquireType("");
            updateDelOutbound.setShipmentService("");
            updateDelOutbound.setLogisticsProviderCode("");
            updateDelOutbound.setProductShipmentRule("");
            updateDelOutbound.setPackingRule("");
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo("");
            updateDelOutbound.setShipmentOrderNumber("");
            updateDelOutbound.setShipmentOrderLabelUrl("");
            // 推单WMS
            updateDelOutbound.setRefOrderNo("");

            // 提审失败
            updateDelOutbound.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode());
            delOutboundService.updateById(updateDelOutbound);
            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return IOSS_CHECK;
        }
    }

    static class IossChekcHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return PRC_PRICING;
        }

        @Override
        public ApplicationState quoState() {
            return IOSS_CHECK;
        }

        @Override
        public boolean otherCondition(ApplicationContext context, ApplicationState currentState) {
            //批量出库->自提出库 不做prc
            return super.batchSelfPick(context, currentState);
        }

        @Override
        public void handle(ApplicationContext context) {
            StopWatch stopWatch = new StopWatch();

            //IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            logger.info(">>>>>[创建出库单{}]-开始执行IOSS_CHECK", delOutbound.getOrderNo());

            BasShipmenRulesService basShipmenRulesService = SpringUtils.getBean(BasShipmenRulesService.class);

            String customCode = delOutbound.getSellerCode();
            String shipmentService = delOutbound.getShipmentService();
            String ioss = delOutbound.getIoss();

            stopWatch.start();

            BasShipmentRulesDto basShipmentRulesDto = new BasShipmentRulesDto();
            basShipmentRulesDto.setCustomCode(customCode);
            basShipmentRulesDto.setServiceChannelName(shipmentService);

            logger.info(">>>>>[创建出库单{}]-IOSS_CHECK参数：耗时{}",delOutbound.getOrderNo(), JSON.toJSONString(basShipmentRulesDto));

            boolean checkIoss = basShipmenRulesService.selectbasShipmentRules(basShipmentRulesDto);

            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]-IOSS_CHECK计算返回结果：{}", delOutbound.getOrderNo(), checkIoss);

            if (checkIoss) {
                if(StringUtils.isEmpty(ioss)) {

                    String exceptionMessage = "["+delOutbound.getOrderNo()+"]单号，此物流服务需要必填IOSS";
                    throw new CommonException("400", exceptionMessage);
                }
            }

            //DelOutboundOperationLogEnum.BRV_PRC_PRICING.listener(delOutbound);
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
            updateDelOutbound.setCurrencyDescribe("");

            updateDelOutbound.setSupplierCalcType("");
            updateDelOutbound.setSupplierCalcId("");
            // 产品信息
            updateDelOutbound.setTrackingAcquireType("");
            updateDelOutbound.setShipmentService("");
            updateDelOutbound.setLogisticsProviderCode("");
            updateDelOutbound.setProductShipmentRule("");
            updateDelOutbound.setPackingRule("");
            // 创建承运商物流订单
            updateDelOutbound.setTrackingNo("");
            updateDelOutbound.setShipmentOrderNumber("");
            updateDelOutbound.setShipmentOrderLabelUrl("");
            // 推单WMS
            updateDelOutbound.setRefOrderNo("");

            // 提审失败
            updateDelOutbound.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode());
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
            return IOSS_CHECK;
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

            StopWatch stopWatch = new StopWatch();

            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            logger.info("出库单{}-开始冻结费用：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
            DelOutboundOperationLogEnum.BRV_FREEZE_BALANCE.listener(delOutbound);

            //RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);

//            String key = "bringVerify-fss-freeze-balance" + delOutbound.getCustomCode() + ":" + delOutbound.getOrderNo();
//
//            RLock lock = redissonClient.getLock(key);
//
            try {
//
//                lock.tryLock(time, unit);

                /**
                 *  获取要冻结的费用数据，并按货币分组冻结
                 */
                IDelOutboundChargeService delOutboundChargeService = SpringUtils.getBean(IDelOutboundChargeService.class);
                RechargesFeignService rechargesFeignService = SpringUtils.getBean(RechargesFeignService.class);
                List<DelOutboundCharge> delOutboundChargeList = delOutboundChargeService.listCharges(delOutbound.getOrderNo());
                if (delOutboundChargeList.isEmpty()) {
                    throw new CommonException("400", MessageUtil.to("冻结费用信息失败，没有要冻结的费用明细", "Failed to freeze expense information. No expense details to be frozen"));
                }
                Map<String, List<DelOutboundCharge>> groupByCharge =
                        delOutboundChargeList.stream().collect(Collectors.groupingBy(DelOutboundCharge::getCurrencyCode));

                logger.info(">>>>>[创建出库单{}]冻结费用map, 数据:{}",delOutbound.getOrderNo(), JSONObject.toJSONString(groupByCharge));

                for (String currencyCode : groupByCharge.keySet()) {
                    BigDecimal bigDecimal = BigDecimal.ZERO;
                    for (DelOutboundCharge c : groupByCharge.get(currencyCode)) {
                        if (c.getAmount() != null) {
                            bigDecimal = bigDecimal.add(c.getAmount());
                        }
                    }
                    // 调用冻结费用接口
                    CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO.setAmount(bigDecimal);
                    cusFreezeBalanceDTO.setCurrencyCode(currencyCode);
                    cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                    cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
                    cusFreezeBalanceDTO.setOrderType("Freight");

                    //logger.info(">>>>>[创建出库单{}]冻结费用freezeBalance, 数据:{}",delOutbound.getOrderNo(), JSONObject.toJSONString(groupByCharge));

                    stopWatch.start();
                    R<?> freezeBalanceR = rechargesFeignService.freezeBalance(cusFreezeBalanceDTO);
                    stopWatch.stop();

                    if (null == freezeBalanceR) {
                        throw new CommonException("400", MessageUtil.to("冻结费用信息失败", "Failed to freeze expense information"));
                    }

                    if (Constants.SUCCESS != freezeBalanceR.getCode()) {
                        // 异常信息
                        String msg = Utils.defaultValue(freezeBalanceR.getMsg(), MessageUtil.to("冻结费用信息失败", "Failed to freeze expense information") + "2");
                        throw new CommonException("400", msg);
                    }

                    logger.info(">>>>>[创建出库单{}]结束冻结费用, 数据:{} ,耗时{}", delOutbound.getOrderNo(), JSONObject.toJSONString(cusFreezeBalanceDTO), stopWatch.getLastTaskInfo().getTimeMillis());
                }

            }catch (Exception e){
                logger.info("冻结费用异常，加锁失败");
                logger.info("异常信息:" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }finally {
//                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
//                    lock.unlock();
//                }
            }

        }

        @Override
        public void rollback(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOperationLogEnum.RK_BRV_FREEZE_BALANCE.listener(delOutbound);


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
                CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                cusFreezeBalanceDTO.setAmount(bigDecimal);
                cusFreezeBalanceDTO.setCurrencyCode(currencyCode);
                cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                cusFreezeBalanceDTO.setNo(delOutbound.getOrderNo());
                cusFreezeBalanceDTO.setOrderType("Freight");
                R<?> thawBalanceR = rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
                logger.info(">>>>>[创建出库单{}]取消冻结费用, 数据:{}",
                        delOutbound.getOrderNo(), JSONObject.toJSONString(cusFreezeBalanceDTO));
                if (null == thawBalanceR) {
                    throw new CommonException("400", MessageUtil.to("取消冻结费用失败", "Failed to cancel freezing expenses"));
                }
                if (Constants.SUCCESS != thawBalanceR.getCode()) {
                    throw new CommonException("400", Utils.defaultValue(thawBalanceR.getMsg(), MessageUtil.to("取消冻结费用失败", "Failed to cancel freezing expenses")+ "2"));
                }
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
            StopWatch stopWatch = new StopWatch();

            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String productCode = delOutbound.getShipmentRule();
            String prcProductCode = delOutboundWrapperContext.getPrcProductCode();
            logger.info("{}-查询产品信息：{}，临时字段：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound), prcProductCode);
            logger.info("获取临时传值字段，prcProductCode：{}", prcProductCode);
            if (StringUtils.isNotEmpty(prcProductCode)) {
                productCode = prcProductCode;
            }
            // 获取产品信息
            IHtpPricedProductClientService htpPricedProductClientService = SpringUtils.getBean(IHtpPricedProductClientService.class);

            stopWatch.start();
            PricedProductInfo pricedProductInfo = htpPricedProductClientService.infoAndSubProducts(productCode);
            stopWatch.stop();

            logger.info(">>>>>[创建出库单{}]获取产品信息 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
            if (null == pricedProductInfo) {
                throw new CommonException("400", MessageUtil.to("查询产品[" + productCode + "]信息失败","Failed to query product ["+productCode+"] information" ));
            }

            // 从PRC返回进行取值，这里作废
            // delOutbound.setShipmentService(pricedProductInfo.getLogisticsRouteId());
            delOutbound.setTrackingAcquireType(pricedProductInfo.getTrackingAcquireType());
            // 这两个字段从PRC返回赋值了
            // delOutbound.setProductShipmentRule(pricedProductInfo.getShipmentRule());
            // delOutbound.setPackingRule(pricedProductInfo.getPackingRule());
            delOutbound.setPrcProductCode(prcProductCode);
            DelOutboundOperationLogEnum.BRV_PRODUCT_INFO.listener(delOutbound);
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
            StopWatch stopWatch = new StopWatch();

            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            logger.info("{}-新增发货规则：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
            IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);

            stopWatch.start();
            delOutboundBringVerifyService.shipmentRule(delOutbound);
            stopWatch.stop();

            logger.info(">>>>>[创建出库单{}]获取发货信息 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

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
            StopWatch stopWatch = new StopWatch();

            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();

            String shipmentService = delOutbound.getShipmentService();
            String trackingAcquireType = delOutbound.getTrackingAcquireType();

            //BasShipmenRulesService basShipmenRulesService = SpringUtils.getBean(BasShipmenRulesService.class);

            //8431 提审订单时，如果返回的物流服务是空的，跳过调用供应商
            if(StringUtils.isEmpty(shipmentService) || trackingAcquireType.equals(DelOutboundTrackingAcquireTypeEnum.NONE.getCode())){
                return;
            }

//            BasShipmentRulesDto paramBasShipmentRulesDto = new BasShipmentRulesDto();
//            paramBasShipmentRulesDto.setCustomCode(delOutbound.getSellerCode());
//            paramBasShipmentRulesDto.setServiceChannelName(shipmentService);
//            List<BasShipmentRules> list = basShipmenRulesService.selectBasShipmentRules(paramBasShipmentRulesDto);
//            if(CollectionUtils.isNotEmpty(list)){
//                return;
//            }

            IDelOutboundService iDelOutboundService = SpringUtils.getBean(IDelOutboundService.class);

            logger.info("{}-创建承运商物流订单：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
            // 判断是否需要创建物流订单
            if (DelOutboundTrackingAcquireTypeEnum.ORDER_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType())) {
                // 创建承运商物流订单
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
                stopWatch.start();
                ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
                stopWatch.stop();
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

                logger.info(">>>>>[创建出库单{}]创建承运商 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

                DelOutboundOperationLogEnum.BRV_SHIPMENT_ORDER.listener(delOutbound);
            }


            if(StringUtils.isNotBlank(delOutbound.getAmazonLogisticsRouteId())){

                // 提交一个亚马逊获取标签的任务
                stopWatch.start();
                IDelOutboundThirdPartyService delOutboundThirdPartyService = SpringUtils.getBean(IDelOutboundThirdPartyService.class);
                DelOutboundThirdParty delOutboundThirdParty = new DelOutboundThirdParty();
                delOutboundThirdParty.setOrderNo(delOutbound.getOrderNo());
                delOutboundThirdParty.setState(DelOutboundCompletedStateEnum.INIT.getCode());
                delOutboundThirdParty.setOperationType(DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_THIRD_PARTY);
                delOutboundThirdParty.setKeyInfo(delOutbound.getAmazonLogisticsRouteId());
                delOutboundThirdPartyService.save(delOutboundThirdParty);
                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]提交一个第三方承运商订单任务,耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());

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
            logger.info("{}-冻结库存：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
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
            logger.info("{}-冻结操作费：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
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
                    throw new CommonException("400", MessageUtil.to("查询SKU信息失败", "Failed to query SKU information"));
                }
                // 处理操作费用参数
                for (DelOutboundDetail detail : details) {
                    String sku = detail.getSku();
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", MessageUtil.to("SKU[" + sku + "]信息不存在", "SKU ["+sku+"] information does not exist"));
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

            StopWatch stopWatch = new StopWatch();

            stopWatch.start();
            OperationFeignService operationFeignService = SpringUtils.getBean(OperationFeignService.class);
            //业务计费 - 出库冻结余额
            R<?> r = operationFeignService.delOutboundFreeze(delOutboundOperationVO);
            stopWatch.stop();

            logger.info(">>>>>[创建出库单{}]冻结操作费用 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

            DelOutboundServiceImplUtil.freezeOperationThrowErrorMessage(r);

                IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);

                DelOutbound updateDelOutbound = new DelOutbound();


                updateDelOutbound.setId(delOutbound.getId());
                updateDelOutbound.setBringVerifyState(END.name());
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

            // 产品信息
                updateDelOutbound.setTrackingAcquireType(delOutbound.getTrackingAcquireType());
                updateDelOutbound.setShipmentService(delOutbound.getShipmentService());
                updateDelOutbound.setLogisticsProviderCode(delOutbound.getLogisticsProviderCode());
                updateDelOutbound.setProductShipmentRule(delOutbound.getProductShipmentRule());
                updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
                // 创建承运商物流订单
                updateDelOutbound.setTrackingNo(delOutbound.getTrackingNo());
                updateDelOutbound.setShipmentOrderNumber(delOutbound.getShipmentOrderNumber());
                updateDelOutbound.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());

                if(!DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())){
                    //判断物流服务是否推送WMS
                    if(delOutboundService.serviceChannelNamePushWMS(delOutbound, updateDelOutbound)){
                        return;
                    }


                    // 提交一个WMS任务
                    stopWatch.start();
                    IDelOutboundThirdPartyService delOutboundThirdPartyService = SpringUtils.getBean(IDelOutboundThirdPartyService.class);
                    DelOutboundThirdParty delOutboundThirdParty = new DelOutboundThirdParty();
                    delOutboundThirdParty.setOrderNo(delOutbound.getOrderNo());
                    delOutboundThirdParty.setState(DelOutboundCompletedStateEnum.INIT.getCode());
                    delOutboundThirdParty.setOperationType(DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_WMS);
                    delOutboundThirdPartyService.save(delOutboundThirdParty);
                    stopWatch.stop();
                    logger.info(">>>>>[创建出库单{}]提交一个WMS订单任务,耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());
                }
            delOutboundService.bringVerifySuccess(updateDelOutbound);



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

            logger.info(">>>>>[创建出库单{}]取消冻结操作费用失败 {}",delOutbound.getOrderNo(),JSON.toJSONString(delOutbound));

            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);

            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());

            updateDelOutbound.setBringVerifyState(FREEZE_INVENTORY.name());

            updateDelOutbound.setState(DelOutboundStateEnum.AUDIT_FAILED.getCode());
            boolean upd  = delOutboundService.updateById(updateDelOutbound);

            logger.info(">>>>>[创建出库单{}]取消冻结操作费用失败状态修改 {}",delOutbound.getOrderNo(),upd);

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            DelOutboundServiceImplUtil.thawOperationThrowCommonException(r);

            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]取消冻结操作费用 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

            super.rollback(context);
        }

        @Override
        public ApplicationState nextState() {
            return END;
        }
    }

    static class ShipmentCreateHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return SHIPMENT_CREATE;
        }

        @Override
        public ApplicationState quoState() {
            return SHIPMENT_CREATE;
        }

        @Override
        public void handle(ApplicationContext context) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            String refOrderNo = "";
            logger.info("{}-推单到WMS：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
            // 推单到WMS
            // 重派出库单不扣库存
            if (!DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                refOrderNo = delOutboundBringVerifyService.shipmentCreate(delOutboundWrapperContext, delOutbound.getTrackingNo());

                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]推单WMS 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());


                delOutbound.setRefOrderNo(refOrderNo);
                DelOutboundOperationLogEnum.BRV_SHIPMENT_CREATE.listener(delOutbound);
            }

            // 保存信息
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            // 推单WMS
            updateDelOutbound.setRefOrderNo(refOrderNo);
            updateDelOutbound.setExceptionStateWms(DelOutboundExceptionStateEnum.NORMAL.getCode());
            updateDelOutbound.setExceptionMessageWms("");
            delOutboundService.updateByIdTransactional(updateDelOutbound);

            // 处理发货条件
            TaskConfigInfo taskConfigInfo = delOutboundWrapperContext.getTaskConfigInfo();
            if (null != taskConfigInfo) {
                if ("NotReceive".equals(taskConfigInfo.getReceiveShippingType())) {
                    // 增加出库单已取消记录，异步处理，定时任务
                    IDelOutboundCompletedService delOutboundCompletedService = SpringUtils.getBean(IDelOutboundCompletedService.class);
                    delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
                }
            }
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

            String productCode = delOutbound.getShipmentRule();
            String prcProductCode = delOutboundWrapperContext.getPrcProductCode();
            if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(prcProductCode)) {
                productCode = prcProductCode;
            }
            // 查询发货条件
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
                    return;
                }
            }
            DelOutboundOperationLogEnum.BRV_SHIPMENT_LABEL.listener(delOutbound);
            logger.info("更新出库单{}标签中",delOutbound.getOrderNo());
            R<List<BasAttachment>> listR = null;
            // 查询上传文件信息
            try{
                RemoteAttachmentService remoteAttachmentService = SpringUtils.getBean(RemoteAttachmentService.class);
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                logger.info("更新出库单{}标签,文件数量{}",delOutbound.getOrderNo(), listR.getData());

            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }


            String filePath = null;
            if (listR != null && listR.getData() != null && CollectionUtils.isNotEmpty(listR.getData())) {
                BasAttachment attachment = listR.getData().get(0);
                filePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();

                File labelFile = new File(filePath);
                if (!labelFile.exists()) {
                    throw new CommonException("500", MessageUtil.to("出库单文件不存在", "The delivery order file does not exist"));
                }
            }
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            String selfPickLabelFilePath = null;
            if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
                DelOutboundLabelDto dto = new DelOutboundLabelDto();
                dto.setId(delOutbound.getId());

                //生成下自提标签文件，如果有就不生成
                delOutboundService.labelSelfPick(null, dto);
                selfPickLabelFilePath = DelOutboundServiceImplUtil.getSelfPickLabelFilePath(delOutbound) + "/" + delOutbound.getOrderNo() + ".pdf";
            }
            String uploadBoxLabel = null;
            if("Y".equals(delOutbound.getUploadBoxLabel())) {
                IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);

                uploadBoxLabel = delOutboundBringVerifyService.getBoxLabel(delOutbound);


            }
            logger.info("更新出库单{}标签,文件{},自提标签{},箱标{}",delOutbound.getOrderNo(), filePath, selfPickLabelFilePath, uploadBoxLabel);
            if(selfPickLabelFilePath != null || uploadBoxLabel != null || filePath != null){
                String mergeFileDirPath = DelOutboundServiceImplUtil.getLabelMergeFilePath(delOutbound);
                File mergeFileDir = new File(mergeFileDirPath);

                File labelFile = null;
                if (!mergeFileDir.exists()) {
                    try {
                        FileUtils.forceMkdir(mergeFileDir);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new CommonException("500", MessageUtil.to("创建文件夹失败，", "Failed to create folder,") + e.getMessage());
                    }
                }
                String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
                try {
                    if (PdfUtil.merge(mergeFilePath, filePath, uploadBoxLabel, selfPickLabelFilePath)) {
                        labelFile = new File(mergeFilePath);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("出库单合并文件失败", "Failed to merge the delivery order file"));
                }

                if(labelFile == null){
                    return;
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
                        throw new CommonException("500", MessageUtil.to("更新标签失败", "Failed to update label"));
                    }
                    if (!responseVO.getSuccess()) {
                        throw new CommonException("500", Utils.defaultValue(responseVO.getMessage(), MessageUtil.to("更新标签失败", "Failed to update label")+"2"));
                    }
                } catch (IOException e) {
                    logger.error("读取标签文件失败, {}", e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("读取标签文件失败", "Failed to read label file"));
                }

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
