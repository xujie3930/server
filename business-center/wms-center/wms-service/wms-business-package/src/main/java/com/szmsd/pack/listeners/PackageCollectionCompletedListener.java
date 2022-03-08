package com.szmsd.pack.listeners;

import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.events.PackageCollectionCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PackageCollectionCompletedListener {
    private final Logger logger = LoggerFactory.getLogger(PackageCollectionContextListener.class);

    @SuppressWarnings({"all"})
    @Autowired
    private RechargesFeignService rechargesFeignService;
    @SuppressWarnings({"all"})
    @Autowired
    private OperationFeignService operationFeignService;

    @Async
    @EventListener
    public void onApplicationEvent(PackageCollectionCompletedEvent event) {
        PackageCollection packageCollection = (PackageCollection) event.getSource();
        logger.info(">>>>>>>>>>>>揽收单完成，执行扣费操作，揽收单号：{}", packageCollection.getCollectionNo());
        try {
            // 扣减费用
            CustPayDTO custPayDTO = new CustPayDTO();
            custPayDTO.setCusCode(packageCollection.getSellerCode());
            custPayDTO.setCurrencyCode(packageCollection.getCurrencyCode());
            custPayDTO.setAmount(packageCollection.getAmount());
            custPayDTO.setNo(packageCollection.getCollectionNo());
            custPayDTO.setPayMethod(BillEnum.PayMethod.BALANCE_DEDUCTIONS);
            custPayDTO.setOrderType("Freight");
            R<?> r = this.rechargesFeignService.feeDeductions(custPayDTO);
            if (null == r || Constants.SUCCESS != r.getCode()) {
                String message;
                if (null != r) {
                    message = "扣减费用失败，" + r.getMsg();
                } else {
                    message = "扣减费用失败";
                }
                throw new CommonException("500", message);
            }
            // 扣减操作费
            DelOutboundOperationVO delOutboundOperationVO = new DelOutboundOperationVO();
            delOutboundOperationVO.setOrderType("PackageCollection");
            delOutboundOperationVO.setOrderNo(packageCollection.getCollectionNo());
            R<?> r2 = this.operationFeignService.delOutboundCharge(delOutboundOperationVO);
            if (null == r2 || Constants.SUCCESS != r2.getCode()) {
                String message;
                if (null != r2) {
                    message = "扣减操作费用失败，" + r2.getMsg();
                } else {
                    message = "扣减操作费用失败";
                }
                throw new CommonException("500", message);
            }
        } catch (Exception e) {
            logger.info(">>>>>>>>>>>>揽收单完成，执行扣费操作，扣费失败，揽收单号：{}", packageCollection.getCollectionNo());
            logger.error(e.getMessage(), e);
        }
    }
}
