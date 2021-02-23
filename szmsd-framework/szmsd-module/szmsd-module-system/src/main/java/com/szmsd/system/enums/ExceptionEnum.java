package com.szmsd.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    DEPT_AND_POST_EXISTING("部门岗位已经存在");


    private String message;
}
