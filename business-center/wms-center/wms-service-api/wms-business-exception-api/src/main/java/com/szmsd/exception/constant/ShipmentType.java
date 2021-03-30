package com.szmsd.bas.constant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 9:19
 */
public final class ShipmentType {

    /**
     * 普货
     */
    public static final String GENERAL_CARGO = "GeneralCargo";

    /**
     * 电池
     */
    public static final String BATTERY = "Battery";

    /**
     * 液体
     */
    public static final String LIQUID = "Liquid";

    /**
     * 粉末
     */
    public static final String POWDER = "Powder";

    /**
     * 返回最高的状态
     * 电池 > 液体 > 粉末 > 普货
     *
     * @param collections collections
     * @return String
     */
    public static String highest(Collection<String> collections) {
        return get(collections, new String[]{BATTERY, LIQUID, POWDER}, GENERAL_CARGO);
    }

    /**
     * 返回最低的状态
     * 普货 > 粉末 > 液体 > 电池
     *
     * @param collections collections
     * @return String
     */
    public static String lowest(Collection<String> collections) {
        return get(collections, new String[]{GENERAL_CARGO, POWDER, LIQUID}, BATTERY);
    }

    /**
     * 获取状态
     *
     * @param collections   collections
     * @param statusArr     statusArr
     * @param defaultStatus defaultStatus
     * @return String
     */
    private static String get(Collection<String> collections, String[] statusArr, String defaultStatus) {
        Set<String> set = new HashSet<>(collections);
        for (String status : statusArr) {
            if (set.contains(status)) {
                return status;
            }
        }
        return defaultStatus;
    }
}
