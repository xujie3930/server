package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:29
 */
@Service
public class DelOutboundAsyncServiceImpl implements IDelOutboundAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private RechargesFeignService rechargesFeignService;
    @Value("${server.tomcat.basedir:/u01/www/ck1/delivery/tmp}")
    private String basedir;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;
    @Autowired
    private IDelOutboundChargeService delOutboundChargeService;

    @Transactional
    @Override
    public int shipmentPacking(Long id) {
        // 获取新的出库单信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        ApplicationContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        ShipmentEnum currentState;
        String shipmentState = delOutbound.getShipmentState();
        if (StringUtils.isEmpty(shipmentState)) {
            currentState = ShipmentEnum.BEGIN;
        } else {
            currentState = ShipmentEnum.get(shipmentState);
        }
        new ApplicationContainer(context, currentState, ShipmentEnum.END, ShipmentEnum.BEGIN).action();
        /*
        DelOutbound updateDelOutbound = new DelOutbound();
        updateDelOutbound.setId(id);
        try {
            DelOutboundWrapperContext delOutboundWrapperContext = this.delOutboundBringVerifyService.initContext(delOutbound);
            String orderNumber;
            // 判断获取承运商信息
            if (DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType())) {
                ShipmentOrderResult shipmentOrderResult = this.delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
                String trackingNo;
                updateDelOutbound.setTrackingNo(trackingNo = shipmentOrderResult.getMainTrackingNumber());
                updateDelOutbound.setShipmentOrderNumber(orderNumber = shipmentOrderResult.getOrderNumber());
                // 保存挂号
                updateDelOutbound.setTrackingNo(trackingNo);
                // 更新WMS挂号
                ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
                shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getRefOrderNo());
                shipmentTrackingChangeRequestDto.setTrackingNo(trackingNo);
                this.htpOutboundClientService.shipmentTracking(shipmentTrackingChangeRequestDto);
            } else {
                orderNumber = delOutbound.getShipmentOrderNumber();
            }
            // 处理物流规则是None
            if (StringUtils.isNotEmpty(orderNumber)) {
                // 获取标签
                ResponseObject<FileStream, ProblemDetails> responseObject = this.htpCarrierClientService.label(orderNumber);
                if (null != responseObject) {
                    if (responseObject.isSuccess()) {
                        FileStream fileStream = responseObject.getObject();
                        // 保存文件
                        File file = new File(this.basedir + "/shipment/label");
                        if (!file.exists()) {
                            FileUtils.forceMkdir(file);
                        }

                        byte[] inputStream;
                        if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                            File labelFile = new File(file.getPath() + "/" + orderNumber);
                            FileOutputStream fileOutputStream = null;
                            ByteArrayInputStream byteArrayInputStream = null;
                            try {
                                fileOutputStream = new FileOutputStream(labelFile);
                                byteArrayInputStream = new ByteArrayInputStream(inputStream);
                                byte[] read = new byte[1024];
                                if (byteArrayInputStream.read(read) != -1) {
                                    fileOutputStream.write(read);
                                }
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            } finally {
                                if (null != fileOutputStream) {
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                }
                            }
                            if (null != byteArrayInputStream) {
                                try {
                                    String encode = Base64.encode(byteArrayInputStream);
                                    ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                                    shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getRefOrderNo());
                                    shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
                                    shipmentLabelChangeRequestDto.setLabel(encode);
                                    this.htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                } finally {
                                    byteArrayInputStream.close();
                                }
                            }
                        }
                    } else {
                        // 计算失败
                        String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "获取标签文件流失败");
                        throw new CommonException("999", exceptionMessage);
                    }
                }
            }
            // 获取运费信息
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = this.delOutboundBringVerifyService.pricing(delOutboundWrapperContext);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    Weight calcWeight = packageInfo.getCalcWeight();
                    updateDelOutbound.setCalcWeight(calcWeight.getValue());
                    updateDelOutbound.setCalcWeightUnit(calcWeight.getUnit());
                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    // 取消冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
                    cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
                    cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                    R<?> thawBalanceR = this.rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
                    if (null == thawBalanceR) {
                        throw new CommonException("999", "取消冻结费用失败");
                    }
                    if (Constants.SUCCESS != thawBalanceR.getCode()) {
                        // 异常信息
                        String msg = thawBalanceR.getMsg();
                        if (StringUtils.isEmpty(msg)) {
                            msg = "取消冻结费用失败";
                        }
                        throw new CommonException("999", msg);
                    }
                    // 清除费用信息
                    this.delOutboundChargeService.clearCharges(delOutbound.getOrderNo());
                    // 保存新的费用信息
                    List<DelOutboundCharge> delOutboundCharges = new ArrayList<>();
                    // 汇总费用
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    String totalCurrencyCode = charges.get(0).getMoney().getCurrencyCode();
                    for (ChargeItem charge : charges) {
                        DelOutboundCharge delOutboundCharge = new DelOutboundCharge();
                        ChargeCategory chargeCategory = charge.getChargeCategory();
                        delOutboundCharge.setOrderNo(delOutbound.getOrderNo());
                        delOutboundCharge.setBillingNo(chargeCategory.getBillingNo());
                        delOutboundCharge.setChargeNameCn(chargeCategory.getChargeNameCN());
                        delOutboundCharge.setChargeNameEn(chargeCategory.getChargeNameEN());
                        delOutboundCharge.setParentBillingNo(chargeCategory.getParentBillingNo());
                        Money money = charge.getMoney();
                        BigDecimal amount = Utils.valueOf(money.getAmount());
                        delOutboundCharge.setAmount(amount);
                        delOutboundCharge.setCurrencyCode(money.getCurrencyCode());
                        delOutboundCharge.setRemark(charge.getRemark());
                        delOutboundCharges.add(delOutboundCharge);
                        totalAmount = totalAmount.add(amount);
                    }
                    this.delOutboundChargeService.saveCharges(delOutboundCharges);
                    // 冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO2 = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO2.setAmount(totalAmount);
                    cusFreezeBalanceDTO2.setCurrencyCode(totalCurrencyCode);
                    cusFreezeBalanceDTO2.setCusCode(delOutbound.getSellerCode());
                    R<?> freezeBalanceR = this.rechargesFeignService.freezeBalance(cusFreezeBalanceDTO2);
                    ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                    shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                    shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                    shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                    shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                    if (null != freezeBalanceR) {
                        if (Constants.SUCCESS == freezeBalanceR.getCode()) {
                            // 更新发货指令
                            shipmentUpdateRequestDto.setIsEx(false);
                            shipmentUpdateRequestDto.setExType(null);
                            shipmentUpdateRequestDto.setExRemark(null);
                            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                            this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                        } else {
                            // 异常信息
                            String msg = freezeBalanceR.getMsg();
                            if (StringUtils.isEmpty(msg)) {
                                msg = "冻结费用信息失败";
                            }
                            shipmentUpdateRequestDto.setIsEx(true);
                            shipmentUpdateRequestDto.setExType("FreezeBalanceError");
                            shipmentUpdateRequestDto.setExRemark(msg);
                            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                            this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                            // 异常信息
                            throw new CommonException("999", msg);
                        }
                    } else {
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("FreezeBalanceError");
                        shipmentUpdateRequestDto.setExRemark("冻结费用信息失败");
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                        // 异常信息
                        throw new CommonException("999", "冻结费用信息失败");
                    }
                    // 更新费用信息
                    updateDelOutbound.setAmount(totalAmount);
                    updateDelOutbound.setCurrencyCode(totalCurrencyCode);
                    this.delOutboundService.updateById(updateDelOutbound);
                } else {
                    // 计算失败
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                    throw new CommonException("999", exceptionMessage);
                }
            }
        } catch (CommonException e) {
            logger.error(e.getMessage(), e);
            String exceptionMessage = e.getMessage();
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            this.delOutboundService.bringVerifyFail(id, exceptionMessage);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 提审失败
            String exceptionMessage = "提审操作失败";
            this.delOutboundService.bringVerifyFail(id, exceptionMessage);
            throw new CommonException("999", exceptionMessage);
        }*/
        return 1;
    }
}
