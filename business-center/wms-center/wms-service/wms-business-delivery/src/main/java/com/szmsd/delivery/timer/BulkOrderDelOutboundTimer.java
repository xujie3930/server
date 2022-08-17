package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.LockerUtil;
import com.szmsd.delivery.vo.DelOutboundDetailVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
public class BulkOrderDelOutboundTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundService delOutboundService;
    @SuppressWarnings({"all"})
    @Autowired
    private PurchaseFeignService purchaseFeignService;
    @Resource
    private HtpOutboundFeignService htpOutboundFeignService;

    /**
     * 每天中午12:00，晚上19:00
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = " 0 */1 * * * ?")
    public void processing() {
        String key = applicationName + ":BulkOrderDelOutboundTimer:processing";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            // 指定查询的字段
            queryWrapper.select(DelOutbound::getSellerCode,
                    DelOutbound::getWarehouseCode,
                    DelOutbound::getOrderNo,
                    DelOutbound::getId);
            queryWrapper.eq(DelOutbound::getOrderType, DelOutboundOrderTypeEnum.BULK_ORDER.getCode());
            queryWrapper.isNull(DelOutbound::getPushOrderState);
            queryWrapper.ne(DelOutbound::getExceptionState, DelOutboundExceptionStateEnum.ABNORMAL.getCode());
            queryWrapper.last("limit 200");
            List<DelOutbound> delOutboundList = delOutboundService.list(queryWrapper);
            for (DelOutbound dataDelOutbound: delOutboundList) {

                DelOutboundVO delOutboundDto = delOutboundService.selectDelOutboundByOrderNo(dataDelOutbound.getOrderNo());
                try {
                    this.processData(delOutboundDto);

                    dataDelOutbound.setPushOrderState("1");
                } catch (Exception e){
                    dataDelOutbound.setExceptionState(DelOutboundExceptionStateEnum.ABNORMAL.getCode());
                    if(e.getMessage() != null && e.getMessage().length() > 200){
                        dataDelOutbound.setExceptionMessage(StringUtils.substring(e.getMessage() , 0, 200));
                    }else{
                        dataDelOutbound.setExceptionMessage(e.getMessage());
                    }
                    dataDelOutbound.setPushOrderState("2");

                }
                delOutboundService.updateById(dataDelOutbound);
            }
        });
    }

    private void processData(DelOutboundVO delOutboundDto){
        BulkOrderRequestDto bulkOrderRequestDto = new BulkOrderRequestDto();


        List<BulkOrderBoxRequestDto> boxList = new ArrayList<>();
        BulkOrderAddressRequestDto address = new BulkOrderAddressRequestDto();
        bulkOrderRequestDto.setAddress(address);
        bulkOrderRequestDto.setBoxList(boxList);

        BulkOrderTaskConfigDto taskConfigInfo = new BulkOrderTaskConfigDto();
        taskConfigInfo.setReceiveShippingType("NotReceive");
        taskConfigInfo.setIsPublishPackageMaterial(false);
        taskConfigInfo.setIsPublishPackageWeight(false);
        taskConfigInfo.setPrintShippingLabelType("NotPrint");

        BeanUtils.copyProperties(delOutboundDto, bulkOrderRequestDto);
        BeanUtils.copyProperties(delOutboundDto.getAddress(), address);


        bulkOrderRequestDto.setRefOrderNo(delOutboundDto.getOrderNo());
        bulkOrderRequestDto.setTaskConfig(taskConfigInfo);

        Set<String> boxSet =new TreeSet<String>();
        if(delOutboundDto.getDetails() != null){
            for(DelOutboundDetailVO detailVO : delOutboundDto.getDetails()){
                if(boxSet.contains(detailVO.getBoxMark())){
                    continue;
                }
                boxList.add(new BulkOrderBoxRequestDto().setBoxNo(detailVO.getBoxMark()));
                boxSet.add(detailVO.getBoxMark());
            }
        }



        R responseVO = htpOutboundFeignService.shipmentBoxtransfer(bulkOrderRequestDto);
        if(!(responseVO.getCode() == 200)){
            throw new CommonException("999", "同步大货订单失败"+ responseVO.getMsg());
        }
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
