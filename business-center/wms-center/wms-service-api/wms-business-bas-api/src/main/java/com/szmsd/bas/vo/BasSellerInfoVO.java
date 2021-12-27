package com.szmsd.bas.vo;

import com.szmsd.bas.domain.BasSeller;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.vo.UserCreditInfoVO;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasSellerInfoVO extends BasSeller {

    @ApiModelProperty(value = "授信额度信息")
    private List<UserCreditInfoVO> userCreditList;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private List<BasSellerCertificateVO> basSellerCertificateList;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentFileDTO> documentsFiles;

}
