package com.szmsd.ec.shopify.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.feign.BasSellerShopifyPermissionFeignService;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.ec.constant.OrderStatusConstant;
import com.szmsd.ec.domain.ShopifyOrder;
import com.szmsd.ec.domain.ShopifyOrderItem;
import com.szmsd.ec.dto.FulfillmentDTO;
import com.szmsd.ec.dto.ShopifyOrderDTO;
import com.szmsd.ec.dto.ShopifyOrderItemDTO;
import com.szmsd.ec.shopify.config.ShopifyConfig;
import com.szmsd.ec.shopify.domain.fulfillment.CreateFulfillmentReqeust;
import com.szmsd.ec.shopify.domain.fulfillment.Fulfillment2;
import com.szmsd.ec.shopify.domain.ship.ShopifyFulfillment;
import com.szmsd.ec.shopify.domain.ship.ShopifyFulfillmentRequest;
import com.szmsd.ec.shopify.domain.ship.ShopifyLineItem;
import com.szmsd.ec.shopify.enums.ShopifyOrderFieldEnum;
import com.szmsd.ec.shopify.mapper.ShopifyOrderItemMapper;
import com.szmsd.ec.shopify.mapper.ShopifyOrderMapper;
import com.szmsd.ec.shopify.service.ShopifyOrderService;
import com.szmsd.ec.vo.FulfillmentVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrder
 * @Description: oms_shopify_order shopify订单表ServiceImpl
 * @date 2021-04-14 13:58:32
 */
@Service
@Slf4j
@Transactional(rollbackFor = BaseException.class)
public class ShopifyOrderServiceImpl extends ServiceImpl<ShopifyOrderMapper, ShopifyOrder> implements ShopifyOrderService {

    @Autowired
    ShopifyOrderItemMapper shopifyOrderItemMapper;
    @Autowired
    private ShopifyOrderMapper shopifyOrderMapper;
    @Autowired
    private BasSellerShopifyPermissionFeignService basSellerShopifyPermissionFeignService;

