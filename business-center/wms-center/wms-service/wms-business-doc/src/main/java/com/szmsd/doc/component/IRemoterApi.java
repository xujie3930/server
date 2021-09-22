package com.szmsd.doc.component;

import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.doc.api.warehouse.req.FileInfoBase64;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IRemoterApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:22
 */
public interface IRemoterApi {
    BasSellerFeignService getBasSellerFeignService();

    RemoteAttachmentService getRemoteAttachmentService();

    List<BasAttachmentDataDTO> uploadFile(List<FileInfoBase64> base64List, AttachmentTypeEnum attachmentTypeEnum);

    List<WarehouseKvDTO> queryCusInboundWarehouse();

    void getUserInfo();

    /**
     * 校验仓库是否存在
     *
     * @param warehouse 仓库名
     * @return
     */
    boolean verifyWarehouse(String warehouse);

    /**
     * 校验sku归属
     *
     * @param sellerCode 客户 null 不能为空字符串
     * @param warehouse  仓库 null 不能为空字符串
     * @param sku        必填 sku
     * @return
     */
    boolean checkSkuBelong(String sellerCode, String warehouse, String sku);

    /**
     * 校验sku是否有图片
     * @param skuList
     * @return
     */
    boolean checkSkuPic(List<String> skuList,AttachmentTypeEnum attachmentTypeEnum);

    boolean checkSkuBelong(String sku);

    boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku);

    Map<String, BasSubWrapperVO> getSubNameByCode(String subCode);

    List<BaseProduct> listSku(BaseProduct baseProduct);

    /**
     * 校验包材
     *
     * @return
     */
    boolean checkPackBelong(String bindCode);
    //校验并设置skuName信息
    boolean checkSkuBelong(String cusCode, List<String> skuList, List<CreateInboundReceiptDTO> addDTO);
}
