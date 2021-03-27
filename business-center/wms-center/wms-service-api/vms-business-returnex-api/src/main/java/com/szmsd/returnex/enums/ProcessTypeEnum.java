package com.szmsd.returnex.enums;

/**
 * @ClassName: ProcessTypeEnum
 * @Description: 退件单处理方式
 * string
 * 处理方式
 * 销毁：Destroy
 * 整包上架：PutawayByPackage
 * 拆包检查：OpenAndCheck
 * 按明细上架：PutawayBySku
 * @Author: 11
 * @Date: 2021/3/27 11:17
 */
public enum ProcessTypeEnum {
    Destroy, PutawayByPackage, OpenAndCheck, PutawayBySku;
}
