package com.szmsd.delivery.service.report;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;

import java.util.List;

public interface IDelOutboundReportService extends IService<DelOutbound> {

    /**
     * 查询提审数据
     *
     * @param queryDto queryDto
     * @return List<DelOutboundReportListVO>
     */
    List<DelOutboundReportListVO> queryBringVerifyData(DelOutboundReportQueryDto queryDto);

    /**
     * 查询出库数据
     *
     * @param queryDto queryDto
     * @return List<DelOutboundReportListVO>
     */
    List<DelOutboundReportListVO> queryOutboundData(DelOutboundReportQueryDto queryDto);
}

