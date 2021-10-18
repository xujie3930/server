package com.szmsd.finance.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import io.netty.util.Constant;
import io.netty.util.internal.ConstantTimeUtils;
import lombok.AllArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @ClassName: CreditConstant
 * @Description: 授信额 constant
 * @Author: 11
 * @Date: 2021-09-07 15:31
 */
public final class CreditConstant {

    public static final ChronoUnit CREDIT_UNIT = ChronoUnit.DAYS;
    public static final int CREDIT_BUFFER_Interval = 3;

    /**
     * 授信额度使用状态
     */
    @AllArgsConstructor
    public enum CreditStatusEnum implements IEnum<Integer> {
        /**
         * 0：未启用，1：启用中，2：欠费停用，3：已禁用
         */
        NOT_ENABLED(0, "未启用"),
        ACTIVE(1, "启用中"),
        ARREARAGE_DEACTIVATION(2, "欠费停用"),
        DISABLED(3, "禁用 - TODO"),
        ;
        @EnumValue
        private final Integer status;

        private final String desc;

        @Override
        public Integer getValue() {
            return status;
        }
    }

    /**
     * 授信额度类型
     */
    @AllArgsConstructor
    public enum CreditTypeEnum implements IEnum<Integer> {
        /**
         * 额度 期限
         */
        DEFAULT(-1, ""),
        QUOTA(0, "额度"),
        TIME_LIMIT(1, "期限");
        @EnumValue
        private final Integer type;
        private final String desc;

        @Override
        public Integer getValue() {
            return type;
        }

        public static CreditTypeEnum getThisByTypeCode(String code) {
            return Arrays.stream(CreditTypeEnum.values())
                    .filter(x -> (x.getValue() + "").equals(code))
                    .findAny()
                    .orElse(DEFAULT);
        }
    }
}
