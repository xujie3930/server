package com.szmsd.delivery.exported;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.dto.DelOutboundReassignExportListDto;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.vo.DelOutboundReassignExportListVO;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class DelOutboundReassignExportQueryPage implements QueryPage<DelOutboundReassignExportListVO> {

    private final DelOutboundListQueryDto delOutboundListQueryDto;
    private final QueryDto queryDto;
    private final ReassignExportContext exportContext;
    private final IDelOutboundService delOutboundService;


    public DelOutboundReassignExportQueryPage(DelOutboundListQueryDto delOutboundListQueryDto, QueryDto queryDto, ReassignExportContext exportContext, IDelOutboundService delOutboundService) {
        this.delOutboundListQueryDto = delOutboundListQueryDto;
        this.queryDto = queryDto;
        this.exportContext = exportContext;
        this.delOutboundService = delOutboundService;
    }

    @Override
    public Page<DelOutboundReassignExportListVO> getPage() {
        PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize());
        Page<DelOutboundReassignExportListDto> exportPage = (Page<DelOutboundReassignExportListDto>) this.delOutboundService.reassignExportList(delOutboundListQueryDto);
        Page<DelOutboundReassignExportListVO> page = new Page<>();
        page.setTotal(exportPage.getTotal());
        page.setPages(exportPage.getPages());
        page.setPageNum(exportPage.getPageNum());
        if (CollectionUtils.isNotEmpty(exportPage)) {
            for (DelOutboundReassignExportListDto dto : exportPage) {
                DelOutboundReassignExportListVO vo = BeanMapperUtil.map(dto, DelOutboundReassignExportListVO.class);
                vo.setRefNo2(dto.getRefNo());
                vo.setStateName(this.exportContext.getStateName(dto.getState()));
                vo.setWarehouseName(this.exportContext.getWarehouseName(dto.getWarehouseCode()));
                vo.setOrderTypeName(this.exportContext.getOrderTypeName(dto.getOrderType()));
                vo.setTrackingStatusName(this.exportContext.getTrackingStatusName(dto.getTrackingStatus()));
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
