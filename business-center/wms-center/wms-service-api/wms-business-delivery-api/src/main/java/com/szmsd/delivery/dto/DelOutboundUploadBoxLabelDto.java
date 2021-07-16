package com.szmsd.delivery.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundUploadBoxLabelDto", description = "DelOutboundUploadBoxLabelDto对象")
public class DelOutboundUploadBoxLabelDto implements Serializable {

    @ApiModelProperty(value = "出库单号")
    @NotEmpty
    private String orderNo;

    @ApiModelProperty(value = "箱标文件")
    private List<AttachmentDataDTO> batchLabels;

}
