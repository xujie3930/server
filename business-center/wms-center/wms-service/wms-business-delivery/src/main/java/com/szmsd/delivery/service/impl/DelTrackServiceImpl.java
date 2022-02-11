package com.szmsd.delivery.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;

import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.delivery.dto.TrackingYeeTraceDto;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.mapper.DelTrackMapper;
import com.szmsd.delivery.service.IDelTrackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
@Service
public class DelTrackServiceImpl extends ServiceImpl<DelTrackMapper, DelTrack> implements IDelTrackService {

    @Autowired
    private DelOutboundMapper delOutboundMapper;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public DelTrack selectDelTrackById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param delTrack 模块
     * @return 模块
     */
    @Override
    public List<DelTrack> selectDelTrackList(DelTrack delTrack) {
        QueryWrapper<DelTrack> where = new QueryWrapper<DelTrack>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    @Override
    public int insertDelTrack(DelTrack delTrack) {
        return baseMapper.insert(delTrack);
    }

    /**
     * 修改模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    @Override
    public int updateDelTrack(DelTrack delTrack) {
        return baseMapper.updateById(delTrack);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void traceCallback(TrackingYeeTraceDto trackingYeeTraceDto) {
        List<TrackingYeeTraceDto.LogisticsTrackingSectionsDto> logisticsTrackingSections = trackingYeeTraceDto.getLogisticsTrackingSections();
        if (CollectionUtils.isEmpty(logisticsTrackingSections)) {
            return;
        }
        List<DelTrack> trackList = new ArrayList<>();
        logisticsTrackingSections.forEach(trackingSection -> {
            TrackingYeeTraceDto.LogisticsTrackingDto logisticsTracking = trackingSection.getLogisticsTracking();
            // 只获取主运单号的轨迹  关联运单号的暂不获取
            if (logisticsTracking != null && logisticsTracking.getTrackingNo().equalsIgnoreCase(trackingYeeTraceDto.getTrackingNo())) {
                List<TrackingYeeTraceDto.ItemsDto> trackingItems = logisticsTracking.getItems();
                trackingItems.forEach(item -> {
                    // 校验路由信息存不存在
                    Integer trackCount = this.count(new LambdaQueryWrapper<DelTrack>().eq(DelTrack::getOrderNo, trackingYeeTraceDto.getOrderNo())
                            .eq(DelTrack::getTrackingNo, trackingYeeTraceDto.getTrackingNo())
                            .eq(DelTrack::getNo, item.getNo()).last("limit 1"));
                    if (trackCount == 0) {
                        DelTrack delTrack = new DelTrack();
                        delTrack.setTrackingNo(trackingYeeTraceDto.getTrackingNo());
                        delTrack.setCarrierCode(trackingYeeTraceDto.getCarrierCode());
                        delTrack.setShipmentId(trackingYeeTraceDto.getShipmentId());
                        delTrack.setOrderNo(trackingYeeTraceDto.getOrderNo());
                        delTrack.setTrackingStatus(trackingYeeTraceDto.getTrackingStatus());
                        delTrack.setNo(item.getNo());
                        delTrack.setDescription(item.getDescription());
                        // 获取时间
                        TrackingYeeTraceDto.TrackingTimeDto trackingTime = item.getTrackingTime();
                        if (trackingTime != null) {
                            String trackingTimeStr = trackingTime.getDateTime();
                            delTrack.setTrackingTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, trackingTimeStr));
                        }
                        delTrack.setActionCode(item.getActionCode());
                        // 获取地址
                        TrackingYeeTraceDto.LocationDto itemLocation = item.getLocation();
                        if (itemLocation != null) {
                            delTrack.setDisplay(itemLocation.getDisplay());
                            TrackingYeeTraceDto.AddressDto address = itemLocation.getAddress();
                            if (address != null) {
                                TrackingYeeTraceDto.CountryDto countryDto = address.getCountry();
                                if (countryDto != null) {
                                    delTrack.setCountryCode(countryDto.getAlpha2Code());
                                    delTrack.setCountryNameEn(countryDto.getEnName());
                                    delTrack.setCountryNameCn(countryDto.getCnName());
                                }
                                delTrack.setProvince(address.getProvince());
                                delTrack.setCity(address.getCity());
                                delTrack.setPostcode(address.getPostcode());
                                delTrack.setStreet1(address.getStreet1());
                                delTrack.setStreet2(address.getStreet2());
                                delTrack.setStreet3(address.getStreet3());
                            }
                        }
                        trackList.add(delTrack);
                    }
                });
            }
        });
        if (CollectionUtils.isNotEmpty(trackList)) {
            this.saveBatch(trackList);
            DelOutbound delOutbound = delOutboundMapper.selectOne(new LambdaQueryWrapper<DelOutbound>().eq(DelOutbound::getOrderNo, trackingYeeTraceDto.getOrderNo()).last("limit 1"));
            if (delOutbound != null) {
                List<DelTrack> delTrackList = trackList.stream().sorted(Comparator.comparing(DelTrack::getNo).reversed()).collect(Collectors.toList());
                DelTrack delTrack = delTrackList.get(0);
                DelOutbound updateDelOutbound = new DelOutbound();
                updateDelOutbound.setId(delOutbound.getId());
                updateDelOutbound.setTrackingStatus(trackingYeeTraceDto.getTrackingStatus());
                updateDelOutbound.setTrackingDescription(delTrack.getDescription() + " ("+delTrack.getTrackingTime()+")");
                delOutboundMapper.updateById(updateDelOutbound);
            }
        }
    }


}

