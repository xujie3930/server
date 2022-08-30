package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.util.ITextPdfFontUtil;
import com.szmsd.delivery.util.ITextPdfUtil;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-08 16:56
 */
@Slf4j
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

    /**
     * 承运商标签文件路径
     *
     * @param delOutbound delOutbound
     * @return String
     */
    public static String getLabelFilePath(DelOutbound delOutbound) {
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
        String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
        return basedir + "/shipment/label/" + labelBizPath;
    }

    /**
     * 转运简易标签文件路径
     *
     * @param delOutbound delOutbound
     * @return String
     */
    public static String getPackageTransferLabelFilePath(DelOutbound delOutbound) {
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
        String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
        return basedir + "/simple/label/" + labelBizPath;
    }

    public static String getSelfPickLabelFilePath(DelOutbound delOutbound) {
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery/tmp");
        String labelBizPath = DelOutboundServiceImplUtil.getLabelBizPath(delOutbound);
        return basedir + "/selfPick/label/" + labelBizPath;
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
        throwCommonException(r, "400", "400", "冻结操作费用失败");
    }

    /**
     * 取消冻结操作费用失败
     *
     * @param r r
     */
    public static void thawOperationThrowCommonException(R<?> r) {
        throwCommonException(r, "400", "400", "取消冻结操作费用失败");
    }

    /**
     * 扣减操作费用失败
     *
     * @param r r
     */
    public static void chargeOperationThrowCommonException(R<?> r) {
        throwCommonException(r, "400", "400", "扣减操作费用失败");
    }

    public static void chargeOperationThrowCommonException2(R<?> r) {
        throwCommonException(r, "400", "400", "扣减物料费用失败");
    }

    /**
     * 扣减物料费用失败
     *
     * @param r r
     */
    public static void packageThrowCommonException(R<?> r) {
        throwCommonException(r, "400", "400", "扣减物料费用失败");
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
            throwCommonException(code2, throwMsg + "，" + r.getMsg());
        }
    }

    private static void throwCommonException(String code, String msg) {
        log.error("【throwCommonException】{},{}", code, msg);
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
        // 特殊处理，支持输入出库单号、跟踪号、refNO，任一单号进行查询
        String refNo = queryDto.getRefNo();
        // 出库单号
        List<String> delOutboundNoList = new ArrayList<>();
        // 跟踪号，refNo
        List<String> otherQueryNoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(refNo)) {
            List<String> nos = splitToArray(refNo, "[\n,]");
            if (CollectionUtils.isNotEmpty(nos)) {
                for (String no : nos) {
                    // CK/RECK开头的是出库单号
                    if (no.startsWith("CK") || no.startsWith("RECK")) {
                        delOutboundNoList.add(no);
                    } else {
                        otherQueryNoList.add(no);
                    }
                }
                queryWrapper.and(wrapper -> {
                    // tracking_no in (xxx) or ref_no in (xxx)
                    if (CollectionUtils.isNotEmpty(otherQueryNoList)) {
                        wrapper.in("o.tracking_no", otherQueryNoList)
                                .or().in("o.ref_no", otherQueryNoList)
                                .or().in("o.amazon_logistics_route_id", otherQueryNoList);

                    }
                    // [or] order_no in (xxx)
                    if (CollectionUtils.isNotEmpty(delOutboundNoList)) {
                        wrapper.or().in("o.order_no", delOutboundNoList);
                    }
                });
            }
        }
        String orderNo = queryDto.getOrderNo();
        if (StringUtils.isNotEmpty(orderNo)) {
            if (orderNo.contains("\n") || orderNo.contains(",")) {
                List<String> list = splitToArray(orderNo, "[\n,]");
                queryWrapper.in(CollectionUtils.isNotEmpty(list), "o.order_no", list);
            } else {
                queryWrapper.likeRight("o.order_no", orderNo);
            }
        }
        String purchaseNo = queryDto.getPurchaseNo();
        if (StringUtils.isNotEmpty(purchaseNo)) {
            if (purchaseNo.contains("\n") || purchaseNo.contains(",")) {
                List<String> list = splitToArray(purchaseNo, "[\n,]");
                queryWrapper.in(CollectionUtils.isNotEmpty(list), "o.purchase_no", list);
            } else {
                queryWrapper.likeRight("o.purchase_no", purchaseNo);
            }
        }
        String trackingNo = queryDto.getTrackingNo();
        if (StringUtils.isNotEmpty(trackingNo)) {
            if (trackingNo.contains("\n") || trackingNo.contains(",")) {
                List<String> list = splitToArray(trackingNo, "[\n,]");
                queryWrapper.in(CollectionUtils.isNotEmpty(list), "o.tracking_no", list);
            } else {
                queryWrapper.likeRight("o.tracking_no", trackingNo);
            }
        }
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.shipment_rule", queryDto.getShipmentRule());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.warehouse_code", queryDto.getWarehouseCode());
        //QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.state", queryDto.getState());
        if (StringUtils.isNotBlank(queryDto.getState())) {
            queryWrapper.in("o.state", StringToolkit.getCodeByArray(queryDto.getState()));
        }

        String orderType = queryDto.getOrderType();
        if (StringUtils.isNotEmpty(orderType)) {
            if (orderType.contains("\n") || orderType.contains(",")) {
                List<String> list = splitToArray(orderType, "[\n,]");
                queryWrapper.in(CollectionUtils.isNotEmpty(list), "o.order_type", list);
            } else {
                queryWrapper.eq("o.order_type", orderType);
            }
        }
        List<String> customCodeList = queryDto.getCustomCodeList();
        if(CollectionUtils.isNotEmpty(customCodeList)) {
//            queryWrapper.in("o.seller_code", customCodeList);
            queryWrapper.in("o.custom_code", customCodeList);
        }
