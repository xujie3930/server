package com.szmsd.delivery.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.*;
import com.szmsd.delivery.dto.DelCk1OutboundDto;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.event.DelCk1RequestLogEvent;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.service.wrapper.ApplicationContainer;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Address;
import com.szmsd.http.dto.CountryInfo;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.http.util.DomainInterceptorUtil;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.pack.api.feign.PackageDeliveryConditionsFeignService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
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

    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundRetryLabelService delOutboundRetryLabelService;
    @Autowired
    private IDelTrackService delTrackService;
    @SuppressWarnings({"all"})
    @Autowired
    private PackageDeliveryConditionsFeignService packageDeliveryConditionsFeignService;

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

        //同步返回trackingNo
        List<Integer> syncTrackingNoStateList = list.stream().map(DelOutboundDto::getSyncTrackingNoState).distinct().collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(ids)) {

            int syncTrackingNoState = syncTrackingNoStateList.get(0);

            List<DelOutboundBringVerifyVO> bringVerifyVOList = new ArrayList<>();

            if(syncTrackingNoState != 1) {

                // 批量提审出库单
                DelOutboundBringVerifyDto bringVerifyDto = new DelOutboundBringVerifyDto();
                bringVerifyDto.setIds(ids);
                stopWatch.start();
                bringVerifyVOList = this.delOutboundBringVerifyService.bringVerify(bringVerifyDto);
                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]this.delOutboundBringVerifyService.bringVerify(bringVerifyDto)耗时{}" + responses.get(0).getOrderNo(), stopWatch.getLastTaskTimeMillis());
            }
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

            if(CollectionUtils.isNotEmpty(syncTrackingNoStateList)){

                if(syncTrackingNoState == 1){

                    try {

                        for(DelOutbound delOutbound : delOutboundList) {
                            DelOutboundWrapperContext delOutboundWrapperContext = delOutboundBringVerifyService.initContext(delOutbound);
                            stopWatch.start();
                            ApplicationContainer applicationContainer = new ApplicationContainer(delOutboundWrapperContext, BringVerifyEnum.BEGIN, BringVerifyEnum.END, BringVerifyEnum.BEGIN);
                            applicationContainer.action();

                            DelOutbound upd = new DelOutbound();
                            upd.setId(delOutbound.getId());
                            upd.setState(DelOutboundStateEnum.DELIVERED.getCode());
                            delOutboundService.updateById(upd);

                            // 提审成功，增加CK1数据
                            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())
                                    || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
                                DelCk1OutboundDto ck1OutboundDto = new DelCk1OutboundDto();
                                ck1OutboundDto.setWarehouseId(Ck1DomainPluginUtil.wrapper(delOutbound.getWarehouseCode()));
                                DelCk1OutboundDto.PackageDTO packageDTO = new DelCk1OutboundDto.PackageDTO();
                                packageDTO.setPackageId(delOutbound.getOrderNo());
                                // packageDTO.setServiceCode(delOutbound.getShipmentRule());
                                packageDTO.setServiceCode("DMTCK");
                                DelCk1OutboundDto.PackageDTO.ShipToAddressDTO shipToAddressDTO = new DelCk1OutboundDto.PackageDTO.ShipToAddressDTO();
                                DelOutboundAddress outboundAddress = delOutboundWrapperContext.getAddress();
                                if(outboundAddress != null) {
                                    shipToAddressDTO.setCountry(outboundAddress.getCountry());
                                    shipToAddressDTO.setProvince(outboundAddress.getStateOrProvince());
                                    shipToAddressDTO.setCity(outboundAddress.getCity());
                                    shipToAddressDTO.setStreet1(outboundAddress.getStreet1());
                                    shipToAddressDTO.setStreet2(outboundAddress.getStreet2());
                                    shipToAddressDTO.setPostcode(outboundAddress.getPostCode());
                                    shipToAddressDTO.setContact(outboundAddress.getConsignee());
                                    shipToAddressDTO.setPhone(outboundAddress.getPhoneNo());
                                    shipToAddressDTO.setEmail(outboundAddress.getEmail());
                                }
                                packageDTO.setShipToAddress(shipToAddressDTO);
                                List<DelCk1OutboundDto.PackageDTO.SkusDTO> skusDTOList = new ArrayList<>();
                                List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
                                List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
                                Map<String, BaseProduct> productMap = null;
                                if (CollectionUtils.isNotEmpty(productList)) {
                                    productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
                                }
                                for (DelOutboundDetail outboundDetail : detailList) {
                                    DelCk1OutboundDto.PackageDTO.SkusDTO skusDTO = new DelCk1OutboundDto.PackageDTO.SkusDTO();
                                    String sku = outboundDetail.getSku();
                                    // String inventoryCode = CkConfig.genCk1SkuInventoryCode(delOutbound.getSellerCode(), delOutbound.getWarehouseCode(), sku);
                                    // skusDTO.setSku(inventoryCode);
                                    skusDTO.setSku(sku);
                                    skusDTO.setQuantity(outboundDetail.getQty());
                                    if (null != productMap && null != productMap.get(sku)) {
                                        BaseProduct baseProduct = productMap.get(sku);
                                        skusDTO.setProductName(baseProduct.getProductName());
                                        skusDTO.setPrice(baseProduct.getDeclaredValue());
                                        skusDTO.setPlatformItemId("" + baseProduct.getId());
                                    } else {
                                        skusDTO.setProductName(outboundDetail.getProductName());
                                        skusDTO.setPrice(outboundDetail.getDeclaredValue());
                                    }
                                    skusDTO.setHsCode(outboundDetail.getHsCode());
                                    skusDTOList.add(skusDTO);
                                }
                                packageDTO.setSkus(skusDTOList);
                                ck1OutboundDto.setPackage(packageDTO);
                                DelCk1RequestLog ck1RequestLog = new DelCk1RequestLog();
                                Map<String, String> headers = new HashMap<>();
                                headers.put(DomainInterceptorUtil.KEYWORD, delOutbound.getSellerCode());
                                ck1RequestLog.setRemark(JSON.toJSONString(headers));
                                ck1RequestLog.setOrderNo(delOutbound.getOrderNo());
                                ck1RequestLog.setRequestBody(JSON.toJSONString(ck1OutboundDto));
                                ck1RequestLog.setType(DelCk1RequestLogConstant.Type.create.name());
                                EventUtil.publishEvent(new DelCk1RequestLogEvent(ck1RequestLog));
                            }
                            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                                // 增加出库单已取消记录，异步处理，定时任务
                                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
                            }

                            String productCode = delOutbound.getShipmentRule();
                            String prcProductCode = delOutboundWrapperContext.getPrcProductCode();
                            if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(prcProductCode)) {
                                productCode = prcProductCode;
                            }

                            boolean bool = false;
                            // 查询发货条件
                            if (StringUtils.isNotEmpty(delOutbound.getWarehouseCode())
                                    && StringUtils.isNotEmpty(productCode)) {
                                PackageDeliveryConditions packageDeliveryConditions = new PackageDeliveryConditions();
                                packageDeliveryConditions.setWarehouseCode(delOutbound.getWarehouseCode());
                                packageDeliveryConditions.setProductCode(productCode);
                                R<PackageDeliveryConditions> packageDeliveryConditionsR = this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions);
                                logger.info("订单号{}查询发货条件接口成功{}，{}, 返回json:{}", delOutbound.getOrderNo(), delOutbound.getWarehouseCode(), delOutbound.getShipmentService(),
                                        JSONUtil.toJsonStr(packageDeliveryConditionsR));
                                PackageDeliveryConditions packageDeliveryConditionsRData = null;
                                if (null != packageDeliveryConditionsR && Constants.SUCCESS == packageDeliveryConditionsR.getCode()) {
                                    packageDeliveryConditionsRData = packageDeliveryConditionsR.getData();
                                }
                                if (null != packageDeliveryConditionsRData && "AfterMeasured".equals(packageDeliveryConditionsRData.getCommandNodeCode())) {
                                    //出库测量后接收发货指令 就不调用标签接口
                                    bool = true;
                                }
                            }else{
                                logger.info("订单号{}查询发货条件失败{}，{}", delOutbound.getOrderNo(), delOutbound.getWarehouseCode(), delOutbound.getShipmentService());
                            }
                            if(!bool){
                                // 提交一个获取标签的任务
                                delOutboundRetryLabelService.saveAndPushLabel(delOutbound.getOrderNo(), "pushLabel", "bringVerify");
                            }

                            delTrackService.addData(new DelTrack()
                                    .setOrderNo(delOutbound.getOrderNo())
                                    .setTrackingNo(delOutbound.getTrackingNo())
                                    .setTrackingStatus("Todo")
                                    .setDescription("DMF, Parcel Infomation Received"));
                            logger.info("(3)提审异步操作成功，出库单号：{}", delOutbound.getOrderNo());

                            stopWatch.stop();
                            logger.info(">>>>>[创建出库单{}]this.BringVerifyEnum(bringVerifyDto)耗时{}" + delOutbound.getOrderNo(), stopWatch.getLastTaskTimeMillis());
                        }

                        List<DelOutbound> delOutbounds = this.delOutboundService.listByIds(ids);

                        Map<String,DelOutbound> delOutboundMap1 = delOutbounds.stream().collect(Collectors.toMap(DelOutbound::getOrderNo,v->v));

                        for(DelOutboundAddResponse response : responses){

                            String orderNo = response.getOrderNo();
                            DelOutbound delOutbound = delOutboundMap1.get(orderNo);

                            if(delOutbound!= null){
                                response.setTrackingNo(delOutbound.getTrackingNo());
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
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
