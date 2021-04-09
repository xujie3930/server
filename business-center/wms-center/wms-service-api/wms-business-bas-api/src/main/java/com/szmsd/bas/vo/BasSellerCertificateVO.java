package com.szmsd.bas.vo;

import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerCertificateVO extends BasSellerCertificate {

    @ApiModelProperty(value = "对版图片")
    private AttachmentFileDTO editionImage;
}
