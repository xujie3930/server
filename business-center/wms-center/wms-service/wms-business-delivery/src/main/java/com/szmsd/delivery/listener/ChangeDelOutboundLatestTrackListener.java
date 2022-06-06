package com.szmsd.delivery.listener;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.delivery.event.ChangeDelOutboundLatestTrackEvent;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.pack.api.feign.PackageCollectionFeignService;
import com.szmsd.pack.constant.PackageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 同步出库单状态or最新轨迹信息
 */
@Slf4j
@Component
public class ChangeDelOutboundLatestTrackListener {

    @Autowired
    private DelOutboundMapper delOutboundMapper;

    @Autowired
    private PackageCollectionFeignService packageCollectionFeignService;

    @Async
    @EventListener
    public void onApplicationEvent(ChangeDelOutboundLatestTrackEvent event){
        log.info("进入同步出库单轨迹监听器");
        DelTrack track = (DelTrack) event.getSource();
        if (track == null) {
            return;
        }
        // 如果LS开头的单号则为揽收单 修改揽收单的状态
        if (track.getOrderNo().startsWith(PackageConstant.LS_PREFIX)) {
            // 已妥投修改揽收单状态为完成
            if ("Delivered".equalsIgnoreCase(track.getTrackingStatus())) {
                packageCollectionFeignService.updateCollectingCompleted(track.getOrderNo());
            } else {
                packageCollectionFeignService.updateCollecting(track.getOrderNo());
            }
        } else {
            DelOutbound delOutbound = delOutboundMapper.selectOne(new LambdaQueryWrapper<DelOutbound>().eq(DelOutbound::getOrderNo, track.getOrderNo()).last("limit 1"));
            if (delOutbound != null) {
                DelOutbound updateDelOutbound = new DelOutbound();
                updateDelOutbound.setId(delOutbound.getId());
                updateDelOutbound.setTrackingStatus(track.getTrackingStatus());
                // 最新时间
                updateDelOutbound.setTrackingTime(track.getTrackingTime());
                updateDelOutbound.setTrackingDescription(track.getDescription() + " (" + DateUtil.format(track.getTrackingTime(), DateUtils.YYYY_MM_DD_HH_MM_SS) + ")");
                delOutboundMapper.updateById(updateDelOutbound);
            }
        }
    }
}
