package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Address;
import com.szmsd.http.dto.CountryInfo;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.vo.PricedProduct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:32
 */
@Service
public class DelOutboundDocServiceImpl implements IDelOutboundDocService {
    private Logger logger = LoggerFactory.getLogger(DelOutboundDocServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Resource
    private ChargeFeignService chargeFeignService;
    private List<DelOutboundAddResponse> addResponseList(List<DelOutboundDto> dtoList) {
        List<DelOutboundAddResponse> result = new ArrayList<>();
        int index = 1;
        List<String> refNoList = new ArrayList<String>();
        for (DelOutboundDto dto : dtoList) {
            DelOutboundAddResponse delOutbound = null;
            if (StringUtils.isNotEmpty(dto.getRefNo()) && refNoList.contains(dto.getRefNo())) {
                delOutbound = new DelOutboundAddResponse();
                // 返回异常错误信息
                delOutbound.setStatus(false);
                delOutbound.setMessage("本次操作数据中Refno 必须唯一值" + dto.getRefNo());
            } else {
                refNoList.add(dto.getRefNo());
                try {
                    delOutbound = this.delOutboundService.insertDelOutbound(dto);
                } catch (Exception e) {
                    e.printStackTrace();
                    delOutbound = new DelOutboundAddResponse();
                    // 返回异常错误信息
                    delOutbound.setStatus(false);
                    delOutbound.setMessage(e.getMessage());
                }
            }
            delOutbound.setIndex(index);
            result.add(delOutbound);
            index++;
        }
        return result;
    }

    @Override
    public List<DelOutboundAddResponse> add(List<DelOutboundDto> list) {
        StopWatch stopWatch = new StopWatch();

        // 批量创建出库单
        // List<DelOutboundAddResponse> responses = this.delOutboundService.insertDelOutbounds(list);
        // 处理异常信息，出现异常让事务回滚
        stopWatch.start();

        List<DelOutboundAddResponse> responses = this.addResponseList(list);
        stopWatch.stop();
        logger.info(">>>>>[创建出库单{}]完成总耗时{}"+responses.get(0).getOrderNo(), stopWatch.getLastTaskTimeMillis());

        // 获取出库单ID
        List<Long> ids = responses.stream().map(DelOutboundAddResponse::getId).filter(Objects::nonNull).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ids)) {
            // 批量提审出库单
            DelOutboundBringVerifyDto bringVerifyDto = new DelOutboundBringVerifyDto();
            bringVerifyDto.setIds(ids);
            stopWatch.start();
            List<DelOutboundBringVerifyVO> bringVerifyVOList = this.delOutboundBringVerifyService.bringVerify(bringVerifyDto);
            stopWatch.stop();
            logger.info(">>>>>[创建出库单{}]this.delOutboundBringVerifyService.bringVerify(bringVerifyDto)耗时{}"+responses.get(0).getOrderNo(), stopWatch.getLastTaskTimeMillis());
            Map<String, DelOutboundBringVerifyVO> bringVerifyVOMap = new HashMap<>();
            for (DelOutboundBringVerifyVO bringVerifyVO : bringVerifyVOList) {
                bringVerifyVOMap.put(bringVerifyVO.getOrderNo(), bringVerifyVO);
            }

            // 查询出库单信息
            List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
            Map<Long, DelOutbound> delOutboundMap = delOutboundList.stream().collect(Collectors.toMap(DelOutbound::getId, v -> v, (a, b) -> a));

            for (DelOutboundAddResponse response : responses) {
                if (!response.getStatus()) {
                    continue;
                }
                // 提审结果
                DelOutboundBringVerifyVO bringVerifyVO = bringVerifyVOMap.get(response.getOrderNo());
                if (null != bringVerifyVO) {
                    response.setStatus(bringVerifyVO.getSuccess());
                    response.setMessage(bringVerifyVO.getMessage());
                }
                // 挂号
                DelOutbound delOutbound = delOutboundMap.get(response.getId());
                if (null != delOutbound) {
                    response.setTrackingNo(delOutbound.getTrackingNo());
                }
            }

            //同步返回trackingNo
            List<Integer> syncTrackingNoStateList = list.stream().map(DelOutboundDto::getSyncTrackingNoState).distinct().collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(syncTrackingNoStateList)){
                int syncTrackingNoState = syncTrackingNoStateList.get(0);

                if(syncTrackingNoState == 1){

                    for(DelOutbound delOutbound : delOutboundList) {
                        DelOutboundWrapperContext delOutboundWrapperContext = delOutboundBringVerifyService.initContext(delOutbound);
                        stopWatch.start();
                        ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
                        stopWatch.stop();
                        logger.info(">>>>>[同步获取创建出库单{}]创建承运商 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
                        if(shipmentOrderResult != null){
                            delOutbound.setTrackingNo(shipmentOrderResult.getMainTrackingNumber());
                            delOutbound.setShipmentOrderNumber(shipmentOrderResult.getOrderNumber());
                            delOutbound.setShipmentOrderLabelUrl(shipmentOrderResult.getOrderLabelUrl());
                            delOutbound.setReferenceNumber(shipmentOrderResult.getReferenceNumber());
                        }
                    }

                    Map<String,DelOutbound> delOutboundMap1 = delOutboundList.stream().collect(Collectors.toMap(DelOutbound::getOrderNo,v->v));

                    for(DelOutboundAddResponse response : responses){

                        String orderNo = response.getOrderNo();
                        DelOutbound delOutbound = delOutboundMap1.get(orderNo);

                        if(delOutbound!= null){
                            response.setTrackingNo(delOutbound.getTrackingNo());
                        }
                    }
                }
            }
        }

