package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 客户报价子表
 * </p>
 *
 * @author ziling
 * @since 2020-09-21
 */
@TableName("bas_cusprice_code")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCuspriceCode对象", description = "客户报价子表")
public class BasCuspriceCode {

    private static final long serialVersionUID = 1L;

    @Excel(name = "id")
    private String id;

    @ApiModelProperty(value = "1=客户 2=目的")
    @Excel(name = "1=客户 2=目的")
    private String types;

    @ApiModelProperty(value = "code")
    @Excel(name = "code")
    private String code;

    @ApiModelProperty(value = "name")
    @Excel(name = "name")
    private String name;

    @ApiModelProperty(value = "报价id")
    @Excel(name = "报价id")
    private String cuspriceId;


}
