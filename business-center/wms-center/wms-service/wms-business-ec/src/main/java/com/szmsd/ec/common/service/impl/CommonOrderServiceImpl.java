package com.szmsd.ec.common.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasSub;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.BasSkuRuleMatchingFeignService;
import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.szmsd.bas.dto.BasDeliveryServiceMatchingDto;
import com.szmsd.bas.dto.BasSkuRuleMatchingDto;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelOutboundAddressDto;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.ec.common.mapper.CommonOrderItemMapper;
import com.szmsd.ec.common.mapper.CommonOrderMapper;
import com.szmsd.ec.common.service.ICommonOrderService;
import com.szmsd.ec.constant.OrderStatusConstant;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.domain.CommonOrderItem;
import com.szmsd.ec.dto.*;
import com.szmsd.ec.enums.OrderSourceEnum;
import com.szmsd.ec.enums.OrderStatusEnum;
import com.szmsd.ec.shopify.config.ShopifyConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 电商平台公共订单表 服务实现类
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
@Slf4j
@Service
public class CommonOrderServiceImpl extends ServiceImpl<CommonOrderMapper, CommonOrder> implements ICommonOrderService {

    @Autowired
    private CommonOrderItemMapper commonOrderItemMapper;
    @Autowired
    private BasSkuRuleMatchingFeignService basSkuRuleMatchingFeignService;
    @Autowired
    private DelOutboundFeignService delOutboundFeignService;
//    @Autowired
//    private OrdOrderFeignService orderFeignService;

    @Autowired
    private BasFeignService basFeignService;

    @Override
    @Transactional
    public <T> void syncCommonOrder(OrderSourceEnum orderSourceEnum, T t) {
        // 数据转换成对应的 bean
        CommonOrder commonOrder = new CommonOrder();
        switch (orderSourceEnum) {
            case Shopify:
                ShopifyOrderDTO shopifyOrderDTO = (ShopifyOrderDTO) t;
                transferShopify(commonOrder, shopifyOrderDTO);
                break;
            default:
                log.info("错误的订单来源：{}", orderSourceEnum.toString());
                return;
        }
        saveOrder(commonOrder);
    }


    @Override
    public List<LabelCountDTO> getCountByStatus(Wrapper<CommonOrder> queryWrapper) {
        return baseMapper.selectCountByStatus(queryWrapper);
    }

