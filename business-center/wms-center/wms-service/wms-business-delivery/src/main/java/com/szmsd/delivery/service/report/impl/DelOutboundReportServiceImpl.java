package com.szmsd.delivery.service.report.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.mapper.DelOutboundReportMapper;
import com.szmsd.delivery.service.report.IDelOutboundReportService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-29 9:33
 */
@Service
public class DelOutboundReportServiceImpl extends ServiceImpl<DelOutboundMapper, DelOutbound> implements IDelOutboundReportService {

    private final DelOutboundReportMapper reportMapper;

    public DelOutboundReportServiceImpl(DelOutboundReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<DelOutboundReportListVO> queryCreateData(DelOutboundReportQueryDto queryDto) {
        // 查询提审的出库单数量
        Date beginDate = queryDto.getBeginDate();
        Date endDate = queryDto.getEndDate();
        if (Utils.isAnyNull(queryDto.getSellerCode(), beginDate, endDate)) {
            return Collections.emptyList();
        }
        QueryWrapper<DelOutboundReportQueryDto> queryWrapper = Wrappers.query();
        // 客户代码
        queryWrapper.eq("o.seller_code", queryDto.getSellerCode());
        // 时间范围
        queryWrapper.between("o.create_time", beginDate, endDate);
        // 按天分组
        queryWrapper.groupBy("DATE_FORMAT(o.create_time, '%Y-%m-%d')");
        List<DelOutboundReportListVO> list = this.reportMapper.queryCreateData(queryWrapper);
        return this.fullDiff(beginDate, endDate, list);
    }

    @Override
    public List<DelOutboundReportListVO> queryBringVerifyData(DelOutboundReportQueryDto queryDto) {
        // 查询提审的出库单数量
        Date beginDate = queryDto.getBeginDate();
        Date endDate = queryDto.getEndDate();
        if (Utils.isAnyNull(queryDto.getSellerCode(), beginDate, endDate)) {
            return Collections.emptyList();
        }
        QueryWrapper<DelOutboundReportQueryDto> queryWrapper = Wrappers.query();
        // 提审时间不是空的
        queryWrapper.isNotNull("o.bring_verify_time");
        // 客户代码
        queryWrapper.eq("o.seller_code", queryDto.getSellerCode());
        // 时间范围
        queryWrapper.between("o.bring_verify_time", beginDate, endDate);
        queryWrapper.ne("o.del_flag", 2);
        // 按天分组
        queryWrapper.groupBy("DATE_FORMAT(o.bring_verify_time, '%Y-%m-%d')");
        List<DelOutboundReportListVO> list = this.reportMapper.queryBringVerifyData(queryWrapper);
        return this.fullDiff(beginDate, endDate, list);
    }

    @Override
    public List<DelOutboundReportListVO> queryOutboundData(DelOutboundReportQueryDto queryDto) {
        // 查询提审的出库单数量
        Date beginDate = queryDto.getBeginDate();
        Date endDate = queryDto.getEndDate();
        if (Utils.isAnyNull(queryDto.getSellerCode(), beginDate, endDate)) {
            return Collections.emptyList();
        }
        QueryWrapper<DelOutboundReportQueryDto> queryWrapper = Wrappers.query();
        // 已完成的
        queryWrapper.eq("o.state", DelOutboundStateEnum.COMPLETED.getCode());
        // 客户代码
        queryWrapper.eq("o.seller_code", queryDto.getSellerCode());
        // 时间范围
        queryWrapper.between("o.shipments_time", beginDate, endDate);
        // 按天分组
        queryWrapper.groupBy("DATE_FORMAT(o.shipments_time, '%Y-%m-%d')");
        List<DelOutboundReportListVO> list = this.reportMapper.queryOutboundData(queryWrapper);
        return this.fullDiff(beginDate, endDate, list);
    }

    public List<DelOutboundReportListVO> fullDiff(Date beginDate, Date endDate, List<DelOutboundReportListVO> list) {
        // 计算区间差
        long diff = Utils.subtractDate(endDate, beginDate) + 1;
        String format = "yyyy-MM-dd";
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>((int) (diff));
            // 处理空值
            for (int i = 0; i < diff; i++) {
                list.add(new DelOutboundReportListVO(DateFormatUtils.format(beginDate, format), 0));
                beginDate = Utils.addDays(beginDate, 1);
            }
        } else {
            String currentDate;
            // 数据补差，首部和中间的
            if (list.size() < diff) {
                for (int i = 0; i < list.size(); i++) {
                    currentDate = DateFormatUtils.format(beginDate, format);
                    if (!currentDate.equals(list.get(i).getDate())) {
                        list.add(i, new DelOutboundReportListVO(currentDate, 0));
                    }
                    beginDate = Utils.addDays(beginDate, 1);
                }
            }
            // 尾部的
            int lastDiff = (int) (diff - list.size());
            if (lastDiff > 0) {
                for (int i = 0; i < lastDiff; i++) {
                    currentDate = DateFormatUtils.format(beginDate, format);
                    list.add(new DelOutboundReportListVO(currentDate, 0));
                    beginDate = Utils.addDays(beginDate, 1);
                }
            }
        }
        return list;
    }
}
