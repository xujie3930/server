package com.szmsd.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.MessageFormat;
import java.time.LocalDate;

/**
 * @ClassName: PurchaseEnum
 * @Description: 采购单枚举
 * @Author: 11
 * @Date: 2021-04-25 18:36
 */
@Getter
@AllArgsConstructor
public enum PurchaseEnum {
    PURCHASE_ORDER(0, "【采购单-创建】：{0}于{1}创建一条[采购单],\n【数据来源】：出库单：{2} \n【采购单号】：{3}"),
    WAREHOUSING_LIST(1, "【入库单-创建】：{0}于{1}创建一条[入库单],\n【数据来源】：采购单：{2} \n【入库单号】：{3}"),
    ;

    private final int type;
    private final String logFormat;

    public String generateLog(Object... param) {
        return MessageFormat.format(this.getLogFormat(), param);
    }

    public static void main(String[] args) {
        {
            String logFormat = PurchaseEnum.PURCHASE_ORDER.getLogFormat();
            System.out.println(MessageFormat.format(logFormat, "用户名", LocalDate.now(), "CG1111", "caaa"));
        }
        System.out.println("-----------------");
        {
            String logFormat = PurchaseEnum.WAREHOUSING_LIST.getLogFormat();
            System.out.println(MessageFormat.format(logFormat, "用户名", LocalDate.now(), "CG1111", "caaa"));
        }
    }
}

