package com.szmsd.finance.domain;

import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author liulei
 */
public class FssBaseEntity extends BaseEntity {
    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;
}
