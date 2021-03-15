package com.szmsd.bas.dto;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "Kv", description = "Kv")
public class WarehouseKvDTO {

    @ApiModelProperty(value = "KEY")
    private String key;

    @ApiModelProperty(value = "VALUE")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String value;

    @ApiModelProperty(value = "国家编码")
    private String country;

}
