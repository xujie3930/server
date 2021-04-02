package com.szmsd.returnex.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ConfigStatus
 * @Description: 配置参数
 * @Author: 11
 * @Date: 2021/4/2 10:15
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = ConfigStatus.CONFIG_PREFIX)
public class ConfigStatus {

    static final String CONFIG_PREFIX = "com.szmsd.returnex";
    /**
     * 过期未处理时间 退件列表超时配置
     */
    private Integer expirationDays = 7;
    /**
     * 处理状态枚举
     */
    private DealStatus dealStatus;

    /**
     * 退件单来源
     */
    private ReturnSource returnSource;


    @Data
    public static class DealStatus {
        private String wmsWaitReceive;
        private String wmsWaitReceiveStr = "处理中";
        private String waitCustomerDeal;
        private String waitCustomerDealStr = "待客户处理";
        private String waitAssigned;
        private String waitAssignedStr = "待指派";
        private String waitProcessedAfterUnpacking;
        private String waitProcessedAfterUnpackingStr = "待客户处理";
        private String wmsReceivedDealWay;
        private String wmsReceivedDealWayStr = "处理中";
        private String wmsFinish;
        private String wmsFinishStr = "已完成";

    }

    @Data
    public static class ReturnSource {
        /**
         * 退件预报
         */
        private String returnForecast;
        private String returnForecastStr="退件预报";
        /**
         * WMS通知退件
         */
        private String wmsReturn;
        private String wmsReturnStr = "WMS通知退件";
    }


}
