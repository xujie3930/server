package com.szmsd.returnex.config;

import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    /**
     * 处理方式
     * processType(code):WMS processType
     */
    private HashMap<String, String> returnProcessingMethod;
    /**
     * 拆包明细
     */
    private String unpackingInspection;
    /**
     * 按明细上架
     */
    private String putawayByDetail;
    /**
     * 整包上架
     */
    private String wholePackageOnShelves;
    /**
     * 销毁
     */
    private String destroy;

    /**
     * 通过OMS中的主子类别的code 获取相对应的WMS操作类型
     *
     * @param code OMS 子类别code
     * @return WMS操作类型
     */
    public String getPrCode(String code) {
        return Optional.ofNullable(returnProcessingMethod.get(code)).filter(StringUtils::isNotEmpty).orElseThrow(() -> new BaseException("暂未配置该类型的处理方式"));
    }

    @Data
    public static class DealStatus {
        //等待接收货物
        private String wmsWaitReceive;
        private String wmsWaitReceiveStr = "处理中";
        private String waitCustomerDeal;
        private String waitCustomerDealStr = "待客户处理";
        private String waitAssigned;
        private String waitAssignedStr = "待指派";
        private String waitProcessedAfterUnpacking;
        private String waitProcessedAfterUnpackingStr = "待客户处理";
        //等待WMS按客户处理方式处理
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
        private String returnForecastStr = "退件预报";
        /**
         * WMS通知退件
         */
        private String wmsReturn;
        private String wmsReturnStr = "WMS通知退件";
    }


}
