package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "单位换算关系基础表", description = "单位换算关系基础表")
@TableName("fss_convert_unit")
public class FssConvertUnit extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "计量单位")
    private String calcUnit;

    @ApiModelProperty(value = "换算值")
    private BigDecimal convertValue;

    @ApiModelProperty(value = "换算单位")
    private String convertUnit;


}
