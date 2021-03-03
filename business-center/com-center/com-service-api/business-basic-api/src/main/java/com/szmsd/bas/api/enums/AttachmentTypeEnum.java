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