    @Override
    public void transferWarehouseOrder(CommonOrderDTO commonOrderDTO) {
        CommonOrder order = this.baseMapper.selectById(commonOrderDTO.getId());
        if (order == null) {
            throw new BaseException("订单不存在");
        }
        order.setCommonOrderItemList(commonOrderItemMapper.selectList(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, order.getId())));
//        OrdOrderBufferDto bufferDto = new OrdOrderBufferDto();
//        bufferDto.setCustomerCode(order.getCusCode());
//        bufferDto.setOrderNoField("cusOrderNo");
        // 推送到订单中心
//        pushCenterOrder(false, order, bufferDto);
    }

    @Override
    @Transactional
    public void orderShipping(List<Long> ids) {
        List<CommonOrder> commonOrderList = this.listByIds(ids);
        if (CollectionUtils.isEmpty(commonOrderList)) {
            throw new RuntimeException("未查询出订单数据");
        }

        // verify param
        List<String> warehouseList = commonOrderList.stream().filter(v -> StringUtils.isBlank(v.getWarehouseCode())).map(CommonOrder::getOrderNo).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            throw new RuntimeException("订单："+String.join(",", warehouseList)+"请完善发货仓库");
        }

        List<String> shipWarehouseList = commonOrderList.stream().filter(v -> v.getShippingWarehouseId() == null).map(CommonOrder::getOrderNo).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(shipWarehouseList)) {
            throw new RuntimeException("订单："+String.join(",", shipWarehouseList)+"请完善Shopify发货仓库");
        }

        List<String> shippingMethodList = commonOrderList.stream().filter(v -> StringUtils.isBlank(v.getShippingMethod())).map(CommonOrder::getOrderNo).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(shippingMethodList)) {
            throw new RuntimeException("订单："+String.join(",", shippingMethodList)+"请完善发货方式");
        }

        List<String> shippingSerivceList = commonOrderList.stream().filter(v -> StringUtils.isBlank(v.getShippingService())).map(CommonOrder::getOrderNo).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(shippingSerivceList)) {
            throw new RuntimeException("订单："+String.join(",", shippingSerivceList)+"请完善发货服务");
        }

        commonOrderList.forEach(order -> {
            DelOutboundDto dto = new DelOutboundDto();
            dto.setCustomCode(order.getCusCode());
            dto.setWarehouseCode(order.getWarehouseCode());

            dto.setSellerCode(SecurityUtils.getLoginUser().getSellerCode());
            dto.setOrderType(order.getShippingMethodCode());
            dto.setShopifyOrderNo(order.getOrderNo());
            dto.setRefNo(order.getOrderNo()); // todo refNo 后续根据自定义规则匹配传参  目前先按照订单号
            dto.setShipmentRule(order.getShippingServiceCode());

            DelOutboundAddressDto address = new DelOutboundAddressDto();
            address.setConsignee(order.getReceiver());
            address.setCountryCode(order.getReceiverCountryCode());
            address.setCountry(order.getReceiverCountryName());
            address.setStateOrProvince(order.getReceiverProvinceName());
            address.setCity(order.getReceiverCityName());
            address.setStreet1(order.getReceiverAddress1());
            address.setStreet2(order.getReceiverAddress2());
            address.setPostCode(order.getReceiverPostcode());
            address.setPhoneNo(order.getReceiverPhone());

            dto.setAddress(address);
            List<DelOutboundDetailDto> details = new ArrayList<>();

            // SKU 需根据匹配规则匹配出wms需要得sku
            List<CommonOrderItem> commonOrderItems = commonOrderItemMapper.selectList(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, order.getId()));
            if (CollectionUtils.isNotEmpty(commonOrderItems)) {
                // 转运类型不需要匹配SKU
                if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(order.getShippingServiceCode())) {
                    for (CommonOrderItem item : commonOrderItems) {
                        DelOutboundDetailDto detail = new DelOutboundDetailDto();
                        detail.setSku(item.getPlatformSku());
                        detail.setProductName(item.getTitle());
                        detail.setProductNameChinese(item.getTitle());
                        detail.setQty(item.getQuantity() != null ? item.getQuantity().longValue() : 0L);
                        detail.setRemark(item.getPlatformSku());
                        details.add(detail);
                    }
                }else {
                    BasSkuRuleMatchingDto requestDto = new BasSkuRuleMatchingDto();
                    requestDto.setSystemType("0");
                    requestDto.setSellerCode(order.getCusCode());
                    requestDto.setSourceSkuList(commonOrderItems.stream().map(CommonOrderItem::getPlatformSku).collect(Collectors.toList()));
                    R<List<BasSkuRuleMatching>> r = basSkuRuleMatchingFeignService.getList(requestDto);
                    List<BasSkuRuleMatching> ruleMatchingList = new ArrayList<>();
                    if (Constants.SUCCESS.equals(r.getCode()) && CollectionUtils.isNotEmpty(r.getData())) {
                        ruleMatchingList = r.getData();
                    }
                    for (CommonOrderItem item : commonOrderItems) {
                        BasSkuRuleMatching ruleMatching = ruleMatchingList.stream().filter(v -> item.getPlatformSku().equals(v.getSourceSku())).findFirst().orElse(null);
                        if (ruleMatching != null) {
                            String omsSku = ruleMatching.getOmsSku();
                            if (StringUtils.isNotBlank(omsSku)) {
                                String[] skuArr = omsSku.split(",");
                                for (String sku : skuArr) {
                                    DelOutboundDetailDto detail = new DelOutboundDetailDto();
                                    detail.setSku(sku);
                                    detail.setProductName(item.getTitle()); // shopify 商品名称就一个字段 不区分中英文
                                    detail.setProductNameChinese(item.getTitle());
                                    detail.setQty(item.getQuantity() != null ? item.getQuantity().longValue() : 0L);
                                    detail.setRemark(item.getPlatformSku());
                                    details.add(detail);
                                }
                            }
                            // 把映射出来的oms sku 存到remark 字段以备查验
                            item.setRemark(ruleMatching.getOmsSku());
                            commonOrderItemMapper.updateById(item);
                        }else {
    //                        DelOutboundDetailDto detailDto = new DelOutboundDetailDto();
    //                        detailDto.setSku(item.getPlatformSku());
    //                        detailDto.setProductName(item.getTitle());
    //                        detailDto.setQty(item.getQuantity().longValue());
    //                        detailDto.setRemark(item.getPlatformSku());
    //                        details.add(detailDto);
                            throw new RuntimeException(order.getOrderNo() + " 未进行SKU匹配");
                        }
                    }
                }
            }
            dto.setDetails(details);
            log.info("电商单发货请求参数：{}", JSON.toJSONString(dto));
            R<DelOutboundAddResponse> outboundAddResponseR = delOutboundFeignService.addShopify(dto);
            log.info("电商单发货响应结果：{}", JSON.toJSONString(outboundAddResponseR));
            if (Constants.SUCCESS != outboundAddResponseR.getCode() || outboundAddResponseR.getData() == null) {
                throw new RuntimeException("订单号："+order.getOrderNo()+"发货异常："+outboundAddResponseR.getMsg());
            }else if (outboundAddResponseR.getData() != null && !outboundAddResponseR.getData().getStatus()) {
                throw new RuntimeException("订单号："+order.getOrderNo()+"发货异常："+outboundAddResponseR.getData().getMessage());
            }
            // 更新订单状态为已发货
            order.setOmsOrderNo(outboundAddResponseR.getData().getOrderNo());
            order.setStatus(OrderStatusConstant.SHIPPED);
            order.setPushMethod("addShopify");
            order.setPushResultMsg(JSON.toJSONString(outboundAddResponseR));
            this.updateById(order);
        });

    }

    /**
     * 保存订单
     * @param commonOrder
     */
    private void saveOrder(CommonOrder commonOrder){
        List<CommonOrderItem> orderItemList = commonOrder.getCommonOrderItemList();
        // 自动匹配各种规则
        if (StringUtils.isBlank(commonOrder.getShippingServiceCode())) {
            if (CollectionUtils.isNotEmpty(orderItemList)) {
                List<String> skuList = orderItemList.stream().map(CommonOrderItem::getPlatformSku).collect(Collectors.toList());
                BasDeliveryServiceMatchingDto matchingDto = new BasDeliveryServiceMatchingDto();
                matchingDto.setSkuList(skuList);
                matchingDto.setSellerCode(commonOrder.getCusCode());
                log.info("查询发货服务匹配请求参数：{}", JSON.toJSONString(matchingDto));
                R<List<BasDeliveryServiceMatching>> deliveryServiceMatchingR = basSkuRuleMatchingFeignService.getList(matchingDto);
                log.info("查询到的发货服务匹配：{}", JSON.toJSONString(deliveryServiceMatchingR));
                if (Constants.SUCCESS.equals(deliveryServiceMatchingR.getCode()) && CollectionUtils.isNotEmpty(deliveryServiceMatchingR.getData())) {
                    List<BasDeliveryServiceMatching> matchingList = deliveryServiceMatchingR.getData();
                    // 如果查出多种规则 不自动匹配, 查出一种才匹配
                    if (matchingList.size() == 1) {
                        BasDeliveryServiceMatching serviceMatching = matchingList.get(0);
                        commonOrder.setWarehouseCode(serviceMatching.getWarehouseCode());
                        commonOrder.setWarehouseName(serviceMatching.getWarehouseName());
                        commonOrder.setShippingService(serviceMatching.getShipmentRule());
                        commonOrder.setShippingServiceCode(serviceMatching.getShipmentService());
                    }
                }
            }
        }
        if (StringUtils.isBlank(commonOrder.getShippingMethodCode())) {
            R<BasOtherRules> info = basSkuRuleMatchingFeignService.getInfo(commonOrder.getCusCode());
            if (Constants.SUCCESS.equals(info.getCode()) && info.getData() != null) {
                BasOtherRules rules = info.getData();
                BasSub sub = getSubByCode(rules.getDeliveryMethod());
                if (sub != null) {
                    commonOrder.setShippingMethodCode(sub.getSubValue());
                    commonOrder.setShippingMethod(sub.getSubName());
                }
            }
        }
        LambdaQueryWrapper<CommonOrder> queryWrapper =new LambdaQueryWrapper<CommonOrder>()
                .eq(CommonOrder::getOrderNo, commonOrder.getOrderNo()).last("limit 1");
        CommonOrder co = this.baseMapper.selectOne(queryWrapper);
//        OrdOrderBufferDto bufferDto = new OrdOrderBufferDto();
//        bufferDto.setCustomerCode(commonOrder.getCusCode());
//        bufferDto.setOrderNoField("cusOrderNo");
        if (co != null) {
            if(!OrderStatusEnum.UnShipped.toString().equals(co.getStatus())){
                return;
            }
            this.baseMapper.update(commonOrder, queryWrapper);
            commonOrderItemMapper.delete(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, co.getId()));
            commonOrder.getCommonOrderItemList().forEach(item -> {
                item.setOrderId(co.getId());
                commonOrderItemMapper.insert(item);
            });
            // 暂时关闭自动转单
//            pushCenterOrder(false, commonOrder, bufferDto);
        } else {
            this.baseMapper.insert(commonOrder);
            commonOrder.getCommonOrderItemList().forEach(item -> {
                item.setOrderId(commonOrder.getId());
                commonOrderItemMapper.insert(item);
            });

//            pushCenterOrder(true, commonOrder, bufferDto);
        }
    }

