package com.szmsd.bas.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasMessageDto extends BasMessage {


    @ApiModelProperty(value = "是否读取")
    @Excel(name = "是否读取")
    private Boolean readable;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentFileDTO> revealDocumentsFiles;


}
