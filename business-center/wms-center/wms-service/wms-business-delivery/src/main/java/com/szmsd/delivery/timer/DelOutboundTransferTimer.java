package com.szmsd.delivery.timer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.util.LockerUtil;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import com.szmsd.inventory.domain.dto.TransportWarehousingAddDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 转运入库特定客户，每天定时生成一条数据
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class DelOutboundTransferTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundService delOutboundService;

    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;

    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @SuppressWarnings({"all"})
    @Autowired
    private IDelTrackService delTrackService;
    @Autowired
    private SerialNumberClientService serialNumberClientService;

    /**
     * 每天8点执行一条创建语句
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    public void processing() {
        //定时生成订单
        String key = applicationName + ":DelOutboundTransferTimer:processing";
        this.doWorker(key, () -> {

            DelOutbound delOutbound = JSON.parseObject("{\"warehouseCode\":\"SP\",\"warehouseName\":null,\"orderType\":\"PackageTransfer\",\"orderTypeName\":\"转运出库\",\"sellerCode\":\"CNW759\",\"trackingNo\":\"\",\"shipmentRule\":\"XXX\",\"packingRule\":\"\",\"remark\":\"\",\"refNo\":\"\",\"refOrderNo\":\"\",\"isPackingByRequired\":false,\"isFirst\":false,\"newSku\":\"\",\"newSkuName\":null,\"customCode\":\"CNW759\",\"state\":\"DELIVERED\",\"deliveryMethod\":\"\",\"deliveryTime\":null,\"deliveryAgent\":\"\",\"deliveryInfo\":\"\",\"packageConfirm\":\"076001\",\"packageWeightDeviation\":0,\"length\":1.0,\"width\":1.0,\"height\":1.0,\"weight\":59.5,\"specifications\":\"1.0*1.0*1.0\",\"shipmentChannel\":\"\",\"isDefaultWarehouse\":false,\"isLabelBox\":false,\"boxNumber\":0,\"ioss\":\"\",\"codAmount\":0.00,\"availableInventory\":null,\"packings\":null,\"containerList\":null,\"combinations\":null,\"documentsFiles\":null,\"calcWeight\":0.0000,\"calcWeightUnit\":\"\",\"shipmentRetryLabel\":null,\"uploadBoxLabel\":null,\"amazonLogisticsRouteId\":null,\"houseNo\":null,\"delDays\":null,\"trackingDays\":null,\"checkFlag\":null,\"queryseShipmentDays\":null,\"querysetrackStayDays\":null,\"sourceType\":\"ADD\"}",
                    DelOutbound.class);

            DelOutboundDetail delOutboundDetail = JSON.parseObject("{\"warehouseCode\":\"SP\",\"warehouseName\":null,\"orderType\":\"PackageTransfer\",\"orderTypeName\":\"转运出库\",\"sellerCode\":\"CNY373\",\"trackingNo\":\"\",\"shipmentRule\":\"XXX\",\"packingRule\":\"\",\"remark\":\"\",\"refNo\":\"\",\"refOrderNo\":\"\",\"isPackingByRequired\":false,\"isFirst\":false,\"newSku\":\"\",\"newSkuName\":null,\"customCode\":\"CNW759\",\"state\":\"REVIEWED\",\"deliveryMethod\":\"\",\"deliveryTime\":null,\"deliveryAgent\":\"\",\"deliveryInfo\":\"\",\"packageConfirm\":\"076001\",\"packageWeightDeviation\":0,\"length\":1.0,\"width\":1.0,\"height\":1.0,\"weight\":59.5,\"specifications\":\"1.0*1.0*1.0\",\"shipmentChannel\":\"\",\"isDefaultWarehouse\":false,\"isLabelBox\":false,\"boxNumber\":0,\"ioss\":\"\",\"codAmount\":0.00,\"availableInventory\":null,\"address\":{\"consignee\":\"Stephanie Mccrillis\",\"countryCode\":\"US\",\"country\":\"United States\",\"zone\":\"\",\"stateOrProvince\":\"Louisville\",\"city\":\"Kentucky\",\"street1\":\"5342 Poindexter Dr Apt D\",\"street2\":\"\",\"street3\":\"\",\"postCode\":\"40291\",\"phoneNo\":\"14157959984\",\"email\":\"3047671706@qq.com\"},\"details\":[{\"sku\":\"\",\"qty\":1,\"newLabelCode\":\"\",\"availableInventory\":null,\"productName\":\"dress\",\"initWeight\":null,\"initLength\":null,\"initWidth\":null,\"initHeight\":null,\"initVolume\":null,\"weight\":null,\"length\":null,\"width\":null,\"height\":null,\"volume\":null,\"bindCode\":\"\",\"bindCodeName\":null,\"productNameChinese\":\"裙子\",\"declaredValue\":5.0,\"productDescription\":null,\"productAttribute\":\"GeneralCargo\",\"productAttributeName\":null,\"electrifiedMode\":\"\",\"electrifiedModeName\":null,\"batteryPackaging\":\"\",\"batteryPackagingName\":null,\"hsCode\":null,\"orderNo\":\"CKCNY37322092600000008\",\"editionImage\":null,\"ioss\":null,\"boxMark\":null,\"skuFile\":null,\"boxMarkFile\":null,\"brandFlag\":null,\"brandUrl\":null,\"boxLength\":null,\"boxWidth\":null,\"boxHeight\":null,\"boxWeight\":null,\"operationType\":null}],\"packings\":null,\"containerList\":null,\"combinations\":null,\"documentsFiles\":null,\"calcWeight\":0.0000,\"calcWeightUnit\":\"\",\"shipmentRetryLabel\":null,\"uploadBoxLabel\":null,\"amazonLogisticsRouteId\":null,\"houseNo\":null,\"delDays\":null,\"trackingDays\":null,\"checkFlag\":null,\"queryseShipmentDays\":null,\"querysetrackStayDays\":null}",
                    DelOutboundDetail.class);

            DelOutboundAddress delOutboundAddress = JSON.parseObject("{\"warehouseCode\":\"SP\",\"warehouseName\":null,\"orderType\":\"PackageTransfer\",\"orderTypeName\":\"转运出库\",\"sellerCode\":\"CNY373\",\"trackingNo\":\"\",\"shipmentRule\":\"XXX\",\"packingRule\":\"\",\"remark\":\"\",\"refNo\":\"\",\"refOrderNo\":\"\",\"isPackingByRequired\":false,\"isFirst\":false,\"newSku\":\"\",\"newSkuName\":null,\"customCode\":\"CNW759\",\"state\":\"REVIEWED\",\"deliveryMethod\":\"\",\"deliveryTime\":null,\"deliveryAgent\":\"\",\"deliveryInfo\":\"\",\"packageConfirm\":\"076001\",\"packageWeightDeviation\":0,\"length\":1.0,\"width\":1.0,\"height\":1.0,\"weight\":59.5,\"specifications\":\"1.0*1.0*1.0\",\"shipmentChannel\":\"\",\"isDefaultWarehouse\":false,\"isLabelBox\":false,\"boxNumber\":0,\"ioss\":\"\",\"codAmount\":0.00,\"availableInventory\":null,\"address\":{\"consignee\":\"Stephanie Mccrillis\",\"countryCode\":\"US\",\"country\":\"United States\",\"zone\":\"\",\"stateOrProvince\":\"Louisville\",\"city\":\"Kentucky\",\"street1\":\"5342 Poindexter Dr Apt D\",\"street2\":\"\",\"street3\":\"\",\"postCode\":\"40291\",\"phoneNo\":\"14157959984\",\"email\":\"3047671706@qq.com\"},\"details\":[{\"sku\":\"\",\"qty\":1,\"newLabelCode\":\"\",\"availableInventory\":null,\"productName\":\"dress\",\"initWeight\":null,\"initLength\":null,\"initWidth\":null,\"initHeight\":null,\"initVolume\":null,\"weight\":null,\"length\":null,\"width\":null,\"height\":null,\"volume\":null,\"bindCode\":\"\",\"bindCodeName\":null,\"productNameChinese\":\"裙子\",\"declaredValue\":5.0,\"productDescription\":null,\"productAttribute\":\"GeneralCargo\",\"productAttributeName\":null,\"electrifiedMode\":\"\",\"electrifiedModeName\":null,\"batteryPackaging\":\"\",\"batteryPackagingName\":null,\"hsCode\":null,\"orderNo\":\"CKCNY37322092600000008\",\"editionImage\":null,\"ioss\":null,\"boxMark\":null,\"skuFile\":null,\"boxMarkFile\":null,\"brandFlag\":null,\"brandUrl\":null,\"boxLength\":null,\"boxWidth\":null,\"boxHeight\":null,\"boxWeight\":null,\"operationType\":null}],\"packings\":null,\"containerList\":null,\"combinations\":null,\"documentsFiles\":null,\"calcWeight\":0.0000,\"calcWeightUnit\":\"\",\"shipmentRetryLabel\":null,\"uploadBoxLabel\":null,\"amazonLogisticsRouteId\":null,\"houseNo\":null,\"delDays\":null,\"trackingDays\":null,\"checkFlag\":null,\"queryseShipmentDays\":null,\"querysetrackStayDays\":null}",
                    DelOutboundAddress.class);

            String prefix = "CK";
            delOutbound.setOrderNo(prefix + "CNW759" + serialNumberClientService.generatorNumber(SerialNumberConstant.DEL_OUTBOUND_NO));
            delOutbound.setTrackingNo(delOutbound.getOrderNo());
            delOutbound.setCreateTime(new Date());
            delOutbound.setSourceType("time");
            delOutboundService.getBaseMapper().insert(delOutbound);


            delOutboundDetail.setOrderNo(delOutbound.getOrderNo());
            delOutboundDetail.setCreateTime(new Date());

            delOutboundDetailService.getBaseMapper().insert(delOutboundDetail);

            delOutboundAddress.setOrderNo(delOutbound.getOrderNo());
            delOutboundAddress.setCreateTime(new Date());
            delOutboundAddressService.getBaseMapper().insert(delOutboundAddress);


        });
    }
    @Async
    public void processing2() {
        //定时生成轨迹
        String key = applicationName + ":DelOutboundTransferTimer:processing2";
        this.doWorker(key, () -> {

            //第一天的轨迹
            processing2(new Date(), "Purchasing", "Order details confirmed， preparing product");

            //第四天
            processing2(addAndSubtractDaysByGetTime(new Date(), -4), "Purchasing", "Packing");

            //第八天
            processing2(addAndSubtractDaysByGetTime(new Date(), -8), "Purchasing", "Packed and shipping to transit warehouse");

        });
    }
    public static Date addAndSubtractDaysByGetTime(Date dateTime/*待处理的日期*/,int n/*加减天数*/){
        return new Date(dateTime.getTime() + n * 24 * 60 * 60 * 1000L);
    }

    private void processing2(Date date, String trackingStatus, String description){

        QueryWrapper<DelOutbound> queryWrapper = new QueryWrapper();
        queryWrapper.eq("seller_code", "CNW759");
        queryWrapper.eq("source_type", "time");

        queryWrapper.eq("date_format(create_time,'%Y-%m-%d')", DateUtil.format(date, "yyyy-MM-dd"));
        List<DelOutbound> list = delOutboundService.list(queryWrapper);
        if(list.isEmpty()){
            return;
        }
        List<DelTrack> delTrackList = new ArrayList<DelTrack>();

        for (DelOutbound delOutbound: list){
            DelTrack delTrack = new DelTrack();
            delTrack.setSource("1");
            delTrack.setTrackingNo(delOutbound.getOrderNo());
            delTrack.setTrackingStatus(trackingStatus);
            delTrack.setDescription(description);
            delTrack.setTrackingTime(new Date());
            delTrack.setOrderNo(delOutbound.getOrderNo());
            delTrack.setCreateTime(new Date());
            delTrackList.add(delTrack);

            delOutbound.setTrackingStatus(trackingStatus);
        }
        delTrackService.saveBatch(delTrackList);
        delOutboundService.updateBatchById(list);

    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
