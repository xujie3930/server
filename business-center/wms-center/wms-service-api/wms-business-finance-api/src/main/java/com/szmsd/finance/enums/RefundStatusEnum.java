package com.szmsd.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName: ReviewStatusEnum
 * @Description: 退费审核状态
 * @Author: 11
 * @Date: 2021-08-13 16:07
 */
@Getter
@AllArgsConstructor
public enum RefundStatusEnum {
    /**
     * 状态：初始、提审、异常、完成
     */
    INITIAL(0, "初始"),
    BRING_INTO_COURT(1, "提审"),
    ABNORMAL(2, "异常"),
    COMPLETE(3, "完成"),
    ;
    private Integer status;

    private String desc;
}
