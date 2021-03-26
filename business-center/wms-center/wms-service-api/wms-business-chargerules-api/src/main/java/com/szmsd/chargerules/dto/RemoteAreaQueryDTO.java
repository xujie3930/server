package com.szmsd.chargerules.dto;

import com.szmsd.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "RemoteAreaQueryDTO", description = "分页查询地址库模板列表")
public class RemoteAreaQueryDTO extends PageDomain {

    @ApiModelProperty("模板名称")
    private String name;

}
