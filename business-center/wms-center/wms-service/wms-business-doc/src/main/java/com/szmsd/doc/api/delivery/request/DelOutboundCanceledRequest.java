package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:07
 */
@Data
@ApiModel(value = "DelOutboundCanceledRequest", description = "DelOutboundCanceledRequest对象")
public class DelOutboundCanceledRequest implements Serializable {

    /**
     * 设置字段不能为null
     * 设置字段名称IDS，字段必填，数据类型为Long，字段顺序为1，参考值[12579]
     */
    @NotNull(message = "IDS不能为空")
    @ApiModelProperty(value = "IDS", required = true, dataType = "Long", position = 1, example = "[12579]")
    private List<Long> ids;
}
