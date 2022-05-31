package com.szmsd.ec.shopify.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.api.feign.BasSellerShopifyPermissionFeignService;
import com.szmsd.bas.api.feign.BasSkuRuleMatchingFeignService;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.ec.common.service.ICommonOrderService;
import com.szmsd.ec.constant.OrderStatusConstant;
import com.szmsd.ec.domain.ShopifyOrder;
import com.szmsd.ec.dto.ShopDTO;
import com.szmsd.ec.dto.ShopifyOrderDTO;
import com.szmsd.ec.dto.ShopifyOrderItemDTO;
import com.szmsd.ec.enums.OrderSourceEnum;
import com.szmsd.ec.shopify.config.ShopifyConfig;
import com.szmsd.ec.shopify.domain.order.*;
import com.szmsd.ec.shopify.service.ShopifyOrderService;
import com.szmsd.ec.shopify.util.GenericPropertyConverter;
import com.szmsd.ec.shopify.util.ShopifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @FileName ShopifyOrderTask.java
 * @Description ----------功能描述---------
 * @Date 2021-04-14 14:10
 * @Author hyx
 * @Version 1.0
 */
@Component
@Slf4j
public class ShopifyOrderTask {
    @Autowired
    ShopifyOrderService shopifyOrderService;
    @Autowired
    ICommonOrderService commonOrderService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    BasSellerShopifyPermissionFeignService basSellerShopifyPermissionFeignService;

    static final String LAST_TIME_KEY = "Shopify::lastGetOrderTime::";

    /**
     * 启动后延时30秒执行，让spring加载完其他的bean
     */
    private static final int DELAY_TIME=6*10*1000;

    /**
     * 任务执行间隔，默认10分钟
     */
    private static final int  PERIOD_TIME = 1000*60*10;

    @PostConstruct
    public void getShopifyList(){
        log.info("开始执行shopify 订单拉取");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        //是否在执行中
        AtomicBoolean isProgress = new AtomicBoolean(false);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //上一次未执行完,return
            if (isProgress.get()) {
                return;
            }
            isProgress.set(true);
            try {
                getShopifyListOrder(null);
                isProgress.set(false);
            } catch (Exception e) {
                log.error("获取shopify订单异常【{}】",e);
            } finally {
                // 任务执行不管有无完成将标志设为false
                isProgress.set(false);
            }
        }, DELAY_TIME, PERIOD_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 根据店铺名称拉取订单
     * @param shopName
     */
    public void getShopifyListOrderByShopName(String shopName){
        BasSellerShopifyPermission permission = new BasSellerShopifyPermission();
        permission.setShop(shopName);
        R<List<BasSellerShopifyPermission>> listR = basSellerShopifyPermissionFeignService.list(permission);
        if (listR == null || listR.getCode() != 200 || CollectionUtils.isEmpty(listR.getData())) {
            throw new BaseException("该店铺信息异常，无法拉取订单");
        }
        List<BasSellerShopifyPermission> shopifyPermissionList = listR.getData();
        pullShopifyOrder(shopifyPermissionList);
    }

    /**
     * 根据客户id 拉取订单
     * @param customerId
     */
    public void getShopifyListOrder(Long customerId){
        try {
            BasSellerShopifyPermission permission = new BasSellerShopifyPermission();
            permission.setSellerId(customerId);
            R<List<BasSellerShopifyPermission>> listR = basSellerShopifyPermissionFeignService.list(permission);
            if (listR == null || listR.getCode() != 200 || CollectionUtils.isEmpty(listR.getData())) {
                log.info("未获取到shopify店铺, 不执行拉取订单任务");
                return;
            }
            List<BasSellerShopifyPermission> shopifyPermissionList = listR.getData();
            log.info("获取【Shopify】店铺个数：{}", shopifyPermissionList.size());
            pullShopifyOrder(shopifyPermissionList);

//            redisTemplate.opsForValue().set(LAST_TIME_KEY, before);
        }catch (Exception e){
            log.error("【Shopify】获取订单列表异常"+e);
        }
    }

