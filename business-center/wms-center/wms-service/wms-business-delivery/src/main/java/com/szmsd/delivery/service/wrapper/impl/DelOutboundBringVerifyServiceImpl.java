package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-23 16:33
 */
@Service
public class DelOutboundBringVerifyServiceImpl implements IDelOutboundBringVerifyService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundBringVerifyServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Override
    public int bringVerify(Long id) {
        // 根据id查询出库信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        // 可以提审的状态：待提审，审核失败
        if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState()))) {
            throw new CommonException("999", "单据不能提审");
        }
        // 查询地址信息
        DelOutboundAddress address = this.delOutboundAddressService.getByOrderNo(delOutbound.getOrderNo());
        if (null == address) {
            throw new CommonException("999", "收货地址信息不存在");
        }
        // 查询sku信息
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(delOutbound.getOrderNo());
        if (CollectionUtils.isEmpty(detailList)) {
            throw new CommonException("999", "出库明细不存在");
        }
        // 查询仓库信息
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(delOutbound.getWarehouseCode());
        if (null == warehouse) {
            throw new CommonException("999", "仓库信息不存在");
        }
        // 修改单据状态为提审中
        this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.UNDER_REVIEW);
        try {
            // 计算包裹费用
            CalcShipmentFeeCommand command = new CalcShipmentFeeCommand();
            // 产品代码就是选择的物流承运商
            command.setProductCode(delOutbound.getShipmentRule());
            command.setClientCode(delOutbound.getCustomCode());
            command.setShipmentType(delOutbound.getShipmentType());
            // 包裹信息
            command.setPackageInfos(null);
            // 收货地址
            command.setToAddress(new Address());
            // 发货地址
            command.setFromAddress(new Address());
            // 联系信息
            command.setToContactInfo(new ContactInfo(address.getConsignee(), address.getPhoneNo(), address.getEmail(), null));
            command.setCalcTimeForDiscount(new Date());
            // 调用接口
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = this.htpPricedProductClientService.pricing(command);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了

                } else {
                    // 计算失败

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 回滚状态
            this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.AUDIT_FAILED);
        }
        return 0;
    }
}
