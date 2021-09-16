package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:10
 */
@Data
@ApiModel(value = "DelOutboundBatchSkuDetailRequest", description = "DelOutboundBatchSkuDetailRequest对象")
public class DelOutboundBatchSkuDetailRequest implements Serializable {

    @NotBlank(message = "SKU不能为空", groups = {DelOutboundGroup.Default.class})
    @Size(max = 50, message = "SKU不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "SKU", dataType = "String")
    private String sku;

    @NotNull(message = "数量不能为空", groups = {DelOutboundGroup.Default.class})
    @Max(value = Integer.MAX_VALUE, message = "数量不能大于2147483647", groups = {DelOutboundGroup.Default.class})
    @Min(value = 1, message = "数量不能小于1", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "数量", required = true, dataType = "Long", example = "1")
    private Long qty;

    @Size(max = 50, message = "新标签不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "新标签", dataType = "String", example = "")
    private String newLabelCode;

}