//    /**
//     * 推送到oms订单中心
//     * @param create
//     * @param commonOrder
//     * @param bufferDto
//     */
//    private void pushCenterOrder(Boolean create, CommonOrder commonOrder, OrdOrderBufferDto bufferDto){
//        PushWmsOrderDTO pushWmsOrderDTO = transferWmsOrder(commonOrder);
//        bufferDto.setParamList(Arrays.asList(pushWmsOrderDTO));
//        log.info("推单订单中心参数, Param：{}", JSON.toJSONString(bufferDto));
//        R<Integer> r = null;
//        String methodName = "";
//        if(create) {
//            r = orderFeignService.createOrderBuffer(bufferDto);
//            methodName = "createOrderBuffer";
//        }else {
//            r = orderFeignService.updateOrderBuffer(bufferDto);
//            methodName = "updateOrderBuffer";
//        }
//        if (r == null || r.getCode() != 200) {
//            log.info("推单订单中心失败, Method：{}；Result：{}", methodName, JSON.toJSONString(r));
//            commonOrder.setStatus(OrderStatusEnum.Exception.toString());
//            commonOrder.setTransferErrorMsg(r.getMsg());
//        }else {
//            log.info("推单订单中心成功, Method：{}；Result：{}", methodName, JSON.toJSONString(r));
//        }
//        commonOrder.setPushMethod(methodName);
//        commonOrder.setPushResultMsg(JSON.toJSONString(r));
//        this.baseMapper.updateById(commonOrder);
//    }

    private PushWmsOrderDTO transferWmsOrder(CommonOrder commonOrder){
        PushWmsOrderDTO pushWmsOrderDTO = JSONObject.parseObject(JSON.toJSONString(commonOrder), PushWmsOrderDTO.class);
        pushWmsOrderDTO.setPlatformOrderNumber(commonOrder.getPlatformOrderNumber());
        pushWmsOrderDTO.setCusOrderNo(commonOrder.getOrderNo());
        pushWmsOrderDTO.setReceiverAddress(commonOrder.getReceiverAddress1());
        pushWmsOrderDTO.setValueAmount(commonOrder.getAmount());
        pushWmsOrderDTO.setValueAmountCurrencyName(commonOrder.getCurrency());
        return pushWmsOrderDTO;
    }

    /**
     * shopify 订单转换
     *
     * @param commonOrder
     * @param shopifyOrderDTO
     */
    public void transferShopify(CommonOrder commonOrder, ShopifyOrderDTO shopifyOrderDTO) {
        commonOrder.setCusId(shopifyOrderDTO.getCustomerId());
        commonOrder.setCusCode(shopifyOrderDTO.getCustomerCode());
        commonOrder.setCusName(shopifyOrderDTO.getCustomerName());
        commonOrder.setShopId(shopifyOrderDTO.getShopId());
        commonOrder.setShopName(shopifyOrderDTO.getShopName());
        commonOrder.setOrderNo(shopifyOrderDTO.getShopifyId());
        commonOrder.setPlatformOrderNumber(shopifyOrderDTO.getOrderNumber() + "");
        commonOrder.setOrderDate(shopifyOrderDTO.getCreatedAt());
        commonOrder.setOrderSource(OrderSourceEnum.Shopify.toString());
        commonOrder.setStatus(shopifyOrderDTO.getOrderStatus());
        commonOrder.setSalesChannels(shopifyOrderDTO.getShippingSource());
        commonOrder.setWarehouseCode(shopifyOrderDTO.getWarehouseCode());
        commonOrder.setWarehouseName(shopifyOrderDTO.getPreWarehouseName());
        commonOrder.setReceiver(shopifyOrderDTO.getShippingAddressFirstName());
        commonOrder.setReceiverPhone(shopifyOrderDTO.getShippingAddressPhone());
        commonOrder.setReceiverCountryName(shopifyOrderDTO.getShippingAddressCountry());
        commonOrder.setReceiverCountryCode(shopifyOrderDTO.getShippingAddressCountryCode());
        commonOrder.setReceiverProvinceName(shopifyOrderDTO.getShippingAddressProvince());
        commonOrder.setReceiverProvinceCode(shopifyOrderDTO.getShippingAddressProvinceCode());
        commonOrder.setReceiverCityName(shopifyOrderDTO.getShippingAddressCity());
        commonOrder.setReceiverAddress1(shopifyOrderDTO.getShippingAddressAddress1());
        commonOrder.setReceiverAddress2(shopifyOrderDTO.getShippingAddressAddress2());
        commonOrder.setReceiverPostcode(shopifyOrderDTO.getShippingAddressZip());
        commonOrder.setShippingChannel(shopifyOrderDTO.getShippingTitle());
        commonOrder.setAmount(new BigDecimal(shopifyOrderDTO.getCurrentTotalPrice()));
        commonOrder.setCurrency(shopifyOrderDTO.getPresentmentCurrency());
        commonOrder.setCreateTime(shopifyOrderDTO.getCreatedAt());
        commonOrder.setUpdateTime(shopifyOrderDTO.getUpdatedAt());
        commonOrder.setPlatformOrderStatus(OrderStatusConstant.OrderPlatformStatusConstant.APPROVED);

        List<CommonOrderItem> orderItemList = new ArrayList<>();
        List<ShopifyOrderItemDTO> itemDTOList = shopifyOrderDTO.getShopifyOrderItemDTOList();
        itemDTOList.forEach(item -> {
            CommonOrderItem orderItem = new CommonOrderItem();
            orderItem.setItemId(item.getItemId());
            orderItem.setOrderNo(item.getShopifyId());
            orderItem.setTitle(item.getTitle());
            orderItem.setPlatformSku(item.getSku());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(new BigDecimal(item.getPrice()));
            orderItem.setDeclareValue(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
            orderItemList.add(orderItem);
        });
        commonOrder.setCommonOrderItemList(orderItemList);
    }


    /**
     * wooCommerce 订单转换为标准订单
     * @param commonOrder
     * @param wooCommerceOrderDTO
     */
    private void transferWooCommerce(CommonOrder commonOrder, WooCommerceOrderDTO wooCommerceOrderDTO) {
        commonOrder.setCusName(wooCommerceOrderDTO.getCusName());
        commonOrder.setCusCode(wooCommerceOrderDTO.getCusCode());
        commonOrder.setCusId(wooCommerceOrderDTO.getCusId());
        commonOrder.setShopId(wooCommerceOrderDTO.getShopId());
        commonOrder.setShopName(wooCommerceOrderDTO.getShopName());

        commonOrder.setOrderNo(wooCommerceOrderDTO.getWooCommerceOrderId());
        commonOrder.setPlatformOrderNumber(wooCommerceOrderDTO.getNumber());
        commonOrder.setOrderDate(wooCommerceOrderDTO.getDateCreatedGmt());
        commonOrder.setOrderSource(OrderSourceEnum.WooCommerce.toString());

        //完成的需要设置为unShipped
        commonOrder.setStatus(ShopifyConfig.UNSHIPPED);

        commonOrder.setCurrency(wooCommerceOrderDTO.getCurrency());

        commonOrder.setReceiver(wooCommerceOrderDTO.getShipFirstName() + (StringUtils.isNotEmpty(wooCommerceOrderDTO.getShipLastName()) ? " " + wooCommerceOrderDTO.getShipLastName() : ""));
        commonOrder.setReceiverAddress1(wooCommerceOrderDTO.getShipAddress1());
        commonOrder.setReceiverAddress2(wooCommerceOrderDTO.getShipAddress2());
        commonOrder.setReceiverCityName(wooCommerceOrderDTO.getShipCity());
        commonOrder.setReceiverProvinceCode(wooCommerceOrderDTO.getShipState());
        commonOrder.setReceiverPostcode(wooCommerceOrderDTO.getShipPostcode());
        commonOrder.setReceiverCountryCode(wooCommerceOrderDTO.getShipCountry());

        //商品小计= total- shipping_total-total_tax， 并且需要把商品小计的值放到出库预报里面的申明价值里
        BigDecimal total = new BigDecimal(Optional.ofNullable(wooCommerceOrderDTO.getTotal()).orElse("0.0"));
        BigDecimal shippingTotal = new BigDecimal(Optional.ofNullable(wooCommerceOrderDTO.getShippingTotal()).orElse("0.0"));
        BigDecimal totalTax = new BigDecimal(Optional.ofNullable(wooCommerceOrderDTO.getTotalTax()).orElse("0.0"));
        commonOrder.setAmount(total.subtract(shippingTotal).subtract(totalTax));

        commonOrder.setRemark(wooCommerceOrderDTO.getCustomerNote());
        commonOrder.setPlatformOrderStatus(OrderStatusConstant.OrderPlatformStatusConstant.APPROVED);

        commonOrder.setOrderVia(wooCommerceOrderDTO.getCreatedVia());


        //明细
        List<WooCommerceOrderItemDTO> orderItems = wooCommerceOrderDTO.getOrderItems();
        List<CommonOrderItem> orderItemList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderItems)) {
            orderItems.forEach(item->{
                CommonOrderItem orderItem = new CommonOrderItem();
                orderItem.setItemId(item.getItemId());
                orderItem.setOrderNo(item.getWooCommerceOrderId());
                orderItem.setTitle(item.getName());
                orderItem.setPlatformSku(item.getSku());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(new BigDecimal(item.getPrice()));
                orderItem.setDeclareValue(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
                orderItemList.add(orderItem);
            });
        }
        commonOrder.setCommonOrderItemList(orderItemList);

    }


    /**
     * 根据code获取主子类别名称
     * @param code
     * @return
     */
    private BasSub getSubByCode(String code) {
        BasSub basSub = new BasSub();
        basSub.setSubCode(code);
        R<List<BasSub>> feignServiceSub = basFeignService.getsub(basSub);
        if (Constants.SUCCESS.equals(feignServiceSub.getCode()) && feignServiceSub.getData() != null) {
            List<BasSub> data = feignServiceSub.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                return data.get(0);
            }
        }
        return null;
    }

}

