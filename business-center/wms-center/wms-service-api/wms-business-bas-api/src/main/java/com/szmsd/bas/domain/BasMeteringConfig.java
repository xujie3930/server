package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="记泡规则", description="BasMeteringConfig对象")
public class BasMeteringConfig extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "产品code")
    private String logisticsErvicesCode;

    @ApiModelProperty(value = "产品名称")
    private String logisticsErvicesName;

    @ApiModelProperty(value = "国家code")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "客户名称")
    private String customerCode;

    @ApiModelProperty(value = "差异类型（0表示重量差，1表示百分比）")
    private Integer differenceType;

    @ApiModelProperty(value = "创建人")
    private String createBy;


    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "规则明细数据字表")
    @TableField(exist = false)
    private List<BasMeteringConfigData> basMeteringConfigDataList;

}