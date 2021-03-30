package com.szmsd.exception.enums;

import java.util.Objects;

public enum ExceptionTypeEnum {
    LABELNOTMATCHED("Inbound-LabelNotMatched","入库-标签不符"),
    GOODSUNIDENTIFED("Inbound-GoodsUnidentifed","入库-裸货无对版信息"),
    UNSAFE("Inbound-Unsafe","入库-安检不合格"),
    GOODSDAMAGED("Inbound-GoodsDamaged","入库-破损"),
    LESSTHANEXPECTEDQTY("Inbound-LessThanExpectedQty","入库-少货"),
    CANNOTPACKASREQUIRED("Outbound-CanNotPackAsRequired","出库-无法按要求装箱"),
    MULTILABEL("Outbound-MultiLabel","出库-多标签件"),
    NOMONEY("Outbound-NoMoney","出库-欠费件"),
    EXPRESSDATAMISSING("Outbound-ExpressDataMissing","出库-快递资料不全"),
    ORDERDUPLICATE("Outbound-OrderDuplicate","出库-出库单号重复"),
    GOODSNOTFOUND("Outbound-GoodsNotFound","出库-仓库找不到货物"),
    OVERSIZE("Outbound-OverSize","出库-超规格件"),
    OVERWEIGHT("Outbound-OverWeight","出库-超重件"),
    BADPACKAGE("Outbound-BadPackage","出库-包装不符"),
    GETTRACKINGFAILED("Outbound-GetTrackingFailed","出库-获取挂号失败"),
    ;

    private final String code;
    private final String name;
    ExceptionTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }
    public static ExceptionTypeEnum get(String code){
        for(ExceptionTypeEnum anEnum:ExceptionTypeEnum.values()){
            if(anEnum.getCode().equals(code)){
                return anEnum;
            }
        }
        return null;
    }
    public static boolean has(String code){
        return Objects.nonNull(get(code));
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
