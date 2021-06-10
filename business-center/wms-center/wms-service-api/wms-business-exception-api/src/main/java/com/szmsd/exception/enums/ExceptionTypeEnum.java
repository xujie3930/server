package com.szmsd.exception.enums;

import java.util.Objects;

public enum ExceptionTypeEnum {
    LABELNOTMATCHED("InboundLabelNotMatched","入库-标签不符"),
    GOODSUNIDENTIFED("InboundGoodsUnidentifed","入库-裸货无对版信息"),
    UNSAFE("InboundUnsafe","入库-安检不合格"),
    GOODSDAMAGED("InboundGoodsDamaged","入库-破损"),
    LESSTHANEXPECTEDQTY("InboundLessThanExpectedQty","入库-少货"),
    CANNOTPACKASREQUIRED("OutboundCanNotPackAsRequired","出库-无法按要求装箱"),
    MULTILABEL("OutboundMultiLabel","出库-多标签件"),
    NOMONEY("OutboundNoMoney","出库-欠费件"),
    EXPRESSDATAMISSING("OutboundExpressDataMissing","出库-快递资料不全"),
    ORDERDUPLICATE("OutboundOrderDuplicate","出库-出库单号重复"),
    GOODSNOTFOUND("OutboundGoodsNotFound","出库-仓库找不到货物"),
    OVERSIZE("OutboundOverSize","出库-超规格件"),
    OVERWEIGHT("OutboundOverWeight","出库-超重件"),
    BADPACKAGE("OutboundBadPackage","出库-包装不符"),
    GETTRACKINGFAILED("OutboundGetTrackingFailed","出库-获取挂号失败"),
    OUTBOUNDINTERCEPT("OutboundIntercept","出库-拦截"),
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
