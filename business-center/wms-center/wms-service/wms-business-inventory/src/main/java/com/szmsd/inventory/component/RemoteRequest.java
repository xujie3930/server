package com.szmsd.inventory.component;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.api.feign.HtpInventoryFeignService;
import com.szmsd.http.dto.CreatePackageReceiptRequest;
import com.szmsd.http.dto.ReceiptDetailPackageInfo;
import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 远程请求
 */
@Component
@Slf4j
public class RemoteRequest {

    @Resource
    private HtpInboundFeignService htpInboundFeignService;

    @Resource
    private HtpInventoryFeignService htpInventoryFeignService;

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

    /**
     * 查询WMS库存
     * @return
     */
    public List<InventoryInfo> listing(String warehouseCode, String sku) {
        R<List<InventoryInfo>> listing = htpInventoryFeignService.listing(warehouseCode, sku);
        if (listing == null) {
            return null;
        }
        return listing.getData();
    }

}
