package com.szmsd.putinstorage.component;

import com.szmsd.putinstorage.enums.InboundReceiptEnum;

import java.util.Optional;

/**
 * @ClassName: CheckTag
 * @Description:
 * @Author: 11
 * @Date: 2021-04-28 18:32
 */
public class CheckTag {
    private static final ThreadLocal<Boolean> FROM_TRANSPORT = new ThreadLocal<>();

    public static void set(String orderType) {
        FROM_TRANSPORT.set(InboundReceiptEnum.OrderType.COLLECTION.getValue().equals(orderType));
    }
    public static Boolean get() {
        return Optional.ofNullable(FROM_TRANSPORT.get()).orElse(false);
    }

    public static void remove() {
        FROM_TRANSPORT.remove();
    }
}
