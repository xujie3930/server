package com.szmsd.exception.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.exception.domain.ExceptionInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExceptionInfoDto extends ExceptionInfo {
    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;
}
