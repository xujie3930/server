package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundStateEnum {

    /**
     * 待提审
     *  - 验证单据状态，只能操作待提审，提审失败的单据
     *  - 提交单据到提审队列中
     *  - 修改单据状态为提审中
     * 提审中
     *  - PRC计费 ------- PRC
     *  - 冻结物流费用 ------- OMS费用
     *  - 获取产品信息（挂号获取方式）------- PRC
     *  - 新增/修改发货规则 ------- PRC
     *  - 创建承运商物流订单（根据挂号获取方式判断）------- 承运商
     *  - 冻结库存 ------- OMS库存
     *  - 冻结操作费用 ------- OMS费用
     *  - 推单WMS ------- WMS
     *  - 更新标签（自提出库 或者 批量出库（渠道是自提出库）） ------- WMS
     *  - 结果：
     *      - 成功：
     *          - 修改单据状态为待发货
     *      - 失败：
     *          - [取消冻结操作费用] ------- OMS费用
     *          - [取消冻结库存] ------- OMS库存
     *          - [取消承运商物流订单] ------- 承运商
     *          - [取消冻结物流费用] ------- OMS费用
     *          - 修改单据状态为审核失败
     * 审核失败
     *  - 在界面上重新提交提审
     * 待发货
     *  - WMS更新包材信息
     *  - 修改单据状态为处理中
     * 处理中
     *  - WMS核重
     *  - 创建承运商订单 ------- 承运商
     *  - 更新挂号 ------- WMS
     *  - 取消冻结物流费用 ------- OMS费用
     *  - PRC计费 ------- PRC
     *  - 冻结物流费用 ------- OMS费用
     *  - 冻结库存（转运出库，集运出库） ------- OMS库存
     *  - 提交获取标签队列
     *  - 获取标签 ------- 承运商
     *  - 推送标签 ------- WMS
     *  - 更新发货指令 ------- WMS
     *  - 修改单据状态为通知仓库处理中
     * 通知仓库处理
     *  - WMS推送处理中状态
     *  - 判断单据状态是否为通知仓库处理中
     *  - 修改单据状态为仓库处理中
     * 仓库处理中
     *  - WMS推送单据完成状态
     *  - 判断单据状态是否为仓库处理中
     *  - 修改单据状态为仓库发货
     *  - 提交单据完成队列
     * 仓库发货
     *  - 扣减库存
     *  - 扣减物流费用
     *  - 扣减操作费用
     *  - 扣减包材费用
     *  - 修改单据状态为已完成
     * 已完成
     *
     *
     * 仓库取消中
     * 仓库取消
     * 已取消
     */

    // 新建的单据默认为【待提审】
    REVIEWED("REVIEWED", "待提审"),
    // 提审步骤出现异常修改状态为【审核失败】
    AUDIT_FAILED("AUDIT_FAILED", "审核失败"),

    // 提审中，提审之后，处理改成异步
    REVIEWED_DOING("REVIEWED_DOING", "提审中"),

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
