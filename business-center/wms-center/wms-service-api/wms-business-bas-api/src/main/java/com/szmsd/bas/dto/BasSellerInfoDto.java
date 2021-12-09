package com.szmsd.bas.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.ThirdPartSystemInfo;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.dto.UserCreditDetailDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class BasSellerInfoDto extends BasSeller {

    @Valid
    @ApiModelProperty(value = "用户授信额度")
    private List<UserCreditDetailDTO> userCreditList;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private List<BasSellerCertificateDto> basSellerCertificateList;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;

    @Valid
    @ApiModelProperty(value = "系统信息（对应系统Token）")
    private List<ThirdPartSystemInfo> systemInfoList;

}
