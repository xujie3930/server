package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasSellerInfoDto extends BasSeller {

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private List<BasSellerCertificate> basSellerCertificateList;

}
