package com.szmsd.chargerules.enums;

import java.util.Arrays;
import java.util.Objects;

public enum DelOutboundOrderEnum {

    NORMAL("Normal", "普通出库单"),
    DESTROY("Destroy", "销毁出库单"),
    SELF_PICK("SelfPick", "自提出库单"),
    PACKAGE_TRANSFER("PackageTransfer", "转运出库单"),
    COLLECTION("Collection", "集运出库单"),
    NEW_SKU("NewSku", "组合SKU上架出库单"),
    SPLIT_SKU("SplitSku", "拆分SKU上架出库单"),
    BATCH("Batch", "批量出库单"),
    SALES("Sales", "普通销售订单"),
    COLLECTION_MANY_SKU("Collection-manySku", "集运出库单-多SKU"),
    BATCH_PACKING("Batch-packing", "批量出库单-装箱费"),
    BATCH_LABEL("Batch-label", "批量出库单-贴标费"),
    FREEZE_IN_STORAGE("FreezeInStorage", "入库冻结"),
    PackageCollection("PackageCollection","揽收单"),
    MULTIPLE_PIECES("MultiplePieces","一票多件出库单"),

    ;

    private final String code;
    private final String name;

    DelOutboundOrderEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundOrderEnum get(String code) {
        for (DelOutboundOrderEnum anEnum : DelOutboundOrderEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getName(String code) {
        return Arrays.stream(DelOutboundOrderEnum.values()).filter(value -> value.getCode().equals(code)).map(DelOutboundOrderEnum::getName).findFirst().orElse(code);
    }

    public static String getCode(String name) {
        for (DelOutboundOrderEnum anEnum : DelOutboundOrderEnum.values()) {
            if (anEnum.getName().equals(name)) {
                return anEnum.getCode();
            }
        }
        return null;
    }

    public static boolean has(String code) {
        return Objects.nonNull(get(code));
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
