package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.util.Utils;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-08 16:56
 */
public final class DelOutboundServiceImplUtil {

    private DelOutboundServiceImplUtil() {
    }

    public static void handlerInventoryOperate(DelOutboundDetail detail, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        String invoiceLineNo = String.valueOf(detail.getLineNo());
        String sku = detail.getSku();
        int qty = Math.toIntExact(detail.getQty());
        // 判断有没有包材，包材也需要冻结
        String bindCode = detail.getBindCode();
        handlerInventoryOperate(invoiceLineNo, sku, bindCode, qty, inventoryOperateDtoMap);
    }

    public static void handlerInventoryOperate(DelOutboundDetailDto detail, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        String invoiceLineNo = String.valueOf(detail.getLineNo());
        String sku = detail.getSku();
        int qty = Math.toIntExact(detail.getQty());
        // 判断有没有包材，包材也需要冻结
        String bindCode = detail.getBindCode();
        handlerInventoryOperate(invoiceLineNo, sku, bindCode, qty, inventoryOperateDtoMap);
    }

    private static void handlerInventoryOperate(String invoiceLineNo, String sku, String bindCode, int qty, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        handlerInventoryOperate(invoiceLineNo, sku, qty, inventoryOperateDtoMap);
        if (StringUtils.isNotEmpty(bindCode)) {
            handlerInventoryOperate(invoiceLineNo, bindCode, qty, inventoryOperateDtoMap);
        }
    }

    private static void handlerInventoryOperate(String invoiceLineNo, String sku, int qty, Map<String, InventoryOperateDto> inventoryOperateDtoMap) {
        if (inventoryOperateDtoMap.containsKey(sku)) {
            inventoryOperateDtoMap.get(sku).addQty(qty);
        } else {
            inventoryOperateDtoMap.put(sku, new InventoryOperateDto(invoiceLineNo, sku, qty));
        }
    }

    private static String getLabelBizPath(DelOutbound delOutbound) {
        // 单据类型/年/月/日/单据号
        Date createTime = delOutbound.getCreateTime();
        String datePath = DateFormatUtils.format(createTime, "yyyy/MM/dd");
        return delOutbound.getOrderType() + "/" + datePath;
    }

    public static String getLabelFilePath(DelOutbound delOutbound) {
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
        String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
        return basedir + "/shipment/label/" + labelBizPath;
    }

    /**
     * 获取到合并之后的文件路径
     *
     * @param delOutbound delOutbound
     * @return String
     */
    public static String getBatchMergeFilePath(DelOutbound delOutbound) {
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
        String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
        return basedir + "/shipment/merge/" + labelBizPath;
    }

    /**
     * 不需要操作库存
     *
     * @param orderType 出库单类型
     * @return true不需要，false需要
     */
    public static boolean noOperationInventory(String orderType) {
        // 转运出库
        // 集运出库
        // 不需要冻结库存
        /*return DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType)
                || DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType);*/
        // 转运出库需要冻结库存
        return DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType);
    }

    /**
     * 冻结操作费用失败
     *
     * @param r r
     */
    public static void freezeOperationThrowErrorMessage(R<?> r) {
        throwCommonException(r, "1900", "1901", "冻结操作费用失败");
    }

    /**
     * 取消冻结操作费用失败
     *
     * @param r r
     */
    public static void thawOperationThrowCommonException(R<?> r) {
        throwCommonException(r, "1910", "1911", "取消冻结操作费用失败");
    }

    /**
     * 扣减操作费用失败
     *
     * @param r r
     */
    public static void chargeOperationThrowCommonException(R<?> r) {
        throwCommonException(r, "1920", "1921", "扣减操作费用失败");
    }

    /**
     * 扣减物料费用失败
     *
     * @param r r
     */
    public static void packageThrowCommonException(R<?> r) {
        throwCommonException(r, "1930", "1931", "扣减物料费用失败");
    }

    /**
     * 对<code>R<?></code>结果集进行判断
     * 当结果集出现异常，执行抛出异常的动作
     *
     * @param r        返回的结果集对象
     * @param code     r结果为空时提示的异常编码
     * @param code2    r失败时提示的异常编码
     * @param throwMsg 当r是null时提示的异常信息；当r的msg是空时，提示的异常信息
     */
    private static void throwCommonException(R<?> r, String code, String code2, String throwMsg) {
        if (null == r) {
            throwCommonException(code, throwMsg);
        }
        if (Constants.SUCCESS != r.getCode()) {
            throwCommonException(code2, Utils.defaultValue(r.getMsg(), throwMsg));
        }
    }

    private static void throwCommonException(String code, String msg) {
        throw new CommonException(code, msg);
    }

    /**
     * 加入hit位
     *
     * @param value value
     * @param key   key
     * @return int
     */
    public static int joinKey(int value, int key) {
        return (value | key);
    }

    /**
     * 判断有没有hit位
     *
     * @param value value
     * @param key   key
     * @return boolean
     */
    public static boolean hitKey(int value, int key) {
        return (value & key) == key;
    }

    /**
     * 出库单公共查询条件处理
     *
     * @param queryWrapper queryWrapper
     * @param queryDto     queryDto
     */
    public static void handlerQueryWrapper(QueryWrapper<DelOutboundListQueryDto> queryWrapper, DelOutboundListQueryDto queryDto) {
        String orderNo = queryDto.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            if (orderNo.contains(",")) {
                queryWrapper.in("o.order_no", Arrays.asList(orderNo.split(",")));
            } else {
                queryWrapper.likeRight("o.order_no", orderNo);
            }
        }
        String purchaseNo = queryDto.getPurchaseNo();
        if (StringUtils.isNotEmpty(purchaseNo)) {
            if (purchaseNo.contains(",")) {
                queryWrapper.in("o.purchase_no", Arrays.asList(purchaseNo.split(",")));
            } else {
                queryWrapper.likeRight("o.purchase_no", purchaseNo);
            }
        }
        String trackingNo = queryDto.getTrackingNo();
        if (StringUtils.isNotEmpty(trackingNo)) {
            if (trackingNo.contains(",")) {
                queryWrapper.in("o.tracking_no", Arrays.asList(trackingNo.split(",")));
            } else {
                queryWrapper.likeRight("o.tracking_no", trackingNo);
            }
        }
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.shipment_rule", queryDto.getShipmentRule());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.warehouse_code", queryDto.getWarehouseCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.state", queryDto.getState());
        String orderType = queryDto.getOrderType();
        if (StringUtils.isNotEmpty(orderType)) {
            if (orderType.contains(",")) {
                String[] split = orderType.split(",");
                queryWrapper.in("o.order_type", Arrays.asList(split));
            } else {
                queryWrapper.eq("o.order_type", orderType);
            }
        }
        QueryWrapperUtil.filter(queryWrapper, SqlLike.DEFAULT, "o.custom_code", queryDto.getCustomCode());
        QueryWrapperUtil.filterDate(queryWrapper, "o.create_time", queryDto.getCreateTimes());
        // 按照创建时间倒序
        queryWrapper.orderByDesc("o.create_time");
    }
}
