package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LanguageEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 仓库客户黑白名单
 */
@Data
@Accessors(chain = true)
@TableName("bas_warehouse_cus")
public class BasWarehouseCus {

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "客户代码")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_CUSTOMER, language = LanguageEnum.enName)
    private String cusName;

    @ApiModelProperty(value = "标识：0黑名单, 1白名单")
    private String express;

    @ApiModelProperty(value = "创建ID",hidden = true)
    private String createBy;

    @ApiModelProperty(value = "创建名称",hidden = true)
    private String createByName;

}
