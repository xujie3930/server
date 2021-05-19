package com.szmsd.bas.dto;

import lombok.Data;

/**
 * @author zhangyuyuan
 * @date 2021-05-18 17:17
 */
@Data
public class ServiceConditionDto {

    /**
     * 业务经理
     */
    private String serviceManager;

    /**
     * 客服
     */
    private String serviceStaff;
}
