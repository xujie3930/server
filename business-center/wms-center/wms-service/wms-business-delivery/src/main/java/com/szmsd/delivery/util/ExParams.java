package com.szmsd.delivery.util;

import lombok.Getter;
import lombok.Setter;

/**
 * 导出参数
 *
 * @author
 */
@Getter
@Setter
public class ExParams {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * sheet名
     */
    private String sheetName;
    /**
     * 是否开启sheet分页:默认开启
     */
    private Boolean ifOpenMultiSheet = Boolean.TRUE;
    /**
     * 单sheet数据行数:默认为xlsx文件最大行数
     */
    private Integer dataNumsOfSheet = 2<<19;
}
