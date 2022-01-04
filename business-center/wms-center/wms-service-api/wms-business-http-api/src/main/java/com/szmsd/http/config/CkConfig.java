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
     * 推送CK1 sku入库上架接口 推送sku的上架明细信息
     */
    String putawayUrl = "/v1/InventoryPutawayOrder/Putaway";
    /**
     * 推送CK1 创建入库单接口
     */
    String createPutawayOrderUrl = "/v1/InventoryPutawayOrder/Create";
    /**
     * 获取Sku库存编码
     * 推送CK1 创建入库单接口后生成对应CK1仓库的编码 ： 客户id+仓库+sku编码
     */
    String genSkuCustomStorageNo = "/v1/merchantSkus/storageno";
    /**
     * 推送CK1 入库单完成时 调用 来货单完成
     */
    String incomingOrderCompletedUrl = "/v1/InventoryPutawayOrder/{customerOrderNo}/finished";

    /**
     * 手动调整库存
     */
    String adjustInventoryUrl = "/v1/ManuallyAdjustSKUInventory";
    /**
     * 查询Sku在某个仓库的库存情况 核对库存
     */
    String checkInventoryUrl = "/v1/inventories";


    public String getIncomingOrderCompletedUrl(String customerOrderNo) {
        return incomingOrderCompletedUrl.replace("{customerOrderNo}", customerOrderNo);
    }
}
