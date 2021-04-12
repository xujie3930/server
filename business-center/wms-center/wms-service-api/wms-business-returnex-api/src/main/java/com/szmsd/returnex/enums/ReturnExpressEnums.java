package com.szmsd.returnex.enums;

import com.szmsd.common.core.exception.web.BaseException;
import lombok.*;

import java.util.Arrays;

/**
 * @ClassName: ReturnExpressEnums
 * @Description: 退单枚举类型
 * @Author: 11
 * @Date: 2021/3/27 16:36
 */
public class ReturnExpressEnums {

    /**
     * @Description: 退件单处理方式
     * string
     * 处理方式
     * 销毁：Destroy
     * 整包上架：PutawayByPackage
     * 拆包检查：OpenAndCheck
     * 按明细上架：PutawayBySku
     */
    @Getter
    @AllArgsConstructor
    public enum ProcessTypeEnum {
        /**
         *
         */
        Destroy("销毁", ""),
        PutawayByPackage("整包上架", ""),
        OpenAndCheck("拆包检查", ""),
        PutawayBySku("按明细上架", "");
        private String desc;
        private String val;
    }

    /**
     * WMS 退件处理方式
     */
    @Getter
    @AllArgsConstructor
    public enum WMSProcessTypeEnum {
        /**
         *
         */
        DESTROY("销毁", "Destroy"),
        PUT_AWAY_BY_PACKAGE("整包上架", "PutawayByPackage"),
        OPEN_AND_CHECK("拆包检查", "OpenAndCheck"),
        PUT_AWAY_BY_SKU("按明细上架", "PutawayBySku");
        private String desc;
        private String val;

        public static String getWMSProcessTypeStr(String processTypeStr) {
            return Arrays.stream(WMSProcessTypeEnum.values())
                    .filter(x -> processTypeStr.equals(x.getDesc()))
                    .map(WMSProcessTypeEnum::getVal).findAny().orElseThrow(() -> new BaseException("调用WMS异常,暂未配置该类型的处理方式"));
        }
    }

    /**
     * 实际处理方式
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum ApplyProcessMethodEnum {
        /**
         * TODO 未定
         */
        Destroy("销毁"),
        PutawayByPackage("整包上架"),
        OpenAndCheck("拆包检查"),
        PutawayBySku("按明细上架");
        private String desc;
    }

    /**
     * 退件单来源
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum ReturnSourceEnum {
        /**
         * 申请退件 预约退件
         */
        RETURN_FORECAST(1, "退件预报", "123"),
        WMS_RETURN(2, "WMS通知退件", "123");
        private Integer key;
        private String desc;
        private String val;

        public static String getDesc(Integer key) {
            return Arrays.stream(ReturnSourceEnum.values())
                    .filter(x -> key.equals(x.getKey()))
                    .map(ReturnSourceEnum::getDesc).findAny().orElse("");
        }
    }

    /**
     * 退件单类型
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum ReturnTypeEnum {
        /**
         * 自有库存退件 转运单退件 外部渠道退件
         */
        OWN_INVENTORY_RETURN("自有库存退件"),
        TRANSFER_ORDER_RETURN("转运单退件"),
        RETURN_FROM_EXTERNAL_CHANNELS("外部渠道退件");
        private String desc;
    }

    /**
     * 退件目标仓库
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum WarehouseEnum {
        /**
         * sz
         */
        SZ("深圳"),
        DG("德国");
        private String desc;
    }

    /**
     * 退件处理状态
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum DealStatusEnum {
        /**
         * sz
         */
        WMS_WAIT_RECEIVE("处理中", "WMS待收货", ""),
        WAIT_CUSTOMER_DEAL("待客户处理", "待客户反馈意见", ""),
        WAIT_ASSIGNED("待指派", "待指派无主件", ""),
        WAIT_PROCESSED_AFTER_UNPACKING("待客户处理", "拆包检查后待处理", ""),
        WMS_RECEIVED_DEAL_WAY("处理中", "WMS接收客户处理结果", ""),
        WMS_FINISH("已完成", "WMS处理完成", "");

        private String desc;
        private String note;
        private String val;
    }

    /**
     * 退件处理状态
     */
    @Getter
    @AllArgsConstructor
    @Deprecated
    public enum OverdueEnum {
        /**
         * sz
         */
        OVERDUE("1", "是"),
        NOT_OVERDUE("0", "否");
        private String key;
        private String desc;

        public static String getDesc(String str) {
            return Arrays.stream(OverdueEnum.values())
                    .filter(x -> x.getKey().equals(str))
                    .map(OverdueEnum::getDesc).findAny().orElse("");
        }
    }
}
