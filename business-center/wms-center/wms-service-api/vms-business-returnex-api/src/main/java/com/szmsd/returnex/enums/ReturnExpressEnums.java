package com.szmsd.returnex.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.stream.Stream;

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
        Destroy("销毁"),
        PutawayByPackage("整包上架"),
        OpenAndCheck("拆包检查"),
        PutawayBySku("按明细上架");
        private String desc;
    }

    /**
     * 实际处理方式
     */
    @Getter
    @AllArgsConstructor
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
    public enum ReturnSourceEnum {
        /**
         * 申请退件 预约退件
         */
        RETURN_FORECAST(1,"退件预报"),
        WMS_RETURN(2,"WMS通知退件");
        private Integer key;
        private String desc;

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
    public enum DestinationWarehouseEnum {
        /**
         * sz
         */
        SZ("深圳"),
        DG("德国");
        private String desc;
    }

}
