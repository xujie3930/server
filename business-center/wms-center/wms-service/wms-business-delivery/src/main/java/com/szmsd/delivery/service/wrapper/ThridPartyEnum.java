package com.szmsd.delivery.service.wrapper;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.MessageUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.dto.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public enum ThridPartyEnum implements ApplicationState, ApplicationRegister{

    /**
     * 开始
     */
    BEGIN,

    // #1 PRC 计费        - 无回滚
    PRC_PRICING,

    // #2 冻结物流费用    - 有回滚
    FREEZE_BALANCE,

    END,
    ;


    public static ThridPartyEnum get(String name) {
        for (ThridPartyEnum anEnum : ThridPartyEnum.values()) {
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
        map.put(END.name(), new EndHandle());
        return map;
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
            logger.info(">>>>>[ThridPartyEnum创建出库单{}]-开始执行Pricing", delOutbound.getOrderNo());

            stopWatch.start();
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.PACKAGE);
            stopWatch.stop();
            logger.info(">>>>>[ThridPartyEnum创建出库单{}]-Pricing计算返回结果：耗时{}, 内容:{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis(),
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
            logger.info(">>>>>[ThridPartyEnum创建出库单{}]-Pricing保存出库单费用信息：耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());
            // 更新值
            delOutbound.setAmount(totalAmount);
            delOutbound.setCurrencyCode(totalCurrencyCode);

            //分组计算货币金额
            Map<String, BigDecimal> currencyMap = new HashMap<String, BigDecimal>();
            for (DelOutboundCharge charge: delOutboundCharges){
                if(currencyMap.containsKey(charge.getCurrencyCode())){
                    currencyMap.put(charge.getCurrencyCode(), currencyMap.get(charge.getCurrencyCode()).add(charge.getAmount()));
                }else{
                    currencyMap.put(charge.getCurrencyCode(), charge.getAmount());
                }
            }
            delOutbound.setCurrencyDescribe(ArrayUtil.join(currencyMap.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(e -> e.getValue() + e.getKey()).collect(Collectors.toList()).toArray(), "；"));


            //更新PRC发货服务
            IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setProductShipmentRule(data.getShipmentRule());
            updateDelOutbound.setPackingRule(delOutbound.getPackingRule());
            updateDelOutbound.setGrade(data.getGrade());
            updateDelOutbound.setZoneName(data.getZoneName());
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
            updateDelOutbound.setThridPardState(BEGIN.name());

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

        public static final TimeUnit unit = TimeUnit.SECONDS;

        public static final long time = 3L;

        @Override
        public void handle(ApplicationContext context) {

            StopWatch stopWatch = new StopWatch();

            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            logger.info("ThridPartyEnum出库单{}-开始冻结费用：{}", delOutbound.getOrderNo(), JSONObject.toJSONString(delOutbound));
            DelOutboundOperationLogEnum.BRV_FREEZE_BALANCE.listener(delOutbound);

            RedissonClient redissonClient = SpringUtils.getBean(RedissonClient.class);

            String key = "bringVerify-fss-freeze-balance" + delOutbound.getCustomCode() + ":" + delOutbound.getOrderNo();

            RLock lock = redissonClient.getLock(key);

            try {

                lock.tryLock(time, unit);

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

                logger.info(">>>>>[ThridPartyEnum创建出库单{}]冻结费用map, 数据:{}",delOutbound.getOrderNo(), JSONObject.toJSONString(groupByCharge));

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

                    logger.info(">>>>>[ThridPartyEnum创建出库单{}]结束冻结费用, 数据:{} ,耗时{}", delOutbound.getOrderNo(), JSONObject.toJSONString(cusFreezeBalanceDTO), stopWatch.getLastTaskInfo().getTimeMillis());
                }

            }catch (Exception e){
                logger.info("冻结费用异常，加锁失败");
                logger.info("异常信息:" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            }finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
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
            return END;
        }
    }

    static class EndHandle extends CommonApplicationHandle {

        @Override
        public ApplicationState preState() {
            return FREEZE_BALANCE;
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

    static abstract class CommonApplicationHandle extends ApplicationHandle.AbstractApplicationHandle {

        @Override
        public boolean condition(ApplicationContext context, ApplicationState currentState) {
            DelOutboundWrapperContext delOutboundWrapperContext = (DelOutboundWrapperContext) context;
            DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
            DelOutboundOrderTypeEnum orderTypeEnum = DelOutboundOrderTypeEnum.get(delOutbound.getOrderType());
            if (null == orderTypeEnum) {
                throw new CommonException("400", MessageUtil.to("不存在的类型[" + delOutbound.getOrderType() + "]", "Non-existent type ["+delOutbound. getOrderType()+"]"));
            }
            return otherCondition(context, currentState);
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

        }
    }
}
