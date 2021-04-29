package com.szmsd.inventory.component;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.enums.InboundReceiptEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 远程请求
 */
@Component
@Slf4j
public class RemoteRequest {

    @Resource
    private HtpInboundFeignService htpInboundFeignService;

    /**
     * 调用WMS创建单
     *
     * @param inboundReceiptInfoVO
     */
    public void createPackage(InboundReceiptInfoVO inboundReceiptInfoVO, List<String> transferNoList) {
        CreatePackageReceiptRequest createPackageReceiptRequest = new CreatePackageReceiptRequest();
        ArrayList<ReceiptDetailPackageInfo> receiptDetailPackageInfos = new ArrayList<>();

        String warehouseNo = inboundReceiptInfoVO.getWarehouseNo();


        transferNoList.forEach(x->{
            ReceiptDetailPackageInfo receiptDetailPackageInfo = new ReceiptDetailPackageInfo();
            //统一传出库单号
            receiptDetailPackageInfo.setPackageOrderNo(x);
            receiptDetailPackageInfo.setScanCode(x);
            receiptDetailPackageInfos.add(receiptDetailPackageInfo);
        });


        createPackageReceiptRequest
                .setWarehouseCode(inboundReceiptInfoVO.getWarehouseCode())
                .setRemark(inboundReceiptInfoVO.getRemark())
                .setOrderType("PackageTransfer")
                .setSellerCode(inboundReceiptInfoVO.getCusCode())
                .setRefOrderNo(inboundReceiptInfoVO.getWarehouseNo())
                .setDetailPackages(receiptDetailPackageInfos)
        ;
        log.info("调用WMS创建入库单{}",createPackageReceiptRequest);
        R<ResponseVO> aPackage = htpInboundFeignService.createPackage(createPackageReceiptRequest);
        ResponseVO.resultAssert(aPackage, "创建转运入库单");
    }

}
