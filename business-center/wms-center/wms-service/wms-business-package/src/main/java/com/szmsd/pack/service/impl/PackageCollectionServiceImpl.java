package com.szmsd.pack.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpPickupPackageService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.Package;
import com.szmsd.http.dto.*;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.pack.constant.PackageCollectionConstants;
import com.szmsd.pack.constant.PackageCollectionOperationRecordConstants;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.domain.PackageCollectionDetail;
import com.szmsd.pack.domain.PackageCollectionOperationRecord;
import com.szmsd.pack.dto.PackageCollectionQueryDto;
import com.szmsd.pack.events.PackageCollectionCompletedEvent;
import com.szmsd.pack.events.PackageCollectionContextEvent;
import com.szmsd.pack.mapper.PackageCollectionMapper;
import com.szmsd.pack.service.IPackageCollectionDetailService;
import com.szmsd.pack.service.IPackageCollectionOperationRecordService;
import com.szmsd.pack.service.IPackageCollectionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * package - ???????????? - ?????? ???????????????
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Service
public class PackageCollectionServiceImpl extends ServiceImpl<PackageCollectionMapper, PackageCollection> implements IPackageCollectionService {
    private final Logger logger = LoggerFactory.getLogger(PackageCollectionServiceImpl.class);

    @Autowired
    private IPackageCollectionDetailService packageCollectionDetailService;
    @Autowired
    private SerialNumberClientService serialNumberClientService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private RechargesFeignService rechargesFeignService;
    @SuppressWarnings({"all"})
    @Autowired
    private OperationFeignService operationFeignService;
    @Autowired
    private IHtpPickupPackageService httpPickupPackageService;
    @Autowired
    private IPackageCollectionOperationRecordService packageCollectionOperationRecordService;
    @Autowired
    private IHtpRmiClientService htpRmiClientService;

    /**
     * ??????package - ???????????? - ????????????
     *
     * @param id package - ???????????? - ????????????ID
     * @return package - ???????????? - ????????????
     */
    @Override
    public PackageCollection selectPackageCollectionById(String id) {
        PackageCollection packageCollection = baseMapper.selectById(id);
        if (null != packageCollection) {
            packageCollection.setDetailList(this.packageCollectionDetailService.listByCollectionId(packageCollection.getId()));
        }
        return packageCollection;
    }