    @Override
    public int saveShopifyOrder(ShopifyOrderDTO shopifyOrderDTO) {
        LambdaUpdateWrapper<ShopifyOrder> updateWrapper = new LambdaUpdateWrapper<ShopifyOrder>()
                .eq(ShopifyOrder::getShopifyId, shopifyOrderDTO.getShopifyId());
        ShopifyOrder ao=this.baseMapper.selectOne(updateWrapper);
        ShopifyOrder shopifyOrder = BeanMapperUtil.map(shopifyOrderDTO, ShopifyOrder.class);
        int flag;
        if (ao != null) {
            if(!ao.getOrderStatus().equals(OrderStatusConstant.UNSHIPPED)){
                return 0;
            }
            flag = this.baseMapper.update(shopifyOrder, updateWrapper);
            LambdaUpdateWrapper<ShopifyOrderItem> queryWrapper = new LambdaUpdateWrapper<ShopifyOrderItem>()
                    .eq(ShopifyOrderItem::getShopifyId, shopifyOrder.getShopifyId());
            shopifyOrderItemMapper.delete(queryWrapper);
            shopifyOrderDTO.getShopifyOrderItemDTOList().forEach(shopifyOrderItemDTO -> {
                ShopifyOrderItem shopifyOrderItem = BeanMapperUtil.map(shopifyOrderItemDTO, ShopifyOrderItem.class);
                shopifyOrderItem.setShopifyOrderId(shopifyOrder.getId());
                shopifyOrderItemMapper.insert(shopifyOrderItem);
            });
        } else {
            flag =this.baseMapper.insert(shopifyOrder);
            shopifyOrderDTO.getShopifyOrderItemDTOList().forEach(shopifyOrderItemDTO -> {
                ShopifyOrderItem shopifyOrderItem = BeanMapperUtil.map(shopifyOrderItemDTO, ShopifyOrderItem.class);
                shopifyOrderItem.setShopifyOrderId(shopifyOrder.getId());
                shopifyOrderItemMapper.insert(shopifyOrderItem);
            });
        }
        return flag;
    }
    /**
     * 分页查询
     *
     * @param tDTO
     * @return
     */
    @Override
    public List<ShopifyOrderDTO> listPage(ShopifyOrderDTO tDTO){
        QueryWrapper<ShopifyOrder> queryWrapper = Wrappers.query();
        queryWrapper.orderByDesc(ShopifyOrderFieldEnum.CREATED_AT.getDataFieldName());
        if (StringUtils.isNotEmpty(tDTO.getOrderStatus())){
            queryWrapper.eq(ShopifyOrderFieldEnum.ORDER_STATUS.getDataFieldName(),tDTO.getOrderStatus());
        }
        if(tDTO.getCustomerId() != null){
            queryWrapper.eq(ShopifyOrderFieldEnum.CUSTOMER_ID.getDataFieldName(),tDTO.getCustomerId());
        }
        QueryWrapperUtil.filterDate(queryWrapper, ShopifyOrderFieldEnum.CREATED_AT.getDataFieldName(), tDTO.getCreateDates());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, ShopifyOrderFieldEnum.SHOPIFY_ID.getDataFieldName(), tDTO.getShopifyId());
        List<ShopifyOrder> resultList = this.baseMapper.selectList(queryWrapper);
        List<ShopifyOrderDTO> orderDTOS = BeanMapperUtil.mapList(resultList, ShopifyOrderDTO.class);
        orderDTOS.forEach(
                shopifyOrderDTO -> {
                    Wrapper<ShopifyOrderItem> stcQueryWrapper = new LambdaQueryWrapper<ShopifyOrderItem>().eq(ShopifyOrderItem::getShopifyId, shopifyOrderDTO.getShopifyId());
                    List<ShopifyOrderItem> amazonOrderItemList = shopifyOrderItemMapper.selectList(stcQueryWrapper);

                    //已发货表明有跟踪号
//                    BasCustomerVO customerVO = basCustomerClientService.get(shopifyOrderDTO.getCustomerCode());
//                    if (customerVO != null) {
//                        shopifyOrderDTO.setCustomerShortName(customerVO.getCustomerShortName());
//                    }
                    List<ShopifyOrderItemDTO> shopifyOrderItemDTOList = new ArrayList<>();
                    amazonOrderItemList.forEach(shopifyOrderItem -> {
                        ShopifyOrderItemDTO shopifyOrderItemDTO = new ShopifyOrderItemDTO();
                        BeanUtils.copyProperties(shopifyOrderItem,shopifyOrderItemDTO);
//                        if(shopifyOrderDTO.getOrderStatus().equals(AmazonConfig.SHIPPED)){
//                            String orderCode = shopifyOrderItem.getItemId();
//                            LambdaUpdateWrapper<Express> updateWrapper = Wrappers.lambdaUpdate(Express.class).eq(Express::getExpressCode, orderCode);
//                            Express express = expressService.getOne(updateWrapper);
//                            shopifyOrderItemDTO.setCarrierName(express.getCarrierName());
//                            shopifyOrderItemDTO.setTransCompName(express.getTransCompName());
//                            shopifyOrderItemDTO.setTransferMainNumber(express.getTransferMainNumber());
//                        }
                        shopifyOrderItemDTOList.add(shopifyOrderItemDTO);
                    });
                    shopifyOrderDTO.setShopifyOrderItemDTOList(shopifyOrderItemDTOList);
                }
        );
        return orderDTOS;
    }

    @Override
    public List<FulfillmentVO> putFulfillmentToShopify(List<FulfillmentDTO> fulfillmentDtos) {
        List<FulfillmentVO> list = fulfillmentDtos.stream().map(fulfillmentDto -> {
            FulfillmentVO fulfillmentVo = new FulfillmentVO();
            fulfillmentVo.setOrderNo(fulfillmentDto.getOrderNo());
            try {
                ShopifyFulfillmentRequest shopifyFulfillmentRequest = new ShopifyFulfillmentRequest();
                List<String> tracking_numbers = new ArrayList<>();
                List<ShopifyLineItem> line_items = new ArrayList<>();
                List<String> tracking_urls = new ArrayList<>();
                fulfillmentDto.getFulfillmentDetailDtos().forEach(
                        fulfillmentDetailDto -> {
                            tracking_numbers.add(fulfillmentDetailDto.getExpressCode());
                            tracking_urls.add(fulfillmentDetailDto.getTrackingURL());
                            ShopifyLineItem lineItem = new ShopifyLineItem();
                            lineItem.setId(Integer.valueOf(fulfillmentDetailDto.getOrderItemId()));
                            line_items.add(lineItem);
                        }
                );
                fulfillmentVo.setStatus(true);
                ShopifyFulfillment shopifyFulfillment = new ShopifyFulfillment();
                //Location_id 这个暂时不知道是哪一个,用接口去查询。这有这个
                shopifyFulfillment.setLocation_id(59860549797L);
                shopifyFulfillment.setLine_items(line_items);
                shopifyFulfillment.setTracking_company(fulfillmentDto.getFulfillmentDetailDtos().get(0).getTransCompName());
                shopifyFulfillment.setTracking_numbers(tracking_numbers);
                shopifyFulfillment.setTracking_urls(tracking_urls);
                shopifyFulfillmentRequest.setFulfillment(shopifyFulfillment);
                String data = JSON.toJSONString(shopifyFulfillmentRequest);
                log.info("【Shopify回传物流信息】单号="+fulfillmentDto.getOrderNo()+"请求报文==》"+data);
                LambdaQueryWrapper<ShopifyOrder> queryWrapper =
                        new LambdaQueryWrapper<ShopifyOrder>().eq(ShopifyOrder::getShopifyId,fulfillmentDto.getOrderNo());
                ShopifyOrder shopifyOrder = baseMapper.selectOne(queryWrapper);
                if (shopifyOrder == null) {
                    log.info("Shopify订单未找到");
                    throw new BaseException("订单未找到");
                }
                BasSellerShopifyPermission permission = new BasSellerShopifyPermission();
                permission.setShop(shopifyOrder.getShopName());
                R<List<BasSellerShopifyPermission>> listR = basSellerShopifyPermissionFeignService.list(permission);
                if (listR == null || listR.getCode() != 200 || CollectionUtils.isEmpty(listR.getData())) {
                    log.info("{}店铺信息异常，无法操作接口", shopifyOrder.getShopName());
                    throw new BaseException(shopifyOrder.getShopName()+"店铺信息异常，无法操作接口");
                }
                List<BasSellerShopifyPermission> listRData = listR.getData();
                BasSellerShopifyPermission shopifyPermission = listRData.get(0);
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("X-Shopify-Access-Token", shopifyPermission.getAccessToken());

                String url = ShopifyConfig.HTTPS + shopifyPermission.getShop() + ShopifyConfig.fulfillmentUrl.replace("{ORDER_ID}",fulfillmentDto.getOrderNo());

                HttpResponseBody responseBody = HttpClientHelper.httpPost(url, data, headerMap);
                String result = responseBody.getBody();
                log.info("【Shopify回传物流信息】单号="+fulfillmentDto.getOrderNo()+"返回结果==》"+result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (StringUtils.isEmpty(jsonObject.get("error").toString())){
                    fulfillmentVo.setStatus(true);
                }else {
                    fulfillmentVo.setStatus(false);
                    fulfillmentVo.setDesc(String.valueOf(jsonObject.get("error")));
                }
            } catch (Exception e) {
                fulfillmentVo.setStatus(false);
                fulfillmentVo.setDesc("系统异常");
            }
            return fulfillmentVo;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public JSONObject createFulfillment(String shopName, String orderNo, CreateFulfillmentReqeust createFulfillmentReqeust) {
        String data = JSON.toJSONString(createFulfillmentReqeust);
        log.info("【Shopify 创建履约单】Request： "+ data);
        BasSellerShopifyPermission permission = new BasSellerShopifyPermission();
        permission.setShop(shopName);
        R<List<BasSellerShopifyPermission>> listR = basSellerShopifyPermissionFeignService.list(permission);
        if (listR == null || listR.getCode() != 200 || CollectionUtils.isEmpty(listR.getData())) {
            log.info("{}店铺信息异常，无法操作接口", shopName);
            throw new BaseException(shopName+"店铺信息异常，无法操作接口");
        }
        List<BasSellerShopifyPermission> listRData = listR.getData();
        BasSellerShopifyPermission shopifyPermission = listRData.get(0);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Shopify-Access-Token", shopifyPermission.getAccessToken());
        String url = ShopifyConfig.HTTPS + shopifyPermission.getShop() + ShopifyConfig.fulfillmentUrl.replace("{ORDER_ID}", orderNo);

        // 复制locationId
        Fulfillment2 fulfillment = (Fulfillment2)createFulfillmentReqeust.getFulfillment();
//        fulfillment.setLocationId(ShopifyConfig.getLocationId(shopifyPermission.getShop(), shopifyPermission.getAccessToken()));
        String locations = shopifyPermission.getLocations();
        if (StringUtils.isNotBlank(locations)) {
            String[] splitLocationIds = locations.split(",");
            fulfillment.setLocationId(splitLocationIds[0]); // 取一个
        }
        data = JSON.toJSONString(createFulfillmentReqeust);
        log.info("【Shopify 创建履约单】请求URL: {}", url);
        HttpResponseBody responseBody = HttpClientHelper.httpPost(url, data, headerMap);
        String result = responseBody.getBody();
        log.info("【Shopify 创建履约单】Response： "+ result);
        JSONObject resultObject = JSONObject.parseObject(result);
        return resultObject;
    }

    @Override
    public void updateOrder(ShopifyOrderDTO orderDTO) {

        LambdaUpdateWrapper<ShopifyOrder> updateWrapper = new LambdaUpdateWrapper<ShopifyOrder>()
                .eq(ShopifyOrder::getShopifyId, orderDTO.getShopifyId());
        ShopifyOrder ao = this.baseMapper.selectOne(updateWrapper);
        if (!ShopifyConfig.UNSHIPPED.equals(ao.getOrderStatus())) {
            throw new BaseException("订单处理中,请确认");
        }
//        Map<String, Object> resultMap = toShopifyOrderList(orderDTO);
//        if(!CollectionUtils.isEmpty(resultMap) && resultMap.containsKey("error")){
//            throw new BaseException((String) resultMap.get("error"));
//        }
//        List<ShopifyOrderDTO> shopifyOrderDTOList = (List<ShopifyOrderDTO>) resultMap.get("success");
//        this.saveOrUpdateWarehouseOut(shopifyOrderDTOList, true);
        ao.setOrderStatus(OrderStatusConstant.DELIVERING);
        shopifyOrderMapper.updateById(ao);

    }

//    /**
//     * TODO推送亚马逊订单 订单池数据入库管理
//     *
//     * @param shopifyOrderDTOList shopifyOrderDTOList
//     * @throws
//     * @return: void
//     * @author Huang Yixian
//     * @date 2020-08-24 20:02
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void saveOrUpdateWarehouseOut(List<ShopifyOrderDTO> shopifyOrderDTOList, Boolean pushFlag) {
//        for (ShopifyOrderDTO shopifyOrderDTO:shopifyOrderDTOList) {
//            WarehouseOutDTO warehouseDTO = new WarehouseOutDTO();
//            BeanUtils.copyProperties(shopifyOrderDTO, warehouseDTO);
//            warehouseDTO.setAllocationStatus("0");
//            warehouseDTO.setPushFlag(pushFlag);
//            warehouseDTO.setAmazonOrderId(shopifyOrderDTO.getShopifyId());
//            if (shopifyOrderDTO.getCurrentTotalPrice() != null) {
//                warehouseDTO.setAmount(shopifyOrderDTO.getCurrentTotalPrice().toString());
//            }
//            warehouseDTO.setAuditStatus("0");
//            warehouseDTO.setChannel(SourceConstant.SHOPIFY);
//            warehouseDTO.setConsignee(shopifyOrderDTO.getShippingAddressName());
//            warehouseDTO.setReceivingPhone(shopifyOrderDTO.getShippingAddressPhone());
////            warehouseDTO.setLatestDeliveryTime();
//            warehouseDTO.setIsCod("0");
////            warehouseDTO.setBuyerEmail(amazonOrderDTO.getBuyerEmail());
//            warehouseDTO.setOrderType("2");
//            warehouseDTO.setOrderNo(shopifyOrderDTO.getShopifyId());
//            warehouseDTO.setExpressCompany(shopifyOrderDTO.getTransCompCode());
//            warehouseDTO.setClientName(shopifyOrderDTO.getCustomerName());
//            warehouseDTO.setCustomerNo(shopifyOrderDTO.getCustomerCode());
////            warehouseDTO.setShippingAddress(amazonOrderDTO.getShippingAddressCountryCode() + "," + amazonOrderDTO.getShippingAddressStateOrregion() + "," + amazonOrderDTO.getShippingAddressCity() + "," + amazonOrderDTO.getShippingAddressAddressLine1() + "," + amazonOrderDTO.getShippingAddressPostalCode());
//            warehouseDTO.setShippingAddressCountryCode(shopifyOrderDTO.getShippingAddressCountryCode());
//            warehouseDTO.setShippingAddressStateOrregion(shopifyOrderDTO.getShippingAddressProvinceCode());
//            warehouseDTO.setShippingAddressCity(shopifyOrderDTO.getShippingAddressCity());
////            warehouseDTO.setShippingAddressCounty(amazonOrderDTO.getShippingAddressCounty());
//            warehouseDTO.setShippingAddressPostalCode(shopifyOrderDTO.getShippingAddressZip());
//            warehouseDTO.setShippingAddressLine1(shopifyOrderDTO.getShippingAddressAddress1());
////            warehouseDTO.setShippingAddressLine2(amazonOrderDTO.getShippingAddressAddressLine2());
////            warehouseDTO.setShippingAddressLine3(amazonOrderDTO.getShippingAddressAddressLine3());
//            warehouseDTO.setShippingAddressPhone(shopifyOrderDTO.getShippingAddressPhone());
////            warehouseDTO.setLastDeliveryTime(amazonOrderDTO.getLatestDeliveryDate());
////            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////            String format1 = format.format(amazonOrderDTO.getLatestShipDate());
////            warehouseDTO.setEarliestDeliveryDate(format1);
//            List<ShopifyOrderItemDTO> shopifyOrderItemDTOList = shopifyOrderDTO.getShopifyOrderItemDTOList();
//            List<WarehouseOutGoodsDTO> warehouseGoodsDTOList = shopifyOrderItemDTOList.stream().map(amazonOrderItemDTO -> {
//                WarehouseOutGoodsDTO warehouseGoodsDTO = new WarehouseOutGoodsDTO();
//                warehouseGoodsDTO.setSkuCode(amazonOrderItemDTO.getSku());
//                warehouseGoodsDTO.setSku(amazonOrderItemDTO.getSku());
//                warehouseGoodsDTO.setAsin("");
//                warehouseGoodsDTO.setGoodsNo(amazonOrderItemDTO.getItemId());
//                warehouseGoodsDTO.setItemPrice(amazonOrderItemDTO.getPrice().toString());
//                warehouseGoodsDTO.setProductName(amazonOrderItemDTO.getTitle());
//                warehouseGoodsDTO.setQuantity(amazonOrderItemDTO.getQuantity()+"");
//                warehouseGoodsDTO.setOrderNo(shopifyOrderDTO.getShopifyId());
//
//                return warehouseGoodsDTO;
//            }).collect(Collectors.toList());
//
//            warehouseDTO.setWarehouseOutGoodsDTOList(warehouseGoodsDTOList);
//            WarehouseOut warehouseOut = warehouseOutConvert.sourceToTarget(warehouseDTO);
//
//            LambdaUpdateWrapper<WarehouseOut> updateWrapper = Wrappers.lambdaUpdate(WarehouseOut.class)
//                    .eq(WarehouseOut::getAmazonOrderId, warehouseOut.getAmazonOrderId());
//            if (warehouseOutService.count(updateWrapper) > 0) {
//                warehouseOutService.update(warehouseDTO);
//            } else {
//                warehouseOutService.save(warehouseDTO);
//            }
//            //回写拆单号到电商sku信息中
//            shopifyOrderItemDTOList.forEach( shopifyOrderItemDTO -> {
//                shopifyOrderItemDTO.setOrderCode(shopifyOrderDTO.getShopifyId());
//                QueryWrapper<ShopifyOrderItem> wrapper = new QueryWrapper<>();
//                wrapper.eq("shopify_id",shopifyOrderDTO.getShopifyId());
//                wrapper.eq("item_id",shopifyOrderItemDTO.getItemId());
//                ShopifyOrderItem orderItem = new ShopifyOrderItem();
//                BeanUtils.copyProperties(shopifyOrderItemDTO,orderItem);
//                orderItem.setSku(shopifyOrderItemDTO.getOldSku());
//                orderItem.setWarehouseId(warehouseDTO.getWarehouseId());
//                orderItem.setWarehouseName(warehouseDTO.getWarehouseName());
//                shopifyOrderItemMapper.update(orderItem,wrapper);
//            });
//        }
//    }

//    /**
//     * 亚马逊订单根据sku选仓库拆分多个仓库出库单
//     */
//    public Map<String,Object> toShopifyOrderList(ShopifyOrderDTO tDTO) {
//        List<ShopifyOrderDTO> shopifyOrderList = new ArrayList<>();
//        List<ShopifyOrderItemDTO> itemList = tDTO.getShopifyOrderItemDTOList();
//        Map<String,Object> resultMap = new HashMap<>();
//        if (itemList != null) {
//            //取映射关系
//            List<String> listSku = itemList.stream().map(ShopifyOrderItemDTO::getSku).collect(Collectors.toList());
//            Map<String, BaseSkuMatchDTO> skuMap = getSkuMapByPlatformSku(String.valueOf(tDTO.getCustomerId()),listSku);
//            if (itemList.size() > 1) {
//                //临时存放订单对象
//                Map<String,ShopifyOrderDTO> dtoMap = new HashMap<>();
//                for (ShopifyOrderItemDTO itemDto:itemList) {
//                    if(!skuMap.containsKey(itemDto.getSku())){
//                        resultMap.put("error","Shopify订单sku："+itemDto.getSku()+"未做sku映射");
//                        return resultMap;
//                    }
//                    BaseSkuMatchDTO skuMatch = skuMap.get(itemDto.getSku());
//                    if(skuMatch == null){
//                        resultMap.put("error","系统无当前SKU："+itemDto.getSku()+"记录,请确认");
//                        return resultMap;
//                    }
//                    itemDto.setOldSku(itemDto.getOldSku());
//                    itemDto.setSku(skuMatch.getSkuCode());
//                    WarehouseRepertoryDTO warehouseRepertoryDTO = new WarehouseRepertoryDTO();
//                    warehouseRepertoryDTO.setTotalPackages(itemDto.getQuantity());
//                    warehouseRepertoryDTO.setSku(skuMatch.getSkuCode());
//                    warehouseRepertoryDTO.setZipCode(tDTO.getBillingAddressZip());
//                    WarehouseRepertoryDTO dto = null;
//                    try {
//                        List<WarehouseRepertoryDTO> warehouseRepertoryDTOS = repertorySkuFeignService.selectWarehouseRepertoryList(warehouseRepertoryDTO);
//                        dto = warehouseRepertoryDTOS.stream().filter(repertoryDto -> repertoryDto.getOptimalFlag() == true).findFirst().get();
//                    } catch (Exception e) {
//                        log.info("调用wms库存服务失败",e);
//                    }
//                    if(dto == null){
//                        resultMap.put("error","Shopify订单sku："+itemDto.getSku()+"无库存");
//                        return resultMap;
//                    }
//                    if(dtoMap.containsKey(dto.getWarehouseId())){
//                        ShopifyOrderDTO shopifyOrderDTO = dtoMap.get(dto.getWarehouseId());
//                        List<ShopifyOrderItemDTO> shopifyOrderItemDTOList = shopifyOrderDTO.getShopifyOrderItemDTOList();
//                        shopifyOrderItemDTOList.add(itemDto);
//                    } else {
//                        ShopifyOrderDTO shopifyOrderDTO = new ShopifyOrderDTO();
//                        BeanUtils.copyProperties(tDTO, shopifyOrderDTO);
//                        shopifyOrderDTO.setWarehouseId(dto.getWarehouseId());
//                        shopifyOrderDTO.setWarehouseCode(dto.getWarehouseCode());
//                        shopifyOrderDTO.setWarehouseName(dto.getWarehouseName());
//                        List<ShopifyOrderItemDTO> iList = new ArrayList<>();
//                        iList.add(itemDto);
//                        shopifyOrderDTO.setShopifyOrderItemDTOList(iList);
//                        dtoMap.put(dto.getWarehouseId(),shopifyOrderDTO);
//                    }
//                }
//                if(!CollectionUtils.isEmpty(dtoMap)){
//                    Set<String> shopifyOrderTempList = dtoMap.keySet();
//                    if(dtoMap.size() > 1){
//                        int orderSerquence=1;
//                        for (String strWarehouseId:shopifyOrderTempList) {
//                            ShopifyOrderDTO shopifyOrderDTO = dtoMap.get(strWarehouseId);
//                            shopifyOrderDTO.setShopifyId(shopifyOrderDTO.getShopifyId()+"-"+orderSerquence);
//                            orderSerquence++;
//                            shopifyOrderList.add(shopifyOrderDTO);
//                        }
//                    } else {
//                        for (String strWarehouseId:shopifyOrderTempList) {
//                            ShopifyOrderDTO shopifyOrderDTO = dtoMap.get(strWarehouseId);
//                            shopifyOrderDTO.setShopifyId(shopifyOrderDTO.getShopifyId());
//                            shopifyOrderList.add(shopifyOrderDTO);
//                        }
//                    }
//                }
//            } else {
//                String sellerSku = itemList.get(0).getSku();
//                if(!skuMap.containsKey(sellerSku)){
//                    resultMap.put("error","shopify订单sku："+sellerSku+"未做sku映射");
//                    return resultMap;
//                }
//                BaseSkuMatchDTO skuMatch = skuMap.get(sellerSku);
//                if(skuMatch != null){
//                    // 查sku在哪个仓库有货
//                    WarehouseRepertoryDTO warehouseRepertoryDTO = new WarehouseRepertoryDTO();
//                    warehouseRepertoryDTO.setTotalPackages(itemList.get(0).getQuantity());
//                    warehouseRepertoryDTO.setSku(skuMatch.getSkuCode());
//                    warehouseRepertoryDTO.setZipCode(tDTO.getBillingAddressZip());
//                    WarehouseRepertoryDTO dto = null;
//                    try {
//                        List<WarehouseRepertoryDTO> warehouseRepertoryDTOS = repertorySkuFeignService.selectWarehouseRepertoryList(warehouseRepertoryDTO);
//                        dto = warehouseRepertoryDTOS.stream().filter(repertoryDto -> repertoryDto.getOptimalFlag() == true).findFirst().get();
//                    } catch (Exception e) {
//                        log.info("调用wms库存服务失败",e);
//                    }
//                    if(dto == null){
//                        log.info("shopify订单sku："+itemList.get(0).getSku()+" 系统对应SKU:"+skuMatch.getSkuCode()+"无库存");
//                        resultMap.put("error","shopify订单sku："+itemList.get(0).getSku()+"无库存");
//                        return resultMap;
//                    }
//                    itemList.get(0).setSku(skuMatch.getSkuCode());
//                    itemList.get(0).setOldSku(sellerSku);
//                    tDTO.setWarehouseId(dto.getWarehouseId());
//                    tDTO.setWarehouseCode(dto.getWarehouseCode());
//                    tDTO.setWarehouseName(dto.getWarehouseName());
//                    tDTO.setShopifyId(tDTO.getShopifyId());
//                    shopifyOrderList.add(tDTO);
//                }
//            }
//        }
//        resultMap.put("success",shopifyOrderList);
//        return resultMap;
//    }
//
//    public Map<String, BaseSkuMatchDTO> getSkuMapByPlatformSku(String customerId,List<String> listSku) {
//        Map<String, BaseSkuMatchDTO> skuMap = new HashMap<>();
//        if(CollectionUtils.isEmpty(listSku) ){
//            return  skuMap;
//        }
//        for (String sellSku:listSku) {
//            BaseSkuMatchDTO baseSkuMatch=baseSkuMatchFeignService.queryBy(customerId, sellSku,"Shopify","sku");
//            if(baseSkuMatch != null){
//                skuMap.put(sellSku,baseSkuMatch);
//            }
//        }
//        return skuMap;
//    }

    @Override
    public Map<String, Object> getCountByStatus(ShopifyOrderDTO dto) {
        QueryWrapper<ShopifyOrder> queryWrapper = Wrappers.query();
        queryWrapper.orderByDesc("create_time");
        if(!StringUtils.isEmpty(dto.getOrderStatus())){
            queryWrapper.eq(ShopifyOrderFieldEnum.ORDER_STATUS.getDataFieldName(),dto.getOrderStatus());
        }
        if(dto.getCustomerId() != null){
            queryWrapper.eq(ShopifyOrderFieldEnum.CUSTOMER_ID.getDataFieldName(), dto.getCustomerId());
        }
        QueryWrapperUtil.filterDate(queryWrapper, ShopifyOrderFieldEnum.CREATED_AT.getDataFieldName(), dto.getCreateDates());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, ShopifyOrderFieldEnum.ORDER_NUMBER.getDataFieldName(), dto.getOrderNumber());
        List<ShopifyOrder> resultPage = this.baseMapper.selectList(queryWrapper);
        Map<String, Object> collect = resultPage.stream().collect(Collectors.groupingBy(ShopifyOrder::getOrderStatus))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().size()));
        return collect;
    }

    /**
     * 获取locationId
     * @param shopName
     * @param accessToken
     * @return
     */
    @Override
    public JSONArray getShopLocations(String shopName,String accessToken){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Shopify-Access-Token", accessToken);
        HttpResponseBody responseBody = HttpClientHelper.httpGet(ShopifyConfig.requestUrl(ShopifyConfig.getLocationUrl,shopName), "", headerMap);
        if(responseBody == null ){
            log.info("【Shopify】店铺{}获取location信息失败！", shopName);
            return null;
        }
        String body = responseBody.getBody();
        log.info("【Shopify】店铺{}获取location信息: {}", shopName, body);
        if (com.szmsd.common.core.utils.StringUtils.isNotBlank(body)) {
            JSONObject jsonObject = JSONObject.parseObject(body);
            JSONArray locationsArr = jsonObject.getJSONArray("locations");
            if (CollectionUtils.isNotEmpty(locationsArr)) {
                return locationsArr;
            }
        }
        log.info("【Shopify】店铺{} 未获取到location信息！", shopName);
        return null;
    }
}