    /**
     * 拉取订单
     * @param shopifyPermissionList
     */
    private void pullShopifyOrder(List<BasSellerShopifyPermission> shopifyPermissionList){
        String before = new DateTime(new Date(), DateTimeZone.UTC).toString(ShopifyConfig.TIME_FORMAT_STR);
        shopifyPermissionList.forEach(shop -> {
            // 获取当前店铺最后的一个订单
            ShopifyOrder lastOrder = shopifyOrderService.getOne(new LambdaQueryWrapper<ShopifyOrder>().select(ShopifyOrder::getCreatedAt)
                    .eq(ShopifyOrder::getShopName, shop.getShop())
                    .orderByDesc(ShopifyOrder::getCreatedAt).last("limit 1"));
            String after = "";
            String createAfter = "";
            if (lastOrder != null){
                after = new DateTime(lastOrder.getCreatedAt(), DateTimeZone.UTC).toString(ShopifyConfig.TIME_FORMAT_STR);
                // 拉单的时候起始时间需要加一秒 不然订单重叠情况 没有最新的订单异常无法抛出
                createAfter = new DateTime(DateUtils.addSeconds(lastOrder.getCreatedAt(), 1), DateTimeZone.UTC).toString(ShopifyConfig.TIME_FORMAT_STR);
            }else {
                after = new DateTime(DateUtils.addYears(new Date(), -1), DateTimeZone.UTC).toString(ShopifyConfig.TIME_FORMAT_STR);
            }
            log.info("【shopify】店铺：{} 查询订单起始时间：{} - {}", shop.getShop(), after, before);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("updated_at_min", after);
            parameters.put("updated_at_max", before);
            parameters.put("created_at_min", createAfter);
            parameters.put("created_at_max", before);
            listOrder(shop,parameters);
        });
    }

