package com.szmsd.exception.exported;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.exception.dto.ExceptionInfoExportDto;
import com.szmsd.exception.dto.ExceptionInfoQueryDto;
import com.szmsd.exception.service.IExceptionInfoService;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class ExceptionInfoExportQueryPage implements QueryPage<ExceptionInfoExportDto> {

    private final ExceptionInfoQueryDto dto;
    private final QueryDto queryDto;
    private final ExportContext exportContext;
    private final IExceptionInfoService exceptionInfoService;

    public ExceptionInfoExportQueryPage(ExceptionInfoQueryDto dto, QueryDto queryDto, ExportContext exportContext, IExceptionInfoService exceptionInfoService) {
        this.dto = dto;
        this.queryDto = queryDto;
        this.exportContext = exportContext;
        this.exceptionInfoService = exceptionInfoService;
    }

    @Override
    public Page<ExceptionInfoExportDto> getPage() {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        Page<ExceptionInfoExportDto> exportPage = (Page<ExceptionInfoExportDto>) this.exceptionInfoService.exportList(dto);
        Page<ExceptionInfoExportDto> page = new Page<>();
        page.setTotal(exportPage.getTotal());
        page.setPages(exportPage.getPages());
        page.setPageNum(exportPage.getPageNum());
        if (CollectionUtils.isNotEmpty(exportPage)) {
            for (ExceptionInfoExportDto dto : exportPage) {
                dto.setStateName(this.exportContext.getStateName(dto.getState()));
                page.add(dto);
            }
        }
        return page;
    }

    @Override
    public void nextPage() {
        // 下一页
        queryDto.setPageNum(queryDto.getPageNum() + 1);
    }
}
