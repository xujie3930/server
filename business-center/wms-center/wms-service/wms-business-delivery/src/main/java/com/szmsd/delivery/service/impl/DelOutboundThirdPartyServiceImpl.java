package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.api.factory.BasFeignServiceFallbackFactory;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.mapper.DelOutboundThirdPartyMapper;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.http.dto.ShipmentOrderResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* <p>
    * 出库单临时第三方任务表 服务实现类
    * </p>
*
* @author admin
* @since 2022-08-22
*/
@Service
public class DelOutboundThirdPartyServiceImpl extends ServiceImpl<DelOutboundThirdPartyMapper, DelOutboundThirdParty> implements IDelOutboundThirdPartyService {
    private static final Logger logger = LoggerFactory.getLogger(DelOutboundThirdPartyServiceImpl.class);

    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;

    @Override
    public List<DelOutboundThirdParty> selectDelOutboundThirdPartyList(DelOutboundThirdParty delOutboundThirdParty) {

        LambdaQueryWrapper<DelOutboundThirdParty> where = new LambdaQueryWrapper<DelOutboundThirdParty>();
        List<String> orderNoList = new ArrayList<>();
        where.eq(DelOutboundThirdParty::getOperationType, DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_WMS);
//        if(StringUtils.isNotEmpty(delOutboundThirdParty.getOrderNo())){
//            where.eq(DelOutboundThirdParty::getOrderNo, delOutboundThirdParty.getOrderNo());
//        }
        if (StringUtils.isNotEmpty(delOutboundThirdParty.getOrderNoes())) {
            List<String> nos = splitToArray(delOutboundThirdParty.getOrderNoes(), "[\n,]");
            if (CollectionUtils.isNotEmpty(nos)) {
                for (String no : nos) {

                    orderNoList.add(no);

                }


            }
            delOutboundThirdParty.setOrderNosList(orderNoList);
            where.in(DelOutboundThirdParty::getOrderNo,delOutboundThirdParty.getOrderNosList());
        }
        if(StringUtils.isNotEmpty(delOutboundThirdParty.getState())){
            where.eq(DelOutboundThirdParty::getState, delOutboundThirdParty.getState());
        }
        if (StringUtils.isNotEmpty(delOutboundThirdParty.getStartDate()) && StringUtils.isNotEmpty(delOutboundThirdParty.getEndDate())) {
            where.between(DelOutboundThirdParty::getCreateTime, delOutboundThirdParty.getStartDate(), delOutboundThirdParty.getEndDate());
        }
        if (delOutboundThirdParty.getIds()!=null&&delOutboundThirdParty.getIds().size()>0){
            where.in(DelOutboundThirdParty::getId,delOutboundThirdParty.getIds());
        }
        where.orderByDesc(DelOutboundThirdParty::getCreateTime);
        return baseMapper.selectList(where);
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }


    @Override
    public void thirdParty(String orderNo, String amazonLogisticsRouteId) {



        StopWatch stopWatch = new StopWatch();

        // 创建亚马逊承运商物流订单
        IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
        stopWatch.start();
        DelOutbound dataDelOutbound = delOutboundService.getByOrderNo(orderNo);
        dataDelOutbound.setAmazonLogisticsRouteId(amazonLogisticsRouteId);
        DelOutboundWrapperContext delOutboundWrapperContext = this.delOutboundBringVerifyService.initContext(dataDelOutbound);
        ShipmentOrderResult shipmentOrderResult = null ;
        try {
            shipmentOrderResult = delOutboundBringVerifyService.shipmentAmazonOrder(delOutboundWrapperContext);
            stopWatch.stop();
            logger.info("创建亚马逊承运商[{}]物流订单成功{},耗时{}", dataDelOutbound.getAmazonLogisticsRouteId(), dataDelOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
        }catch (Exception e){
            stopWatch.stop();
            logger.info("创建亚马逊承运商[{}]物流订单失败{},耗时{}", dataDelOutbound.getAmazonLogisticsRouteId(), dataDelOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
            throw new RuntimeException(e);
        }
        IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
        DelOutbound updateDelOutbound = new DelOutbound();
        updateDelOutbound.setId(dataDelOutbound.getId());
        updateDelOutbound.setAmazonLogisticsRouteId(shipmentOrderResult.getMainTrackingNumber());
        delOutboundService.updateById(updateDelOutbound);
        DelOutboundOperationLogEnum.BRV_SHIPMENT_AMAZON_ORDER.listener(dataDelOutbound);



    }

    @Override
    public void thirdWMS(String orderNo) {

        DelOutbound delOutbound = delOutboundService.getByOrderNo(orderNo);

        if(delOutbound == null){
            return;
        }

        //该订单全部接收完成后，调用PRC
        ApplicationContext context = delOutboundBringVerifyService.initContext(delOutbound);
        ApplicationContainer applicationContainer = new ApplicationContainer(context, BringVerifyEnum.SHIPMENT_CREATE, BringVerifyEnum.END, BringVerifyEnum.SHIPMENT_CREATE);
        applicationContainer.action();
    }

    @Transactional
    @Override
    public void fail(Long id, String remark) {
        DelOutboundThirdParty  modifyDelOutboundCompleted = new DelOutboundThirdParty();
        modifyDelOutboundCompleted.setId(id);
        // 修改状态为失败
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.FAIL.getCode());
        if (remark.length() > 500) {
            remark = remark.substring(0, 200);
        }
        modifyDelOutboundCompleted.setRemark(remark);
        // 处理次数累加，下一次处理时间 = 系统当前时间 + 5
        this.baseMapper.updateRecord(modifyDelOutboundCompleted);
    }

    @Transactional
    @Override
    public void success(Long id) {
        DelOutboundThirdParty modifyDelOutboundCompleted = new DelOutboundThirdParty();
        modifyDelOutboundCompleted.setId(id);
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.SUCCESS.getCode());
        this.updateById(modifyDelOutboundCompleted);
    }



}