//        QueryWrapperUtil.filter(queryWrapper, SqlLike.DEFAULT, "o.custom_code", queryDto.getCustomCode());
        // 创建时间
        QueryWrapperUtil.filterDate(queryWrapper, "o.create_time", queryDto.getCreateTimes());
        // 发货时间
        QueryWrapperUtil.filterDate(queryWrapper, "o.shipments_time", queryDto.getShipmentsTimes());
        // 重派订单
        String reassignType = queryDto.getReassignType();
        if (StringUtils.isEmpty(reassignType)) {
            // 如果没有传重派订单的逻辑，默认就查询不是重派订单
            reassignType = DelOutboundConstant.REASSIGN_TYPE_N;
        }
        // 后端接口查询重派和非重派订单
        if (queryDto.getQueryAll() != null && queryDto.getQueryAll()) {
            reassignType = null;
        }
        queryWrapper.eq(StringUtils.isNotBlank(reassignType), "o.reassign_type", reassignType);
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "o.tracking_status", queryDto.getTrackingStatus());
        // 删除标识
        String delFlag = queryDto.getDelFlag();
        if (StringUtils.isEmpty(delFlag)) {
            // 2已删除，0未删除
            delFlag = "0";
        }
        queryWrapper.eq("o.del_flag", delFlag);

        // 国家查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryDto.getCountryCode()), "a.country_code", queryDto.getCountryCode());


        // 按照创建时间倒序
        queryWrapper.orderByDesc("o.create_time");
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }

    public static ByteArrayOutputStream renderSelfPick(DelOutbound delOutbound, List<DelOutboundDetail> detailList, String skuLabel) throws Exception {
        Document document = new Document();
        document.top(0f);
        document.left(0f);
        document.right(0f);
        document.bottom(0f);
        document.setMargins(10f, 10f, 0f, 0f);
        // 页面大小
        int width = 100;
        int height = 100;
        float dpi = 72f;
        Rectangle rec = new Rectangle(ITextPdfUtil.mm2px(width, dpi), ITextPdfUtil.mm2px(height, dpi));
        rec.rotate();
        document.setPageSize(rec);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 为该Document创建应该Writer实例
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        byte[] fontBytes = ITextPdfFontUtil.getFont("fonts/ARIALUNI.TTF");
        BaseFont bf = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.NOT_CACHED, fontBytes, fontBytes);
        //3. 注册字体
        Font font = new Font(bf, 18);
