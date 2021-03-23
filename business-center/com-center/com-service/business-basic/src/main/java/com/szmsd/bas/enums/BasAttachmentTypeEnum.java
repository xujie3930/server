package com.szmsd.bas.enums;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author admpon
 * 附件类型枚举
 */

@Getter
@AllArgsConstructor
public enum BasAttachmentTypeEnum {

    PREFIX_TEMP("PREFIX_TEMP", "临时目录", "临时文件", ""),
    INBOUND_RECEIPT_DOCUMENTS("INBOUND_RECEIPT", "入库单", "单证信息文件", "documents"),
    INBOUND_RECEIPT_EDITION_IMAGE("INBOUND_RECEIPT", "入库单", "对版图片", "editionImage"),
    PAYMENT_DOCUMENT("PAYMENT_DOCUMENT","汇款凭证","汇款凭证","paymentDocument"),
    ;

    /** 业务编码 **/
    private String businessCode;

    /** 业务类型 **/
    private String businessType;

    /** 附件类型 **/
    private String attachmentType;

    /** 存在哪个目录下 **/
    private String fileDirectory;

    public static BasAttachmentTypeEnum getEnum(String businessCode, String attachmentType) {
        for (BasAttachmentTypeEnum basAttachmentTypeEnum : values()) {
            if (basAttachmentTypeEnum.getBusinessCode().equals(businessCode) && basAttachmentTypeEnum.getAttachmentType().equals(attachmentType)) {
                return basAttachmentTypeEnum;
            }
        }
        throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
    }

}
