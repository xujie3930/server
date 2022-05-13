package com.szmsd.ec.common.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

//    @Autowired
//    private OrdOrderFeignService orderFeignService;

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

    /**
     * 保存订单
     * @param commonOrder
     */
    private void saveOrder(CommonOrder commonOrder){
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
            commonOrderItemMapper.delete(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, commonOrder.getId()));
            commonOrder.getCommonOrderItemList().forEach(item -> {
                item.setOrderId(commonOrder.getId());
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

}

