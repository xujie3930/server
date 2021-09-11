package com.szmsd.doc.component;

import java.util.List;

/**
 * @ClassName: IRemoterApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-11 14:22
 */
public interface IRemoterApi {
    /**
     * 校验仓库是否存在
     *
     * @param warehouse 仓库名
     * @return
     */
    boolean verifyWarehouse(String warehouse);

    /**
     * 校验sku归属
     * @param sellerCode 客户 null 不能为空字符串
     * @param warehouse 仓库 null 不能为空字符串
     * @param sku 必填 sku
     * @return
     */
    boolean checkSkuBelong(String sellerCode, String warehouse, String sku);
    boolean checkSkuBelong(String sellerCode, String warehouse, List<String> sku);
}
