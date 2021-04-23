package com.szmsd.common.core.web.controller;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 19:03
 */
public class QueryDto implements Serializable {

    @ApiModelProperty("当前页，从1开始，默认为1")
    private int pageNum = 1;

    @ApiModelProperty("每页的数量，默认为10")
    private int pageSize = 10;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
