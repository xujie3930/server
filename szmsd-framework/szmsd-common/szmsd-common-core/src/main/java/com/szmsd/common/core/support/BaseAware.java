package com.szmsd.common.core.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.utils.sql.SqlUtil;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.PageDomain;
import com.szmsd.common.core.web.page.TableDataInfo;

import java.util.List;

public interface BaseAware {


    default void startPage(QueryDto queryDto) {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(queryDto.getPageNum());
        pageDomain.setPageSize(queryDto.getPageSize());
        startPage(pageDomain);
    }

    default void startPage(PageDomain pageDomain) {

        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        if (pageNum == null) {
            pageNum = 1;
            pageDomain.setPageNum(pageNum);
        }
        if (pageSize == null) {
            pageSize = 10;
            pageDomain.setPageSize(pageSize);
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy);
    }

    default  <T> TableDataInfo<T> getDataTable(List<T> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        //只适用于list instanceof Page 的查询
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

}
