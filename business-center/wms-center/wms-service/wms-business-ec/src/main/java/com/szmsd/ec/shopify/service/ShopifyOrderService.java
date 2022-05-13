package com.szmsd.ec.shopify.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.ec.domain.ShopifyOrder;
import com.szmsd.ec.dto.FulfillmentDTO;
import com.szmsd.ec.dto.ShopifyOrderDTO;
import com.szmsd.ec.shopify.domain.fulfillment.CreateFulfillmentReqeust;
import com.szmsd.ec.vo.FulfillmentVO;

import java.util.List;
import java.util.Map;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrder
 * @Description: oms_shopify_order shopify订单表Service
 * @date 2021-04-14 13:58:31
 */
public interface ShopifyOrderService extends IService<ShopifyOrder> {

    List<ShopifyOrderDTO> listPage(ShopifyOrderDTO queryDTO);

    /**
     * 保存订单值shopify订单表
     * @param shopifyOrderDTO
     * @author hyx
     * @date 2021-04-14 17:56
     * @throws
     * @return int
     */
    int saveShopifyOrder(ShopifyOrderDTO shopifyOrderDTO);
    /**
     * 推送物流信息至Shopify
     * @param fulfillmentDtos
     * @author hyx
     * @date 2021-04-20 17:56
     * @throws
     * @return java.util.List<com.szmsd.oms.ec.express.dto.FulfillmentVo>
     */
    List<FulfillmentVO> putFulfillmentToShopify(List<FulfillmentDTO> fulfillmentDtos);

    /**
     *
     * 创建履约单
     * @return
     */
    JSONObject createFulfillment(String shopName, String orderNo, CreateFulfillmentReqeust createFulfillmentReqeust);

    /**
     * 自动保存订单到出库单
     * @param ebayOrderDTO ebayOrderDTO
     */
    void updateOrder(ShopifyOrderDTO ebayOrderDTO);

    Map<String, Object> getCountByStatus(ShopifyOrderDTO queryDTO);
}