package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundStateEnum {

    // 新建的单据默认为【待提审】
    REVIEWED("REVIEWED", "待提审"),
    // 提审步骤出现异常修改状态为【审核失败】
    AUDIT_FAILED("AUDIT_FAILED", "审核失败"),

    // 提审成功之后，修改单据状态为【待发货】，这个审核已经向仓库发起创建单据的请求
    // 仓库那边核重之后会调用OMS核重的接口
    DELIVERED("DELIVERED", "待发货"),
    // OMS接收到新的核重数据后修改单据信息，同时处理承运商单据等信息。并且通知仓库发货
    // OMS接收到核重信息后修改单据状态为【处理中】
    PROCESSING("PROCESSING", "处理中"),
    // OMS处理过程中如果出现异常，通知仓库发货指令，传参异常为是，并且修改状态为【处理中】
    // OMS处理过程无异常，通知仓库发货指令，传参异常为否，并且修改状态为【通知仓库处理】
    NOTIFY_WHSE_PROCESSING("NOTIFY_WHSE_PROCESSING", "通知仓库处理"),
    // 仓库更新状态，OMS接收到仓库处理的状态后，更新状态为【仓库处理中】
    WHSE_PROCESSING("WHSE_PROCESSING", "仓库处理中"),

    // 仓库通知OMS已发货，OMS接收到通知后，修改单据状态为【仓库发货】，然后处理自己的业务，自己的业务处理完成后修改状态为【已完成】
    WHSE_COMPLETED("WHSE_COMPLETED", "仓库发货"),
    COMPLETED("COMPLETED", "已完成"),

    // 1.待提审，提审失败，OMS取消单据，直接取消。
    // 2.OMS取消单据，调用仓库取消单据接口，成功之后OMS等待仓库取消，单据状态修改为【仓库取消中】
    // 仓库取消成功之后会调用OMS更新状态接口，此时修改单据状态为【仓库取消】，OMS处理自己的业务。
    // OMS处理完成自己的业务之后修改单据状态为【已取消】
    WHSE_CANCELING("WHSE_CANCELING", "仓库取消中"),
    WHSE_CANCELLED("WHSE_CANCELLED", "仓库取消"),
    CANCELLED("CANCELLED", "已取消"),

    ;

    private final String code;
    private final String name;

    DelOutboundStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundStateEnum get(String code) {
        for (DelOutboundStateEnum anEnum : DelOutboundStateEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public static String getOriginName(String code) {
        DelOutboundStateEnum anEnum = get(code);
        if (null != anEnum) {
            return anEnum.getName();
        }
        return "";
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
