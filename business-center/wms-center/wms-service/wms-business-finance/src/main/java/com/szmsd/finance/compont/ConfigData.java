package com.szmsd.finance.compont;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @ClassName: ConfigData
 * @Description:
 * @Author: 11
 * @Date: 2021-09-09 13:58
 */
@Slf4j
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = ConfigData.CONFIG_PREFIX)
public class ConfigData {
    static final String CONFIG_PREFIX = "com.szmsd.fss.api";

    private MainSubCode mainSubCode;

    @Data
    public static class MainSubCode {
        /**
         * 处理性质 087
         */
        private String treatmentProperties = "025";
        /**
         * 责任地区
         */
        private String responsibilityArea = "089";
        /**
         * 业务类型
         */
        private String businessType = "012";
        /**
         * 业务明细
         */
        private String businessDetails;
        /**
         * 费用类型
         */
        private String typesOfFee="038";
        /**
         * 费用类别
         */
        private String costCategory;
        /**
         * 币种
         */
        private String currency = "008";
        /**
         * 属性
         */
        private String property = "037";

    }

    @PostConstruct
    private void afterInit() {
        log.info(JSONObject.toJSONString(this));
    }
}
