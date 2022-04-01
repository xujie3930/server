package com.szmsd.delivery.exported;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.delivery.dto.DelOutboundExportListDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundExportListVO;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class DelOutboundExportQueryPage implements QueryPage<DelOutboundExportListVO> {

    private final DelOutboundListQueryDto delOutboundListQueryDto;
    private final QueryDto queryDto;
    private final ExportContext exportContext;
    private final IDelOutboundService delOutboundService;


    public DelOutboundExportQueryPage(DelOutboundListQueryDto delOutboundListQueryDto, QueryDto queryDto, ExportContext exportContext, IDelOutboundService delOutboundService) {
        this.delOutboundListQueryDto = delOutboundListQueryDto;
        this.queryDto = queryDto;
        this.exportContext = exportContext;
        this.delOutboundService = delOutboundService;
    }

    @Override
    public Page<DelOutboundExportListVO> getPage() {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        Page<DelOutboundExportListDto> exportPage = (Page<DelOutboundExportListDto>) this.delOutboundService.exportList(delOutboundListQueryDto);
        Page<DelOutboundExportListVO> page = new Page<>();
        page.setTotal(exportPage.getTotal());
        page.setPages(exportPage.getPages());
        page.setPageNum(exportPage.getPageNum());
        if (CollectionUtils.isNotEmpty(exportPage)) {
            for (DelOutboundExportListDto dto : exportPage) {
                DelOutboundExportListVO vo = BeanMapperUtil.map(dto, DelOutboundExportListVO.class);
                vo.setStateName(this.exportContext.getStateName(dto.getState()));
                vo.setWarehouseName(this.exportContext.getWarehouseName(dto.getWarehouseCode()));
                vo.setOrderTypeName(this.exportContext.getOrderTypeName(dto.getOrderType()));
                vo.setExceptionStateName(this.exportContext.getExceptionStateName(dto.getExceptionState()));
                if (StringUtils.isNotEmpty(dto.getCountryCode()) || "en".equals(this.exportContext.len())) {
                    // 根据编码去查询名称
                    vo.setCountry(this.exportContext.getCountry(dto.getCountryCode()));
                }
                page.add(vo);
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