//        font.setSize(12f);
        /*Paragraph weight = new Paragraph(delOutbound.getWeight() + " g", font);
        weight.setAlignment(Element.ALIGN_LEFT);
        weight.setSpacingBefore(0f);
        weight.setSpacingAfter(0f);
        weight.setPaddingTop(0f);
        document.add(weight);*/
        //3. 添加段落,并设置字体
        // 文本块(Chunk)、短语(Phrase)和段落(paragraph)处理文本
        font.setSize(18f);
        Paragraph paragraph = new Paragraph(delOutbound.getShipmentRule(), font);
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        paragraph.setSpacingBefore(-26f);
        paragraph.setSpacingAfter(0f);
        paragraph.setPaddingTop(5f);
        document.add(paragraph);
        //3.添加pdf tables 3表示列数，
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.setSpacingBefore(5f);
        pdfPTable.setSpacingAfter(-9f);
        pdfPTable.setWidths(new float[]{1f});
        pdfPTable.setPaddingTop(0f);

        PdfPCell pdfPCell = new PdfPCell();
        pdfPCell.setPadding(0f);
        pdfPCell.setPaddingLeft(5f);
        pdfPCell.setPaddingRight(5f);
        pdfPCell.setBorder(15);
        pdfPCell.setBorderWidth(1.5f);
        pdfPCell.setFixedHeight(135f);

        Phrase element = new Phrase();
        pdfPCell.addElement(element);
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < detailList.size(); i++) {
            DelOutboundDetail detail = detailList.get(i);
            buf.append(","+detail.getSku() + "*" + detail.getQty());
        }
        if(buf.length() == 0){
            pdfPCell.addElement(new Phrase(""));

        }else{
            pdfPCell.addElement(new Phrase(buf.substring(1)));

        }
        pdfPTable.addCell(pdfPCell);

        document.add(pdfPTable);
        // 条形码
        String content = delOutbound.getOrderNo();
        BufferedImage bufferedImage = ITextPdfUtil.getBarCode(content);
        BufferedImage image = ITextPdfUtil.insertWords(bufferedImage, content);
        Image image1 = Image.getInstance(image, null);
        // 计算缩放比例
        // 渲染在画布上的宽度只有200，以200作为基础比例
        image1.scalePercent(55f);
        // image1.setSpacingBefore(-10f);
        // image1.setAbsolutePosition(10f, 25f);
        document.add(image1);
        if (StringUtils.isNotEmpty(skuLabel)) {
            font.setSize(10f);
            Paragraph paragraph_label = new Paragraph(skuLabel, font);
            paragraph_label.setAlignment(Element.ALIGN_LEFT);
            paragraph_label.setSpacingBefore(1f);
            paragraph_label.setSpacingAfter(0f);
            paragraph_label.setPaddingTop(0f);
            paragraph_label.setMultipliedLeading(1f);
            document.add(paragraph_label);
        }
        document.close();
        writer.close();
        return byteArrayOutputStream;
    }

    public static ByteArrayOutputStream renderPackageTransfer(DelOutbound delOutbound, DelOutboundAddress delOutboundAddress, String skuLabel) throws Exception {
        Document document = new Document();
        document.top(0f);
        document.left(0f);
        document.right(0f);
        document.bottom(0f);
        document.setMargins(10f, 10f, 0f, 0f);
        // 页面大小
        int width = 100;
        int height = 100;
        float dpi = 72f;
        Rectangle rec = new Rectangle(ITextPdfUtil.mm2px(width, dpi), ITextPdfUtil.mm2px(height, dpi));
        rec.rotate();
        document.setPageSize(rec);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 为该Document创建应该Writer实例
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        byte[] fontBytes = ITextPdfFontUtil.getFont("fonts/ARIALUNI.TTF");
        BaseFont bf = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.NOT_CACHED, fontBytes, fontBytes);
        //3. 注册字体
        Font font = new Font(bf, 18);
        font.setSize(12f);
        Paragraph weight = new Paragraph(delOutbound.getWeight() + " g", font);
        weight.setAlignment(Element.ALIGN_LEFT);
        weight.setSpacingBefore(0f);
        weight.setSpacingAfter(0f);
        weight.setPaddingTop(0f);
        document.add(weight);
        //3. 添加段落,并设置字体
        // 文本块(Chunk)、短语(Phrase)和段落(paragraph)处理文本
        font.setSize(18f);
        Paragraph paragraph = new Paragraph(delOutbound.getShipmentRule(), font);
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        paragraph.setSpacingBefore(-26f);
        paragraph.setSpacingAfter(0f);
        paragraph.setPaddingTop(0f);
        document.add(paragraph);
        //3.添加pdf tables 3表示列数，
        PdfPTable pdfPTable = new PdfPTable(1);
        pdfPTable.setWidthPercentage(100f);
        pdfPTable.setSpacingBefore(5f);
        pdfPTable.setSpacingAfter(-9f);
        pdfPTable.setWidths(new float[]{1f});
        pdfPTable.setPaddingTop(0f);
        for (int i = 0; i < 2; i++) {
            PdfPCell pdfPCell = new PdfPCell();
            pdfPCell.setPadding(0f);
            pdfPCell.setPaddingLeft(5f);
            pdfPCell.setPaddingRight(5f);
            pdfPCell.setBorder(15);
            pdfPCell.setBorderWidth(1.5f);
            if (i == 0) {
                pdfPCell.setFixedHeight(135f);
                Phrase element = new Phrase("To:");
                pdfPCell.addElement(element);
                pdfPCell.addElement(new Phrase(delOutboundAddress.getConsignee()));
                pdfPCell.addElement(new Phrase(delOutboundAddress.getStreet1()));
                pdfPCell.addElement(new Phrase(delOutboundAddress.getStreet2()));
                pdfPCell.addElement(new Phrase(delOutboundAddress.getCity() + " " + delOutboundAddress.getStateOrProvince() + " " + delOutboundAddress.getPostCode()));
                // 国家二字码
                String text = delOutboundAddress.getCountryCode();
                if (StringUtils.isEmpty(text)) {
                    // 二字码是空的就取国家名称
                    text = delOutboundAddress.getCountry();
                }
                Paragraph country = new Paragraph(text, font);
                country.setAlignment(Element.ALIGN_RIGHT);
                country.setSpacingBefore(-5f);
                country.setSpacingAfter(0f);
                pdfPCell.addElement(country);
                pdfPCell.addElement(new Phrase("TEL:" + delOutboundAddress.getPhoneNo()));
            } else {
                pdfPCell.setFixedHeight(38f);
                font.setSize(12f);
                Phrase element = new Phrase("Custom:" + delOutbound.getRemark(), font);
                element.setMultipliedLeading(1f);
                pdfPCell.addElement(element);
            }
            pdfPTable.addCell(pdfPCell);
        }
        document.add(pdfPTable);
        // 条形码
        String content = delOutbound.getOrderNo();
        BufferedImage bufferedImage = ITextPdfUtil.getBarCode(content);
        BufferedImage image = ITextPdfUtil.insertWords(bufferedImage, content);
        Image image1 = Image.getInstance(image, null);
        // 计算缩放比例
        // 渲染在画布上的宽度只有200，以200作为基础比例
        image1.scalePercent(55f);
        // image1.setSpacingBefore(-10f);
        // image1.setAbsolutePosition(10f, 25f);
        document.add(image1);
        if (StringUtils.isNotEmpty(skuLabel)) {
            font.setSize(10f);
            Paragraph paragraph_label = new Paragraph(skuLabel, font);
            paragraph_label.setAlignment(Element.ALIGN_LEFT);
            paragraph_label.setSpacingBefore(1f);
            paragraph_label.setSpacingAfter(0f);
            paragraph_label.setPaddingTop(0f);
            paragraph_label.setMultipliedLeading(1f);
            document.add(paragraph_label);
        }
        document.close();
        writer.close();
        return byteArrayOutputStream;
    }
}
