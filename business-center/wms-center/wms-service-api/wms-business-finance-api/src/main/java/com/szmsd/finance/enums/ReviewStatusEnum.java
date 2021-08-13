package com.szmsd.finance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName: ReviewStatusEnum
 * @Description: 审核状态
 * @Author: 11
 * @Date: 2021-08-13 16:07
 */
@Getter
@AllArgsConstructor
public enum ReviewStatusEnum {
    /**
     * 审核状态 0 待审核，1：审核驳回，2：审核 通过
     */
    AUDIT_WAIT(0, "待审核"),
    AUDIT_REJECT(1, "审核驳回"),
    AUDIT_PASS(2, "审核通过"),
    ;
    private Integer status;

    private String desc;
}
