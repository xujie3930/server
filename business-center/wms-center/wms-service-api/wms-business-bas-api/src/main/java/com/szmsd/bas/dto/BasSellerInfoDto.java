package com.szmsd.bas.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BasSellerInfoDto extends BasSeller {

    @ApiModelProperty(value = "授信类型(0：额度，1：期限)")
    @Excel(name = "授信类型(0：额度，1：期限)")
    private Integer creditType;
    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;
    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;
    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private List<BasSellerCertificateDto> basSellerCertificateList;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;

}