    @Override
    public PackageCollection selectPackageCollectionByNo(String no, String hasDetail) {
        LambdaQueryWrapper<PackageCollection> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PackageCollection::getCollectionNo, no);
        PackageCollection packageCollection = super.getOne(queryWrapper);
        if (StringUtils.isNotEmpty(hasDetail)) {
            hasDetail = "Y";
        }
        if (null != packageCollection && "Y".equals(hasDetail)) {
            packageCollection.setDetailList(this.packageCollectionDetailService.listByCollectionId(packageCollection.getId()));
        }
        return packageCollection;
    }

    /**
     * ??????package - ???????????? - ??????????????????
     *
     * @param packageCollection package - ???????????? - ????????????
     * @return package - ???????????? - ????????????
     */
    @Override
    public List<PackageCollection> selectPackageCollectionList(PackageCollection packageCollection) {
        QueryWrapper<PackageCollection> where = new QueryWrapper<>();
        return baseMapper.selectList(where);
    }

    /**
     * ??????package - ???????????? - ????????????
     *
     * @param packageCollection package - ???????????? - ????????????
     * @return ??????
     */
    @Transactional
    @Override
    public int insertPackageCollection(PackageCollection packageCollection) {
        this.logger.info(">>>insertPackageCollection: ????????????");
        TimeInterval timer = DateUtil.timer();
        PackageCollectionContext packageCollectionContextCancel = new PackageCollectionContext(packageCollection, PackageCollectionContext.Type.CANCEL);
        try {
            List<PackageCollectionDetail> detailList = packageCollection.getDetailList();
            packageCollection.setTotalQty(this.countTotalQty(detailList));
            // ????????????
            String collectionNo = this.serialNumberClientService.generateNumber("COLLECTION_NO");
            packageCollection.setCollectionNo(collectionNo);
            // ????????????
            if (PackageCollectionConstants.COLLECTION_PLAN_YES.equals(packageCollection.getCollectionPlan())) {
                packageCollection.setStatus(PackageCollectionConstants.Status.PLANNED.name());
            } else {
                packageCollection.setStatus(PackageCollectionConstants.Status.NO_PLAN.name());
            }
            this.logger.info(">>>insertPackageCollection: ???????????????????????????{}", timer.intervalRestart());
            // PRC???????????????????????????
            ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> responseObject = this.pricing(packageCollection);
            this.logger.info(">>>insertPackageCollection: PRC??????????????????{}", timer.intervalRestart());
            if (null == responseObject) {
                // ??????????????????
                throw new CommonException("400", "?????????????????????????????????????????????");
            } else {
                // ???????????????
                if (responseObject.isSuccess()) {
                    // ???????????????
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    // ????????????
                    packageCollection.setShipmentService(data.getLogisticsRouteId());
                    // ?????????code
                    packageCollection.setLogisticsProviderCode(data.getLogisticsProviderCode());
                    // ????????????
                    Packing packing = packageInfo.getPacking();
                    BigDecimal length = packing.getLength();
                    if (null == length) {
                        length = BigDecimal.ZERO;
                    }
                    packageCollection.setLength(length.doubleValue());
                    BigDecimal width = packing.getWidth();
                    if (null == width) {
                        width = BigDecimal.ZERO;
                    }
                    packageCollection.setWidth(width.doubleValue());
                    BigDecimal height = packing.getHeight();
                    if (null == height) {
                        height = BigDecimal.ZERO;
                    }
                    packageCollection.setHeight(height.doubleValue());
                    // ???????????????
                    Weight calcWeight = packageInfo.getCalcWeight();
                    packageCollection.setCalcWeight(calcWeight.getValue());
                    packageCollection.setCalcWeightUnit(calcWeight.getUnit());

                    if (data.getIsPickupPackageService() != null)
                        packageCollection.setIsPickupPackageService(data.getIsPickupPackageService());
                    if (data.getPickupPackageServiceName() != null)
                        packageCollection.setPickupPackageServiceName(data.getPickupPackageServiceName());

                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    // ????????????
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    String totalCurrencyCode = charges.get(0).getMoney().getCurrencyCode();
                    for (ChargeItem charge : charges) {
                        BigDecimal amount = BigDecimal.ZERO;
                        Money money = charge.getMoney();
                        if (null != money) {
                            Double amount1 = money.getAmount();
                            if (null != amount1) {
                                amount = BigDecimal.valueOf(amount1);
                            }
                        }
                        totalAmount = totalAmount.add(amount);
                    }
                    // ?????????
                    packageCollection.setAmount(totalAmount);
                    packageCollection.setCurrencyCode(totalCurrencyCode);
                } else {
                    // ????????????
                    String exceptionMessage = ProblemDetails.getErrorMessageOrNull(responseObject.getError());
                    if (StringUtils.isNotEmpty(exceptionMessage)) {
                        exceptionMessage = "????????????????????????";
                    }
                    throw new CommonException("400", exceptionMessage);
                }
            }
            CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
            cusFreezeBalanceDTO.setAmount(packageCollection.getAmount());
            cusFreezeBalanceDTO.setCurrencyCode(packageCollection.getCurrencyCode());
            cusFreezeBalanceDTO.setCusCode(packageCollection.getSellerCode());
            cusFreezeBalanceDTO.setNo(packageCollection.getCollectionNo());
            cusFreezeBalanceDTO.setOrderType("Freight");
            // ????????????????????????
            R<?> freezeBalanceR = this.rechargesFeignService.freezeBalance(cusFreezeBalanceDTO);
            this.logger.info(">>>insertPackageCollection: ????????????????????????{}", timer.intervalRestart());
            if (null != freezeBalanceR) {
                if (Constants.SUCCESS != freezeBalanceR.getCode()) {
                    // ????????????
                    String msg = freezeBalanceR.getMsg();
                    throw new CommonException("400", msg);
                }
            } else {
                // ????????????
                throw new CommonException("400", "?????????????????????????????????????????????");
            }
            // ????????????????????????
            // this.packageCollectionOperationRecordService.add(packageCollection.getCollectionNo(), PackageCollectionOperationRecordConstants.Type.FREIGHT.name());
            packageCollectionContextCancel.addUndoType(PackageCollectionOperationRecordConstants.Type.FREIGHT);
            // ????????????????????????????????????
            CreateShipmentOrderCommand createShipmentOrderCommand = new CreateShipmentOrderCommand();
            createShipmentOrderCommand.setWarehouseCode(packageCollection.getWarehouseCode());
            // ??????uuid
            createShipmentOrderCommand.setReferenceNumber(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
            createShipmentOrderCommand.setOrderNumber(packageCollection.getCollectionNo());
            createShipmentOrderCommand.setClientNumber(packageCollection.getSellerCode());
            createShipmentOrderCommand.setReceiverAddress(new AddressCommand(packageCollection.getReceiverName(),
                    packageCollection.getReceiverPhone(),
                    "",
                    packageCollection.getReceiverAddress(),
                    "",
                    "",
                    packageCollection.getReceiverCity(),
                    packageCollection.getReceiverProvince(),
                    packageCollection.getReceiverPostCode(),
                    packageCollection.getReceiverCountryCode(),
                    packageCollection.getTaxNumber(),
                    packageCollection.getIdNumber(),
                    null

            ));
            createShipmentOrderCommand.setReturnAddress(new AddressCommand(packageCollection.getCollectionName(),
                    packageCollection.getCollectionPhone(),
                    "",
                    packageCollection.getReceiverAddress(),
                    "",
                    "",
                    packageCollection.getCollectionCity(),
                    packageCollection.getCollectionProvince(),
                    packageCollection.getCollectionPostCode(),
                    packageCollection.getCollectionCountryCode(),
                    packageCollection.getTaxNumber(),
                    packageCollection.getIdNumber(),
                    null
                    )
            );
            // ????????????
            List<Package> packages = new ArrayList<>();
            List<PackageItem> packageItems = new ArrayList<>();
            BigDecimal weightInGram = BigDecimal.ZERO;
            for (PackageCollectionDetail detail : detailList) {
                String sku = detail.getSku();
                BigDecimal weight = detail.getWeight();
                if (null == weight) {
                    weight = BigDecimal.ZERO;
                }
                weightInGram = weightInGram.add(weight);
                BigDecimal length = detail.getLength();
                if (null == length) {
                    length = BigDecimal.ZERO;
                }
                BigDecimal width = detail.getWidth();
                if (null == width) {
                    width = BigDecimal.ZERO;
                }
                BigDecimal height = detail.getHeight();
                if (null == height) {
                    height = BigDecimal.ZERO;
                }
                BigDecimal declaredValue = detail.getDeclaredValue();
                if (null == declaredValue) {
                    declaredValue = BigDecimal.ZERO;
                }
                packageItems.add(new PackageItem(detail.getSku(), detail.getSkuName(), declaredValue.doubleValue(), weight.intValue(),
                        new Size(length.doubleValue(), width.doubleValue(), height.doubleValue()),
                        detail.getQty(), "", String.valueOf(detail.getId()), sku));
            }
            if (weightInGram.intValue() <= 0) {
                throw new CommonException("400", "???????????????0????????????0????????????????????????????????????");
            }
            String packageNumber = packageCollection.getCollectionNo();
            packages.add(new Package(packageNumber, String.valueOf(packageCollection.getId()),
                    new Size(packageCollection.getLength(), packageCollection.getWidth(), packageCollection.getHeight()),
                    weightInGram.intValue(), packageItems));
            createShipmentOrderCommand.setPackages(packages);
            createShipmentOrderCommand.setCarrier(new Carrier(packageCollection.getShipmentService()));
            ResponseObject<ShipmentOrderResult, ProblemDetails> responseObjectWrapper = this.htpCarrierClientService.shipmentOrder(createShipmentOrderCommand);
            this.logger.info(">>>insertPackageCollection: ???????????????????????????????????????{}", timer.intervalRestart());
            if (null == responseObjectWrapper) {
                throw new CommonException("400", "??????????????????????????????????????????????????????????????????");
            }
            if (responseObjectWrapper.isSuccess()) {
                // ????????????
                // ?????????????????????????????????
                ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
                if (null == shipmentOrderResult) {
                    throw new CommonException("400", "???????????????????????????????????????????????????????????????????????????");
                }
                if (null == shipmentOrderResult.getSuccess() || !shipmentOrderResult.getSuccess()) {
                    // ???????????????????????????
                    ErrorDto error = shipmentOrderResult.getError();
                    StringBuilder builder = new StringBuilder();
                    if (null != error && StringUtils.isNotEmpty(error.getMessage())) {
                        if (StringUtils.isNotEmpty(error.getErrorCode())) {
                            builder.append("[")
                                    .append(error.getErrorCode())
                                    .append("]");
                        }
                        builder.append(error.getMessage());
                    } else {
                        builder.append("??????????????????????????????????????????????????????????????????????????????????????????");
                    }
                    throw new CommonException("400", builder.toString());
                }
                packageCollection.setTrackingNo(shipmentOrderResult.getMainTrackingNumber());
                packageCollection.setShipmentOrderNumber(shipmentOrderResult.getOrderNumber());
                packageCollection.setShipmentOrderLabelUrl(shipmentOrderResult.getOrderLabelUrl());
            } else {
                String exceptionMessage = ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError(), true);
                if (StringUtils.isEmpty(exceptionMessage)) {
                    exceptionMessage = "???????????????????????????????????????????????????????????????";
                }
                throw new CommonException("400", exceptionMessage);
            }
            // ????????????????????????
            // this.packageCollectionOperationRecordService.add(packageCollection.getCollectionNo(), PackageCollectionOperationRecordConstants.Type.CARRIER.name());
            packageCollectionContextCancel.addUndoType(PackageCollectionOperationRecordConstants.Type.CARRIER);
            // ???????????????
            this.logger.info(">>>insertPackageCollection: ??????-???????????????????????????{}", timer.intervalRestart());
            BigDecimal amount = BigDecimalUtil.setScale(packageCollection.getAmount(),BigDecimalUtil.PRICE_SCALE);
            packageCollection.setAmount(amount);

            BigDecimal calcWeight = BigDecimalUtil.setScale(packageCollection.getCalcWeight(),BigDecimalUtil.WEIGHT_SCALE);
            packageCollection.setCalcWeight(calcWeight);

            boolean save = super.save(packageCollection);
            this.logger.info(">>>insertPackageCollection: ??????-???????????????????????????{}", timer.intervalRestart());
            if (save) {
                // ????????????
                this.saveDetail(packageCollection.getId(), detailList);
                this.logger.info(">>>insertPackageCollection: ?????????????????????????????????{}", timer.intervalRestart());
                // ???????????????
                DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
                List<DelOutboundOperationDetailVO> detailVOList = new ArrayList<>(detailList.size());
                // ????????????????????????
                for (PackageCollectionDetail detail : detailList) {
                    String sku = detail.getSku();
                    // ???????????????
                    DelOutboundOperationDetailVO detailVO = new DelOutboundOperationDetailVO();
                    detailVO.setSku(sku);
                    Integer qty = detail.getQty();
                    if (null == qty) {
                        qty = 0;
                    }
                    detailVO.setQty(Long.valueOf(qty));
                    BigDecimal weight = detail.getWeight();
                    if (null == weight) {
                        weight = BigDecimal.ZERO;
                    }
                    detailVO.setWeight(weight.doubleValue());
                    detailVOList.add(detailVO);
                }
                delOutboundOperationVO.setCustomCode(packageCollection.getSellerCode());
                delOutboundOperationVO.setOrderNo(packageCollection.getCollectionNo());
                delOutboundOperationVO.setDetails(detailVOList);
                delOutboundOperationVO.setOrderType("PackageCollection");
                R<?> r = operationFeignService.delOutboundFreeze(delOutboundOperationVO);
                this.logger.info(">>>insertPackageCollection: ??????????????????????????????{}", timer.intervalRestart());
                if (null == r) {
                    throw new CommonException("500", "???????????????????????????????????????????????????");
                }
                if (Constants.SUCCESS != r.getCode()) {
                    String msg = r.getMsg();
                    if (StringUtils.isEmpty(msg)) {
                        msg = "??????????????????????????????";
                    }
                    throw new CommonException("500", msg);
                }
                // ????????????????????????
                // this.packageCollectionOperationRecordService.add(packageCollection.getCollectionNo(), PackageCollectionOperationRecordConstants.Type.OPERATING_FEE.name());
                packageCollectionContextCancel.addUndoType(PackageCollectionOperationRecordConstants.Type.OPERATING_FEE);
                // ??????TrackingYee
                this.createTrackingYee(packageCollection);
                this.logger.info(">>>insertPackageCollection: ??????TY????????????{}", timer.intervalRestart());
                // ???????????????????????????
                if (PackageCollectionConstants.COLLECTION_PLAN_YES.equals(packageCollection.getCollectionPlan())) {
                    // ?????????????????????
                    PackageCollectionContext packageCollectionContextCreateReceiver = new PackageCollectionContext(packageCollection, PackageCollectionContext.Type.CREATE_RECEIVER, true);
                    PackageCollectionContextEvent.publishEvent(packageCollectionContextCreateReceiver);
                    operationFeignService.delOutboundFreeze(delOutboundOperationVO);
                }
                //?????????????????????????????????
                if (StringUtils.isNotEmpty(packageCollection.getPickupPackageServiceName())) {
                    createPackageService(packageCollection);
                }
            }
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // ????????????????????????
            PackageCollectionContextEvent.publishEvent(packageCollectionContextCancel);
            if (e instanceof CommonException) {
                throw e;
            }
            throw new CommonException("500", e.getMessage());
        } finally {
            this.logger.info(">>>insertPackageCollection: ????????????");
        }
    }

    //??????????????????
    private void createPackageService(PackageCollection packageCollection) {
        CreatePickupPackageCommand createPickupPackageCommand = new CreatePickupPackageCommand().setReferenceNumber(packageCollection.getCollectionNo()).setPickupServiceName(packageCollection.getPickupPackageServiceName());

        Address4PackageService address = new Address4PackageService().setName(packageCollection.getCollectionName()).setCompanyName(packageCollection.getCollectionName()).setPhone(packageCollection.getCollectionPhone()).setEmail(null).setAddress1(packageCollection.getCollectionAddress()).setAddress2(null).setAddress3(null).setCity(packageCollection.getCollectionCity()).setProvince(packageCollection.getCollectionProvince()).setPostCode(packageCollection.getCollectionPostCode()).setCountry(packageCollection.getCollectionCountry());
        createPickupPackageCommand.setPickupAddress(address);

        PickupPieces pickupPieces = new PickupPieces();
        List<PickupPieceItemPickupPieceItem> list = new ArrayList<>();
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (PackageCollectionDetail detail : packageCollection.getDetailList()) {
            PickupPieceItemPickupPieceItem item = new PickupPieceItemPickupPieceItem();
            item.setQuantity(detail.getQty());
            item.setCustomerTag(detail.getSkuName());
            list.add(item);
            totalWeight.add(detail.getWeight());
        }
        pickupPieces.setPickupPieceItems(list);
        pickupPieces.setTotalWeight(totalWeight/*.divide(new BigDecimal("1000")*/.intValue());
        pickupPieces.setUnitOfMeasurement("KGS");
        createPickupPackageCommand.setPickupPieces(pickupPieces);

        PickupDateInfo pickupDateInfo = new PickupDateInfo();
        if (packageCollection.getCollectionDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            pickupDateInfo.setPickupDate(sdf.format(packageCollection.getCollectionDate()));
        }
        createPickupPackageCommand.setPickupDateInfo(pickupDateInfo);

        httpPickupPackageService.create(createPickupPackageCommand);
    }

    private ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> pricing(PackageCollection packageCollection) {
        // ??????????????????
        CalcShipmentFeeCommand calcShipmentFeeCommand = new CalcShipmentFeeCommand();
        // true?????????????????????false??????????????????????????????
        calcShipmentFeeCommand.setAddressValifition(true);
        // ??????????????????????????????????????????
        calcShipmentFeeCommand.setProductCode(packageCollection.getCollectionServiceCode());
        calcShipmentFeeCommand.setClientCode(packageCollection.getSellerCode());
        calcShipmentFeeCommand.setShipmentType("");
        calcShipmentFeeCommand.setIoss("");
        List<PackageInfo> packageInfos = new ArrayList<>();
        List<PackageCollectionDetail> detailList = packageCollection.getDetailList();
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (PackageCollectionDetail detail : detailList) {
                packageInfos.add(new PackageInfo(new Weight(detail.getWeight(), "g"),
                        new Packing(detail.getLength(), detail.getWidth(), detail.getHeight(), "cm"),
                        Math.toIntExact(detail.getQty()), packageCollection.getCollectionNo(), detail.getDeclaredValue(), ""));
            }
        }
        calcShipmentFeeCommand.setPackageInfos(packageInfos);
        // ????????????
        calcShipmentFeeCommand.setToAddress(new Address(packageCollection.getReceiverAddress(),
                "",
                "",
                packageCollection.getReceiverPostCode(),
                packageCollection.getReceiverCity(),
                packageCollection.getReceiverProvince(),
                new CountryInfo(packageCollection.getReceiverCountryCode(), "", "", packageCollection.getReceiverCountry())
        ));
        // ????????????
        calcShipmentFeeCommand.setFromAddress(new Address(packageCollection.getCollectionAddress(),
                "",
                "",
                packageCollection.getCollectionPostCode(),
                packageCollection.getCollectionCity(),
                packageCollection.getCollectionProvince(),
                new CountryInfo(packageCollection.getCollectionCountryCode(), "", "", packageCollection.getCollectionCountry())
        ));
        // ????????????
        calcShipmentFeeCommand.setToContactInfo(new ContactInfo(packageCollection.getReceiverName(), packageCollection.getReceiverPhone(), "", ""));
        calcShipmentFeeCommand.setCalcTimeForDiscount(new Date());
        // ????????????
        return this.htpPricedProductClientService.pricing(calcShipmentFeeCommand);
    }

    /**
     * ??????package - ???????????? - ????????????
     *
     * @param packageCollection package - ???????????? - ????????????
     * @return ??????
     */
    @Override
    public int updatePackageCollection(PackageCollection packageCollection) {
        packageCollection.setTotalQty(this.countTotalQty(packageCollection.getDetailList()));

        if(packageCollection.getAmount() != null) {
            BigDecimal amount = BigDecimalUtil.setScale(packageCollection.getAmount(), BigDecimalUtil.PRICE_SCALE);
            packageCollection.setAmount(amount);
        }

        if(packageCollection.getCalcWeight() != null) {
            BigDecimal calcWeight = BigDecimalUtil.setScale(packageCollection.getCalcWeight(), BigDecimalUtil.WEIGHT_SCALE);
            packageCollection.setCalcWeight(calcWeight);
        }

        int update = baseMapper.updateById(packageCollection);
        if (update > 0) {
            LambdaQueryWrapper<PackageCollectionDetail> packageCollectionDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
            packageCollectionDetailLambdaQueryWrapper.eq(PackageCollectionDetail::getCollectionId, packageCollection.getId());
            this.packageCollectionDetailService.remove(packageCollectionDetailLambdaQueryWrapper);
            this.saveDetail(packageCollection.getId(), packageCollection.getDetailList());
        }
        return update;
    }

    @Override
    public int updatePackageCollectionPlan(PackageCollection packageCollection) {
        List<Long> idList = packageCollection.getIdList();
        if (CollectionUtils.isNotEmpty(idList)) {
            PackageCollection updatePackageCollection = new PackageCollection();
            updatePackageCollection.setStatus(PackageCollectionConstants.Status.PLANNED.name());
            updatePackageCollection.setCollectionPlan(PackageCollectionConstants.COLLECTION_PLAN_YES);
            updatePackageCollection.setCollectionDate(packageCollection.getCollectionDate());
            updatePackageCollection.setHandleMode(packageCollection.getHandleMode());
            LambdaUpdateWrapper<PackageCollection> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.in(PackageCollection::getId, idList);
            int update = super.baseMapper.update(updatePackageCollection, updateWrapper);
            if (update > 0) {
                List<PackageCollection> packageCollectionList = super.listByIds(idList);
                for (PackageCollection collection : packageCollectionList) {
                    // ?????????????????????
                    PackageCollectionContext packageCollectionContextCreateReceiver = new PackageCollectionContext(collection, PackageCollectionContext.Type.CREATE_RECEIVER, false);
                    PackageCollectionContextEvent.publishEvent(packageCollectionContextCreateReceiver);
                }
                //??????????????????????????????
                LambdaQueryWrapper<PackageCollection> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.in(PackageCollection::getId, idList);
                List<PackageCollection> dataList = super.baseMapper.selectList(queryWrapper);
                dataList.stream().forEach(e -> {
                    e.setDetailList(this.packageCollectionDetailService.listByCollectionId(e.getId()));
                    //?????????????????????????????????
                    createPackageService(e);
                });
            }
            return update;
        }
        return 0;
    }

    @Override
    public int updateOutboundNo(PackageCollection packageCollection) {
        LambdaUpdateWrapper<PackageCollection> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(PackageCollection::getOutboundNo, packageCollection.getOutboundNo());
        updateWrapper.eq(PackageCollection::getCollectionNo, packageCollection.getCollectionNo());
        return super.baseMapper.update(null, updateWrapper);
    }

    @Override
    public int cancel(List<Long> idList) {
        List<PackageCollection> collectionList = super.listByIds(idList);
        if (CollectionUtils.isNotEmpty(collectionList)) {
            // ?????????????????????????????????????????????????????????????????????????????????
            for (PackageCollection collection : collectionList) {
                if (!(PackageCollectionConstants.Status.PLANNED.name().equals(collection.getStatus())
                        || PackageCollectionConstants.Status.NO_PLAN.name().equals(collection.getStatus()))) {
                    throw new CommonException("500", collection.getCollectionNo() + "???????????????????????????????????????");
                }
                // ??????????????????
                this.cancelFreight(collection);
                // ?????????????????????
                this.cancelOperatingFee(collection);
            }
            // ?????????????????????
            this.cancelCarrier(collectionList);
            // ?????????????????????????????????
            LambdaUpdateWrapper<PackageCollection> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
            lambdaUpdateWrapper.set(PackageCollection::getStatus, PackageCollectionConstants.Status.CANCELLED.name());
            lambdaUpdateWrapper.in(PackageCollection::getId, idList);
            super.update(lambdaUpdateWrapper);
            return collectionList.size();
        }
        return 0;
    }

    @Override
    public void notRecordCancel(PackageCollection packageCollection) {
        LambdaQueryWrapper<PackageCollectionOperationRecord> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(PackageCollectionOperationRecord::getCollectionNo, packageCollection.getCollectionNo());
        List<PackageCollectionOperationRecord> operationRecordList = this.packageCollectionOperationRecordService.list(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(operationRecordList)) {
            for (PackageCollectionOperationRecord operationRecord : operationRecordList) {
                if (PackageCollectionOperationRecordConstants.Type.FREIGHT.name().equals(operationRecord.getType())) {
                    this.cancelFreight(packageCollection);
                } else if (PackageCollectionOperationRecordConstants.Type.CARRIER.name().equals(operationRecord.getType())) {
                    this.cancelCarrier(Collections.singletonList(packageCollection));
                } else if (PackageCollectionOperationRecordConstants.Type.OPERATING_FEE.name().equals(operationRecord.getType())) {
                    this.cancelOperatingFee(packageCollection);
                }
            }
            this.packageCollectionOperationRecordService.remove(lambdaQueryWrapper);
        }
    }

    private void createTrackingYee(PackageCollection collection) {
        boolean success = false;
        String responseBody;
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", collection.getTrackingNo());
            shipment.put("carrierCode", collection.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", collection.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", collection.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            Date createTime = collection.getCreateTime();
            if (null != createTime) {
                shipment.put("OrdersOn", DateFormatUtils.format(createTime, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
            }
            shipment.put("paymentTime", "");
            shipment.put("shippingOn", "");
            List<String> searchTags = new ArrayList<>();
            searchTags.add(collection.getSellerCode());
            searchTags.add(collection.getCollectionNo());
            shipment.put("searchTags", searchTags);
            shipment.put("orderNo", collection.getCollectionNo());
            Map<String, Object> senderAddress = new HashMap<>();
            senderAddress.put("country", collection.getCollectionCountry());
            senderAddress.put("province", collection.getCollectionProvince());
            senderAddress.put("city", collection.getCollectionCity());
            senderAddress.put("postcode", collection.getCollectionPostCode());
            senderAddress.put("street1", collection.getCollectionAddress());
            senderAddress.put("street2", "");
            senderAddress.put("street3", "");
            shipment.put("senderAddress", senderAddress);
            Map<String, Object> destinationAddress = new HashMap<>();
            destinationAddress.put("country", collection.getReceiverCountry());
            destinationAddress.put("province", collection.getReceiverProvince());
            destinationAddress.put("city", collection.getReceiverCity());
            destinationAddress.put("postcode", collection.getReceiverPostCode());
            destinationAddress.put("street1", collection.getReceiverAddress());
            destinationAddress.put("street2", "");
            destinationAddress.put("street3", "");
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            recipientInfo.put("recipient", collection.getReceiverName());
            recipientInfo.put("phoneNumber", collection.getReceiverPhone());
            recipientInfo.put("email", "");
            shipment.put("recipientInfo", recipientInfo);
            Map<String, Object> customFieldInfo = new HashMap<>();
            customFieldInfo.put("fieldOne", collection.getCollectionNo());
            customFieldInfo.put("fieldTwo", "");
            customFieldInfo.put("fieldThree", "");
            shipment.put("customFieldInfo", customFieldInfo);
            shipments.add(shipment);
            requestBodyMap.put("shipments", shipments);
            HttpRequestDto httpRequestDto = new HttpRequestDto();
            httpRequestDto.setMethod(HttpMethod.POST);
            String url = DomainEnum.TrackingYeeDomain.wrapper("/tracking/v1/shipments");
            httpRequestDto.setUri(url);
            httpRequestDto.setBody(requestBodyMap);
            HttpResponseVO httpResponseVO = htpRmiClientService.rmi(httpRequestDto);
            if (200 == httpResponseVO.getStatus() ||
                    201 == httpResponseVO.getStatus()) {
                success = true;
            }
            responseBody = (String) httpResponseVO.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseBody = e.getMessage();
            if (null == responseBody) {
                responseBody = "????????????";
            }
        }
        // ?????????????????????????????????
        if (success) {
            try {
                // ?????????????????????????????????????????????
                JSONObject jsonObject = JSON.parseObject(responseBody);
                // ?????????????????????OK
                if ("OK".equals(jsonObject.getString("status"))) {
                    // ????????????????????????????????????
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (1 != data.getIntValue("successNumber")) {
                        // ???????????????????????????1??????????????????
                        success = false;
                        // ??????????????????
                        int failNumber = data.getIntValue("failNumber");
                        if (failNumber > 0) {
                            JSONArray failImportRowResults = data.getJSONArray("failImportRowResults");
                            JSONObject failImportRowResult = failImportRowResults.getJSONObject(0);
                            JSONObject errorInfo = failImportRowResult.getJSONObject("errorInfo");
                            String errorCode = errorInfo.getString("errorCode");
                            String errorMessage = errorInfo.getString("errorMessage");
                            throw new CommonException("500", "[" + errorCode + "]" + errorMessage);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof CommonException) {
                    throw e;
                }
                // ??????????????????????????????
                success = false;
            }
        }
        if (!success) {
            throw new CommonException("500", "??????TrackingYee??????");
        }
    }

    private void cancelFreight(PackageCollection collection) {
        CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
        cusFreezeBalanceDTO.setAmount(collection.getAmount());
        cusFreezeBalanceDTO.setCurrencyCode(collection.getCurrencyCode());
        cusFreezeBalanceDTO.setCusCode(collection.getSellerCode());
        cusFreezeBalanceDTO.setNo(collection.getCollectionNo());
        cusFreezeBalanceDTO.setOrderType("Freight");
        R<?> thawBalanceR = this.rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
        if (null == thawBalanceR) {
            throw new CommonException("400", "?????????????????????????????????????????????");
        }
        if (Constants.SUCCESS != thawBalanceR.getCode()) {
            String msg = thawBalanceR.getMsg();
            if (StringUtils.isEmpty(msg)) {
                msg = "????????????????????????";
            }
            throw new CommonException("400", msg);
        }
    }

    private void cancelOperatingFee(PackageCollection collection) {
        DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
        delOutboundOperationVO.setOrderType("PackageCollection");
        delOutboundOperationVO.setOrderNo(collection.getCollectionNo());
        R<?> r = this.operationFeignService.delOutboundThaw(delOutboundOperationVO);
        if (null == r) {
            throw new CommonException("400", "???????????????????????????????????????????????????");
        }
        if (Constants.SUCCESS != r.getCode()) {
            String msg = r.getMsg();
            if (StringUtils.isEmpty(msg)) {
                msg = "??????????????????????????????";
            }
            throw new CommonException("400", msg);
        }
    }

    private void cancelCarrier(List<PackageCollection> collectionList) {
        if (CollectionUtils.isEmpty(collectionList)) {
            return;
        }
        Map<String, List<PackageCollection>> collectionListMap = collectionList.stream().collect(Collectors.groupingBy(PackageCollection::getWarehouseCode));
        for (String warehouseCode : collectionListMap.keySet()) {
            try {
                CancelShipmentOrderCommand command = new CancelShipmentOrderCommand();
                command.setWarehouseCode(warehouseCode);
                command.setReferenceNumber(UUID.randomUUID().toString().replaceAll("-", ""));
                List<PackageCollection> list = collectionListMap.get(warehouseCode);
                if (CollectionUtils.isNotEmpty(list)) {
                    List<CancelShipmentOrder> cancelShipmentOrders = new ArrayList<>();
                    for (PackageCollection collection : list) {
                        cancelShipmentOrders.add(new CancelShipmentOrder(collection.getShipmentOrderNumber(), collection.getTrackingNo()));
                    }
                    command.setCancelShipmentOrders(cancelShipmentOrders);
                    this.htpCarrierClientService.cancellation(command);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private int countTotalQty(List<PackageCollectionDetail> detailList) {
        int totalQty = 0;
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (PackageCollectionDetail detail : detailList) {
                Integer qty = detail.getQty();
                if (null == qty) {
                    qty = 0;
                }
                totalQty += qty;
            }
        }
        return totalQty;
    }

    private void saveDetail(Long collectionId, List<PackageCollectionDetail> detailList) {
        if (CollectionUtils.isNotEmpty(detailList)) {
            int sort = 0;
            for (PackageCollectionDetail detail : detailList) {
                detail.setCollectionId(collectionId);
                detail.setSort(sort++);

                BigDecimal calcWeight = BigDecimalUtil.setScale(detail.getWeight(),BigDecimalUtil.WEIGHT_SCALE);
                detail.setWeight(calcWeight);
            }
            this.packageCollectionDetailService.saveBatch(detailList);
        }
    }

    /**
     * ????????????package - ???????????? - ????????????
     *
     * @param ids ???????????????package - ???????????? - ????????????ID
     * @return ??????
     */
    @Override
    public int deletePackageCollectionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * ??????package - ???????????? - ??????????????????
     *
     * @param id package - ???????????? - ????????????ID
     * @return ??????
     */
    @Override
    public int deletePackageCollectionById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public IPage<PackageCollection> page(PackageCollectionQueryDto dto) {
        IPage<PackageCollection> iPage = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<PackageCollection> queryWrapper = Wrappers.lambdaQuery();
        // ???????????????????????????
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null != loginUser) {
            String sellerCode = loginUser.getSellerCode();
            queryWrapper.eq(StringUtils.isNotEmpty(sellerCode), PackageCollection::getSellerCode, sellerCode);
        }
        // ?????????????????? ???????????????????????????????????????
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (StringUtils.isEmpty(dto.getCustomCode())) {
                dto.setCustomCode(cusCode);
            }
        }
        if (CollectionUtils.isNotEmpty(dto.getCustomCodeList())) {
            queryWrapper.in(PackageCollection::getSellerCode, dto.getCustomCodeList());
        }

        String queryNo = dto.getQueryNoOne();
        if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(queryNo)) {
            List<String> queryNoList = splitToArray(queryNo, "[\n,]");
            queryWrapper.in(PackageCollection::getOutboundNo,queryNoList).or().in(PackageCollection::getTrackingNo,queryNoList);
        }
        // ????????????
        this.autoSettingListCondition(queryWrapper, PackageCollection::getCollectionNo, this.getTextList(dto.getCollectionNo()));
        // ?????????
        this.autoSettingListCondition(queryWrapper, PackageCollection::getTrackingNo, this.getTextList(dto.getTrackingNo()));
        // ????????????
        this.autoSettingDateCondition(queryWrapper, PackageCollection::getCreateTime, dto.getCreateTimes());
        // ????????????
        this.autoSettingDateCondition(queryWrapper, PackageCollection::getCollectionDate, dto.getCollectionTimes());
        // ??????
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getStatus()), PackageCollection::getStatus, dto.getStatus());
        // ?????????
        queryWrapper.like(StringUtils.isNotEmpty(dto.getCollectionName()), PackageCollection::getCollectionName, dto.getCollectionName());
        // ???????????????
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getCollectionToWarehouse()), PackageCollection::getCollectionToWarehouse, dto.getCollectionToWarehouse());
        // ????????????
        queryWrapper.eq(StringUtils.isNotEmpty(dto.getHandleMode()), PackageCollection::getHandleMode, dto.getHandleMode());
        queryWrapper.orderByDesc(PackageCollection::getCreateTime);
        IPage<PackageCollection> page = super.page(iPage, queryWrapper);
        List<PackageCollection> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<Long> collectionIdList = records.stream().map(PackageCollection::getId).collect(Collectors.toList());
            Map<Long, String> collectionSkuNames = this.packageCollectionDetailService.getCollectionSkuNames(collectionIdList);
            if (MapUtils.isNotEmpty(collectionSkuNames)) {
                for (PackageCollection record : records) {
                    record.setSkuNames(collectionSkuNames.getOrDefault(record.getId(), "-"));
                }
            }
        }
        return page;
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }

    @Override
    public int updateCollecting(String collectionNo) {
        PackageCollection packageCollection = this.getByCollectionNo(collectionNo);
        if (null != packageCollection) {
            if (PackageCollectionConstants.Status.PLANNED.name().equals(packageCollection.getStatus())) {
                PackageCollection updatePackageCollection = new PackageCollection();
                updatePackageCollection.setStatus(PackageCollectionConstants.Status.COLLECTING.name());
                updatePackageCollection.setId(packageCollection.getId());
                return super.baseMapper.updateById(updatePackageCollection);
            }
            // ???????????????
            return -1;
        }
        // ???????????????
        return -2;
    }

    @Override
    public int updateCollectingCompleted(String collectionNo) {
        PackageCollection packageCollection = this.getByCollectionNo(collectionNo);
        if (null != packageCollection) {
            if (PackageCollectionConstants.Status.PLANNED.name().equals(packageCollection.getStatus())
                    || PackageCollectionConstants.Status.COLLECTING.name().equals(packageCollection.getStatus())) {
                PackageCollection updatePackageCollection = new PackageCollection();
                updatePackageCollection.setStatus(PackageCollectionConstants.Status.COMPLETED.name());
                updatePackageCollection.setId(packageCollection.getId());
                int i = super.baseMapper.updateById(updatePackageCollection);
                if (i > 0) {
                    // ?????????????????????
                    PackageCollectionCompletedEvent.publishEvent(packageCollection);
                }
                return i;
            }
            // ???????????????
            return -1;
        }
        // ???????????????
        return -2;
    }

    private PackageCollection getByCollectionNo(String collectionNo) {
        LambdaQueryWrapper<PackageCollection> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PackageCollection::getCollectionNo, collectionNo);
        return super.getOne(queryWrapper);
    }

    @Override
    public void collectionLabel(String collectionNo, HttpServletResponse response) {
        PackageCollection packageCollection = this.getByCollectionNo(collectionNo);
        if (null != packageCollection) {
            // ??????????????????
            String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/package/tmp");
            // ????????????
            basedir += "/label";
            // ?????????
            Date createTime = packageCollection.getCreateTime();
            String datePath = DateFormatUtils.format(createTime, "yyyy/MM/dd");
            basedir += "/" + datePath;
            // ???????????????
            String pathname = basedir + "/" + packageCollection.getCollectionNo() + ".pdf";
            // ????????????????????????
            File packageCollectionLabelFile = new File(pathname);
            if (packageCollectionLabelFile.exists()) {
                // ???????????????????????????????????????????????????
                try {
                    byte[] byteArray = FileUtils.readFileToByteArray(packageCollectionLabelFile);
                    this.output(response, byteArray, packageCollection.getCollectionNo() + ".pdf");
                    // end
                    return;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", "???????????????????????????????????????????????????");
                }
            }
            // ????????????
            CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
            command.setWarehouseCode(packageCollection.getWarehouseCode());
            command.setOrderNumber(packageCollection.getShipmentOrderNumber());
            command.setShipmentOrderLabelUrl(packageCollection.getShipmentOrderLabelUrl());
            logger.info("??????????????????????????????????????????{}????????????????????????{}", packageCollection.getCollectionNo(), packageCollection.getShipmentOrderNumber());
            ResponseObject<FileStream, ProblemDetails> responseObject = this.htpCarrierClientService.label(command);
            if (null != responseObject) {
                if (responseObject.isSuccess()) {
                    // ???????????????????????????
                    File file = new File(basedir);
                    if (!file.exists()) {
                        try {
                            // ???????????????
                            FileUtils.forceMkdir(file);
                        } catch (IOException e) {
                            // ?????????????????????????????????????????????
                            throw new CommonException("500", "???????????????[" + file.getPath() + "]?????????Error???" + e.getMessage());
                        }
                    }
                    byte[] inputStream;
                    FileStream fileStream = responseObject.getObject();
                    if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                        // ?????????????????????
                        File labelFile = new File(pathname);
                        try {
                            FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                        } catch (IOException e) {
                            // ?????????????????????????????????????????????
                            throw new CommonException("500", "???????????????????????????Error???" + e.getMessage());
                        }
                        // ???????????????
                        try {
                            this.output(response, inputStream, packageCollection.getCollectionNo() + ".pdf");
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            throw new CommonException("500", "???????????????????????????????????????????????????");
                        }
                        // end
                    }
                } else {
                    // ?????????????????????????????????
                    String exceptionMessage = ProblemDetails.getErrorMessageOrNull(responseObject.getError());
                    if (StringUtils.isNotEmpty(exceptionMessage)) {
                        exceptionMessage = "???????????????????????????????????????????????????";
                    }
                    throw new CommonException("500", exceptionMessage);
                }
            } else {
                // ??????????????????????????????
                throw new CommonException("500", "?????????????????????????????????????????????");
            }
        } else {
            throw new CommonException("500", "?????????????????????");
        }
    }

    private void output(HttpServletResponse response, byte[] bytes, String name) {
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            //response???HttpServletResponse??????
            response.setContentType("application/pdf;charset=utf-8");
            //Loading plan.xls??????????????????????????????????????????????????????????????????????????????
            response.setHeader("Content-Disposition", "attachment;filename=" + name);
            IOUtils.write(bytes, outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "?????????????????????????????????????????????");
        } finally {
            if (null != outputStream) {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    private void autoSettingDateCondition(LambdaQueryWrapper<PackageCollection> queryWrapper, SFunction<PackageCollection, ?> column, String[] dates) {
        if (ArrayUtils.isNotEmpty(dates) && dates.length == 2) {
            queryWrapper.between(column, dates[0] + " 00:00:00", dates[1] + " 23:59:59");
        }
    }

    private void autoSettingListCondition(LambdaQueryWrapper<PackageCollection> queryWrapper, SFunction<PackageCollection, ?> column, List<?> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() == 1) {
                queryWrapper.eq(column, list.get(0));
            } else {
                queryWrapper.in(column, list);
            }
        }
    }

    private List<String> getTextList(String text) {
        if (StringUtils.isNotEmpty(text)) {
            List<String> textList;
            if (text.contains("\n") || text.contains("\r")) {
                if (text.contains("\r")) {
                    text = text.replaceAll("\r\n", "\n");
                    text = text.replaceAll("\r", "\n");
                }
                String[] textArray = text.split("\n");
                textList = Arrays.stream(textArray).collect(Collectors.toList());
            } else {
                textList = Collections.singletonList(text);
            }
            return textList;
        } else {
            return null;
        }
    }
}