        return responses;
    }

    @Override
    public List<PricedProduct> inService(DelOutboundOtherInServiceDto dto, boolean isShow) {
        // 查询仓库信息
        String warehouseCode = dto.getWarehouseCode();
        BasWarehouse warehouse = null;
        if (StringUtils.isNotEmpty(warehouseCode)) {
            Long s = System.currentTimeMillis();
            warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
            Long e = System.currentTimeMillis();
            logger.info("inService  basWarehouseClientService.queryByWarehouseCode,{}",e-s);
            if (null == warehouse) {
                throw new CommonException("400", "仓库信息不存在");
            }
        }
        // 查询国家信息
        // String countryCode = dto.getCountryCode();
        // 从仓库上获取国家信息
        String countryCode = null;
        if (null != warehouse) {
            countryCode = warehouse.getCountryCode();
        }
        BasRegionSelectListVO country = null;
        if (StringUtils.isNotEmpty(countryCode)) {
            Long s = System.currentTimeMillis();
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(countryCode);
            Long e = System.currentTimeMillis();
            logger.info("inService  basRegionFeignService.queryByCountryCode,{}",e-s);
            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", "国家信息不存在");
            }
        }
        // 传入参数：仓库，SKU
        PricedProductInServiceCriteria criteria = new PricedProductInServiceCriteria();
        criteria.setClientCode(dto.getClientCode());
        // 目的地国家
        criteria.setCountryName(dto.getCountryCode());
        CountryInfo countryInfo = null;
        if (null != country) {
            // 仓库上的国家
            countryInfo = new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName());
        }
        Address fromAddress = null;
        if (null != warehouse) {
            fromAddress = new Address(warehouse.getStreet1(),
                    warehouse.getStreet2(),
                    null,
                    warehouse.getPostcode(),
                    warehouse.getCity(),
                    warehouse.getProvince(),
                    countryInfo
            );
        }
        criteria.setFromAddress(fromAddress);
        if (CollectionUtils.isNotEmpty(dto.getSkus())) {
            // fix:SKU不跟仓库关联，SKU跟卖家编码关联。
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSellerCode(dto.getClientCode());
            conditionQueryDto.setSkus(dto.getSkus());

            Long s = System.currentTimeMillis();

            criteria.setShipmentTypes(this.baseProductClientService.listProductAttribute(conditionQueryDto));

            Long e = System.currentTimeMillis();
            logger.info("inService  baseProductClientService.listProductAttribute,{}",e-s);
        }
        if (CollectionUtils.isNotEmpty(dto.getProductAttributes())) {
            criteria.setShipmentTypes(dto.getProductAttributes());
        }

        Long ss = System.currentTimeMillis();
        List<PricedProduct> list = this._inService(criteria);
        Long ee = System.currentTimeMillis();
        logger.info("inService  _inService,{}",ss-ee);



        if(list.size() > 0 && !isShow){
            //是否可以查询
            List<String> codes = list.stream().
                    map(sfcMessage -> sfcMessage.getCode()).collect(Collectors.toList());

            Long s = System.currentTimeMillis();

            R<List<BasProductService>> r = chargeFeignService.selectBasProductService(codes);
            Long e = System.currentTimeMillis();
            logger.info("inService  chargeFeignService.selectBasProductService,{}",e-s);
            if(r.getCode() == 200 && r.getData()!= null && r.getData().size() > 0){

                Map<String, Boolean> map = r.getData().stream()
                        .collect(Collectors.toMap(BasProductService::getProductCode, BasProductService::getIsShow, (p1, p2) -> p1));
                List<PricedProduct> newList = new ArrayList<>();
                for(PricedProduct product: list){
                    Boolean isShowVal = map.get(product.getCode());
                    if(isShowVal != null && isShowVal == false){
                        continue;
                    }
                    newList.add(product);
                }
                return newList;
            }

        }

        return list;
    }

    @Override
    public List<PricedProduct> inService2(DelOutboundOtherInServiceDto dto) {
        // 查询仓库信息
        String warehouseCode = dto.getWarehouseCode();
        BasWarehouse warehouse = null;
        if (StringUtils.isNotEmpty(warehouseCode)) {
            warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
            if (null == warehouse) {
                throw new CommonException("400", "仓库信息不存在");
            }
        }
        // 传入参数：仓库，SKU
        PricedProductInServiceCriteria criteria = new PricedProductInServiceCriteria();
        criteria.setClientCode(dto.getClientCode());
        CountryInfo countryInfo = null;
        if (null != warehouse.getCountryCode()) {
            criteria.setCountryName(warehouse.getCountryName());
            countryInfo = new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName());
        }
        Address fromAddress = null;
        if (null != warehouse) {
            fromAddress = new Address(warehouse.getStreet1(),
                    warehouse.getStreet2(),
                    null,
                    warehouse.getPostcode(),
                    warehouse.getCity(),
                    warehouse.getProvince(),
                    countryInfo
            );
        }
        criteria.setFromAddress(fromAddress);
        return this._inService(criteria);
    }

    private List<PricedProduct> _inService(PricedProductInServiceCriteria criteria) {
        return this.htpPricedProductClientService.inService(criteria);
    }

    @Override
    public boolean inServiceValid(DelOutboundOtherInServiceDto dto, String shipmentRule) {
        if (StringUtils.isEmpty(shipmentRule)) {
            return false;
        }
        List<PricedProduct> productList = this.inService(dto, true);
        if (CollectionUtils.isEmpty(productList)) {
            return false;
        }
        for (PricedProduct product : productList) {
            if (null != product && shipmentRule.equals(product.getCode())) {
                return true;
            }
        }
        return false;
    }
}
