package com.szmsd.http.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: CkConfig
 * @Description: CK1配置
 * @Author: 11
 * @Date: 2021-12-13 16:42
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = CkConfig.CONFIG_PREFIX)
public class CkConfig {

    static final String CONFIG_PREFIX = "com.szmsd.http.ck";
    /**
     * ck1 token
     */
    String token = "Bearer OGE0M2UzNmItMzVhMy00MDY5LWJkMjgtMWIwYjQ4ZmQ3YmM0";
    /**
     * 仓库查询url
     */
    String warHouseListUrl = "http://openapi.ck1info.com/v1/warehouses";
    /**
     * 推送CK1 入库上架接口
     */
    String putawayUrl= "/v1/InventoryPutawayOrder/Putaway";
    /**
     * 推送CK1 创建入库单接口
     */
    String createPutawayOrderUrl= "/v1/InventoryPutawayOrder/Create";


}