    public void listOrder(BasSellerShopifyPermission shop, Map<String, String> parameters){
        String orderUrl = ShopifyConfig.orderUrl;
        String url = ShopifyConfig.HTTPS + shop.getShop() + orderUrl+ "?limit=200";
        //获取取消的订单和未发货的订单
        for (int i = 0; i < 2; i++) {
            String orderState = OrderStatusConstant.UNSHIPPED;
            if (i==0){
                //未发货订单
                url="&"+ url + "created_at_min="+parameters.get("created_at_min")+"&created_at_max="+parameters.get("created_at_max") + "&fulfillment_status=null&financial_status=paid";
            }
            if (i==1){
                orderState = OrderStatusConstant.CANCEL;
                url= "&"+ url + "updated_at_min="+parameters.get("updated_at_min")+"&updated_at_max="+parameters.get("updated_at_max") +"&status=cancelled";
            }
            log.info("【Shopify】店铺{}获取订单请求地址{}",shop.getShop(), url);
            System.out.println(url);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("X-Shopify-Access-Token", shop.getAccessToken());
            HttpResponseBody responseBody = HttpClientHelper.httpGet(url, "", headerMap);
            if(responseBody == null ){
                log.info("【Shopify】店铺{}获取订单接口请求失败",shop.getShop());
                return;
            }
            String result = responseBody.getBody();
//            String result = "{\"orders\":[{\"id\":3735774986405,\"admin_graphql_api_id\":\"gid:\\/\\/shopify\\/Order\\/3735774986405\",\"app_id\":580111,\"browser_ip\":\"174.105.183.192\",\"buyer_accepts_marketing\":false,\"cancel_reason\":null,\"cancelled_at\":null,\"cart_token\":null,\"checkout_id\":20645387108517,\"checkout_token\":\"704e0fbef689060fe819531dc323b0af\",\"client_details\":{\"accept_language\":\"en-US,en;q=0.9\",\"browser_height\":1304,\"browser_ip\":\"174.105.183.192\",\"browser_width\":2560,\"session_hash\":null,\"user_agent\":\"Mozilla\\/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/89.0.4389.114 Safari\\/537.36\"},\"closed_at\":null,\"confirmed\":true,\"contact_email\":\"jim@willeke.com\",\"created_at\":\"2021-04-11T02:31:09-07:00\",\"currency\":\"USD\",\"current_subtotal_price\":\"299.90\",\"current_subtotal_price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"current_total_discounts\":\"0.00\",\"current_total_discounts_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"current_total_duties_set\":null,\"current_total_price\":\"299.90\",\"current_total_price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"current_total_tax\":\"0.00\",\"current_total_tax_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"customer_locale\":\"en\",\"device_id\":null,\"discount_codes\":[],\"email\":\"jim@willeke.com\",\"financial_status\":\"paid\",\"fulfillment_status\":null,\"gateway\":\"shopify_payments\",\"landing_site\":\"\\/wallets\\/checkouts.json\",\"landing_site_ref\":null,\"location_id\":null,\"name\":\"#1002\",\"note\":null,\"note_attributes\":[],\"number\":2,\"order_number\":1002,\"order_status_url\":\"https:\\/\\/srsunriseus.com\\/53757870245\\/orders\\/3f1fa094a74f717157032b190a07030b\\/authenticate?key=c24688038d15b561609d5552c5d8fb5e\",\"original_total_duties_set\":null,\"payment_gateway_names\":[\"shopify_payments\"],\"phone\":\"+14195647692\",\"presentment_currency\":\"USD\",\"processed_at\":\"2021-04-11T02:31:08-07:00\",\"processing_method\":\"direct\",\"reference\":null,\"referring_site\":\"https:\\/\\/srsunriseus.com\\/products\\/brushed-nickel-shower-mixer-two-functions-valve-control-shower-faucet\",\"source_identifier\":null,\"source_name\":\"web\",\"source_url\":null,\"subtotal_price\":\"299.90\",\"subtotal_price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"tags\":\"\",\"tax_lines\":[],\"taxes_included\":false,\"test\":false,\"token\":\"3f1fa094a74f717157032b190a07030b\",\"total_discounts\":\"0.00\",\"total_discounts_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"total_line_items_price\":\"299.90\",\"total_line_items_price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"total_outstanding\":\"0.00\",\"total_price\":\"299.90\",\"total_price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"total_price_usd\":\"299.90\",\"total_shipping_price_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"total_tax\":\"0.00\",\"total_tax_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"total_tip_received\":\"0.00\",\"total_weight\":6010,\"updated_at\":\"2021-04-11T02:31:36-07:00\",\"user_id\":null,\"billing_address\":{\"first_name\":\"James\",\"address1\":\"21 Fairway Drive\",\"phone\":\"\",\"city\":\"Mount Vernon\",\"zip\":\"43050\",\"province\":\"Ohio\",\"country\":\"United States\",\"last_name\":\"Willeke\",\"address2\":\"\",\"company\":\"\",\"latitude\":40.4066619,\"longitude\":-82.45945689999999,\"name\":\"James Willeke\",\"country_code\":\"US\",\"province_code\":\"OH\"},\"customer\":{\"id\":5180141600933,\"email\":\"jim@willeke.com\",\"accepts_marketing\":false,\"created_at\":\"2021-04-11T02:26:52-07:00\",\"updated_at\":\"2021-04-11T02:31:09-07:00\",\"first_name\":\"James\",\"last_name\":\"Willeke\",\"orders_count\":1,\"state\":\"disabled\",\"total_spent\":\"299.90\",\"last_order_id\":3735774986405,\"note\":null,\"verified_email\":true,\"multipass_identifier\":null,\"tax_exempt\":false,\"phone\":null,\"tags\":\"\",\"last_order_name\":\"#1002\",\"currency\":\"USD\",\"accepts_marketing_updated_at\":\"2021-04-11T02:26:52-07:00\",\"marketing_opt_in_level\":null,\"admin_graphql_api_id\":\"gid:\\/\\/shopify\\/Customer\\/5180141600933\",\"default_address\":{\"id\":6359443112101,\"customer_id\":5180141600933,\"first_name\":\"James\",\"last_name\":\"Willeke\",\"company\":\"\",\"address1\":\"21 Fairway Drive\",\"address2\":\"\",\"city\":\"Mount Vernon\",\"province\":\"Ohio\",\"country\":\"United States\",\"zip\":\"43050\",\"phone\":\"\",\"name\":\"James Willeke\",\"province_code\":\"OH\",\"country_code\":\"US\",\"country_name\":\"United States\",\"default\":true}},\"discount_applications\":[],\"fulfillments\":[],\"line_items\":[{\"id\":9775122743461,\"admin_graphql_api_id\":\"gid:\\/\\/shopify\\/LineItem\\/9775122743461\",\"fulfillable_quantity\":1,\"fulfillment_service\":\"manual\",\"fulfillment_status\":null,\"gift_card\":false,\"grams\":6010,\"name\":\"16 Inch Brushed Nickel Ceiling Mounted Shower System\",\"origin_location\":{\"id\":2675252461733,\"country_code\":\"US\",\"province_code\":\"CA\",\"name\":\"SR SUNRISE\",\"address1\":\"15035 Proctor Avenue\",\"address2\":\"200\",\"city\":\"City of Industry\",\"zip\":\"91746\"},\"price\":\"299.90\",\"price_set\":{\"shop_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"299.90\",\"currency_code\":\"USD\"}},\"product_exists\":true,\"product_id\":6174072373413,\"properties\":[],\"quantity\":1,\"requires_shipping\":true,\"sku\":\"SRSH-BC1603\",\"taxable\":true,\"title\":\"16 Inch Brushed Nickel Ceiling Mounted Shower System\",\"total_discount\":\"0.00\",\"total_discount_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"variant_id\":37901664845989,\"variant_inventory_management\":\"shopify\",\"variant_title\":\"\",\"vendor\":\"SR SUNRISE\",\"tax_lines\":[],\"duties\":[],\"discount_allocations\":[]}],\"payment_details\":{\"credit_card_bin\":\"371555\",\"avs_result_code\":\"Y\",\"cvv_result_code\":\"M\",\"credit_card_number\":\"•••• •••• •••• 2007\",\"credit_card_company\":\"American Express\"},\"refunds\":[],\"shipping_address\":{\"first_name\":\"James\",\"address1\":\"21 Fairway Drive\",\"phone\":\"\",\"city\":\"Mount Vernon\",\"zip\":\"43050\",\"province\":\"Ohio\",\"country\":\"United States\",\"last_name\":\"Willeke\",\"address2\":\"\",\"company\":\"\",\"latitude\":40.4066619,\"longitude\":-82.45945689999999,\"name\":\"James Willeke\",\"country_code\":\"US\",\"province_code\":\"OH\"},\"shipping_lines\":[{\"id\":3179473109157,\"carrier_identifier\":null,\"code\":\"Economy\",\"delivery_category\":null,\"discounted_price\":\"0.00\",\"discounted_price_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"phone\":null,\"price\":\"0.00\",\"price_set\":{\"shop_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"},\"presentment_money\":{\"amount\":\"0.00\",\"currency_code\":\"USD\"}},\"requested_fulfillment_service_id\":null,\"source\":\"shopify\",\"title\":\"Economy\",\"tax_lines\":[],\"discount_allocations\":[]}]}]}";
            log.info("【Shopify】店铺{}获取订单返回结果{}",shop.getShop(),result);
            OrderResponse orderResponse = JSON.parseObject (result, OrderResponse.class, new ParserConfig() , JSONObject.DEFAULT_PARSER_FEATURE);
            final String  orderStateFinal = orderState;
            if (orderResponse!=null && CollectionUtils.isNotEmpty(orderResponse.getOrders())){
                orderResponse.getOrders().stream().forEach(order -> {
                    try {
                        ShopifyOrderDTO shopifyOrderDTO =getShopifyOrderDTO(order,orderStateFinal,shop);
                        int saveResult = shopifyOrderService.saveShopifyOrder(shopifyOrderDTO);
                        if (saveResult > 0) {
                            // 同步至公共中间表
                            commonOrderService.syncCommonOrder(OrderSourceEnum.Shopify, shopifyOrderDTO);
                        }

                    } catch (Exception e) {
                        log.info("【Shopify】店铺{}获取订单号{}异常",order.getId(),e);
                    }
                });
            }else {
                log.info("【Shopify】店铺{}获取订单返回结果无返回数据",shop.getShop());
                // 手动拉不到单的情况下
                if (i == 0){
                    throw new BaseException("没有最新的订单");
                }
            }

        }
    }

//    public static void main(String[] args) {
//        ShopDTO shopDTO = new ShopDTO();
//        shopDTO.setSecretKey("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");
//        shopDTO.setPlatformAccessId("7ea0b7860f53e210421005024bc7e71d");
//        shopDTO.setShopName("embather");
//        shopDTO.setVoucher("7ea0b7860f53e210421005024bc7e71d");
//        Map<String, String> parameters = new HashMap<>();
//        parameters.put("updated_at_min","2020-07-01T16:15:47-04:00");
//        parameters.put("updated_at_max", "2021-07-25T16:15:47-04:00");
//        ShopifyOrderTask shopifyOrderTask = new ShopifyOrderTask();
//        shopifyOrderTask.listOrder(shopDTO,parameters);
//    }
    public ShopifyOrderDTO getShopifyOrderDTO(ShopifyOrders order, String orderState, BasSellerShopifyPermission shop){
        ShopifyOrderDTO shopifyOrderDTO = GenericPropertyConverter.baseConvertObject(order, false,ShopifyOrderDTO.class,new HashMap<>(),new ArrayList<>());
        shopifyOrderDTO.setShopifyId(order.getId()+"");
        shopifyOrderDTO.setId(null);
        shopifyOrderDTO.setOrderStatus(orderState);
        ShopifyUtil.getTotalPrice(shopifyOrderDTO,order);
        ShopifyUtil.getTime(shopifyOrderDTO,order);
        //处理是否
        shopifyOrderDTO.setBuyerAcceptsMarketing(toStr(order.isBuyer_accepts_marketing()));
        shopifyOrderDTO.setConfirmed(toStr(order.isConfirmed()));
        shopifyOrderDTO.setTaxesIncluded(toStr(order.isTaxes_included()));
        shopifyOrderDTO.setTest(toStr(order.isTest()));
        //寄件地址
        ShopifyShippingAddress shippingAddress = order.getShipping_address();
        if (shippingAddress!=null){
            ShopifyUtil.getShippingAddress(shopifyOrderDTO,shippingAddress);
        }
        //账单地址
        BillingAddress billingAddress = order.getBilling_address();
        if (billingAddress!=null){
            ShopifyUtil.getBillingAddress(shopifyOrderDTO,billingAddress);
        }

        Customer customer = order.getCustomer();
        if (customer!=null){
            if (customer.getDefault_address()!=null){
                DefaultAddress defaultAddress = customer.getDefault_address();
                if (defaultAddress!=null){
                    ShopifyUtil.getDefaultAddress(shopifyOrderDTO,defaultAddress);
                }
            }
        }
        //支付信息
        PaymentDetails payment_details = order.getPayment_details();
        if (payment_details!=null){
            ShopifyUtil.getPaymentDetail(shopifyOrderDTO,payment_details);
        }
        shopifyOrderDTO.setCustomerId(shop.getSellerId() + "");
        shopifyOrderDTO.setCustomerName(shop.getSellerName());
        shopifyOrderDTO.setCustomerCode(shop.getSellerCode());
        shopifyOrderDTO.setShopId(shop.getId());
        shopifyOrderDTO.setShopName(shop.getShop());
        shopifyOrderDTO.setOrderNumber(order.getOrder_number());
        List<ShopifyOrderItemDTO> shopifyOrderItemDTOList = order.getLine_items().stream().map(lineItems -> {
            ShopifyOrderItemDTO shopifyOrderItemDTO = getShopifyOrderItemDTO(lineItems,shopifyOrderDTO.getShopifyId());
            return shopifyOrderItemDTO;
        }).collect(Collectors.toList());
        shopifyOrderDTO.setShopifyOrderItemDTOList(shopifyOrderItemDTOList);
        System.out.println(JSON.toJSONString(shopifyOrderDTO));
        return shopifyOrderDTO;
    }
    public ShopifyOrderItemDTO getShopifyOrderItemDTO(LineItems lineItem,String shopifyId){
        ShopifyOrderItemDTO shopifyOrderItemDTO = GenericPropertyConverter.baseConvertObject(lineItem, false,ShopifyOrderItemDTO.class,new HashMap<>(),new ArrayList<>());
        shopifyOrderItemDTO.setPrice(ShopifyUtil.changeTotal(lineItem.getPrice()));
        shopifyOrderItemDTO.setTotalDiscount(ShopifyUtil.changeTotal(lineItem.getTotal_discount()));
        shopifyOrderItemDTO.setShopifyId(shopifyId);
        shopifyOrderItemDTO.setItemId(lineItem.getId()+"");
        return shopifyOrderItemDTO;
    }


    public static String toStr(Object v) {
        if (v == null) {
            return null;
        }
        if(v instanceof String) {
            return (String) v;
        }

        return v.toString();
    }
}