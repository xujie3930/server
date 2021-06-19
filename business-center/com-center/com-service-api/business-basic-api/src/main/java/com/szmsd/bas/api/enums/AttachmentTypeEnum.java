package com.szmsd.bas.api.enums;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author admpon
 */

@Getter
@AllArgsConstructor
public enum AttachmentTypeEnum {

    PREFIX_TEMP("PREFIX_TEMP", "临时目录", "临时文件", ""),
    SKU_IMAGE("SKU_IMAGE","基础信息","SKU图片","skuImage"),
    SELLER_CERTIFICATE_DOCUMENT("SELLER_CERTIFICATE_DOCUMENT","基础信息","客户认证资料文件","sellerCertificateDocument"),
    INBOUND_RECEIPT_DOCUMENTS("INBOUND_RECEIPT", "入库单", "单证信息文件", "documents"),
    INBOUND_RECEIPT_EDITION_IMAGE("INBOUND_RECEIPT", "入库单", "对版图片", "editionImage"),
    PAYMENT_DOCUMENT("PAYMENT_DOCUMENT","汇款凭证","","paymentDocument"),
    DEL_OUTBOUND_DOCUMENT("DEL_OUTBOUND_DOCUMENT","出库单","物流面单","delOutboundDocument"),
    DEL_OUTBOUND_BATCH_LABEL("DEL_OUTBOUND_BATCH_LABEL","出库单批量出库箱标","出库箱标","delOutboundBatchLabel"),
    EXCEPTION_DOCUMENT("EXCEPTION_DOCUMENT","异常信息","异常处理附件","exceptionDocument"),
    MESSAGE_IMAGE("MESSAGE_IMAGE","信息通知","信息通知图片","messageImage"),
    SELLER_IMAGE("SELLER_IMAGE","基础信息","客户实名认证图片","sellerImage"),
    ;

    /** 业务编码 **/
    private String businessCode;

    /** 业务类型 **/
    private String businessType;

    /** 附件类型 **/
    private String attachmentType;

    /** 存在哪个目录下 **/
    private String fileDirectory;

    public static AttachmentTypeEnum getEnum(String businessCode, String attachmentType) {
        for (AttachmentTypeEnum attachmentTypeEnum : values()) {
            if (attachmentTypeEnum.getBusinessCode().equals(businessCode) && attachmentTypeEnum.getAttachmentType().equals(attachmentType)) {
                return attachmentTypeEnum;
            }
        }
        throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
    }

}
