package com.szmsd.doc.component;

import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IRemoterApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:22
 */
public interface IRemoterApi {
    RemoteAttachmentService getRemoteAttachmentService();

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

    boolean checkSkuBelong(String sku);

    boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku);

    Map<String, BasSubWrapperVO> getSubNameByCode(String subCode);
}
