package com.szmsd.bas.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.domain.BasSellerCertificate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasSellerCertificateDto extends BasSellerCertificate {
    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;
}
