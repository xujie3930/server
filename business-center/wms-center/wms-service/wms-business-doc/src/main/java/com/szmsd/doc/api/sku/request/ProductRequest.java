package com.szmsd.doc.api.sku.request;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductRequest extends BaseProductRequest {
    @ApiModelProperty(value = "产品图片Base64")
    private String productImageBase64;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;
}
