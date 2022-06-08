package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 查件设置
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="查件设置", description="DelQuerySettings对象")
public class DelQuerySettings extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
            @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "国家代码")
    @Excel(name = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String country;

    @ApiModelProperty(value = "物流服务编号")
    @Excel(name = "物流服务编号")
    private String shipmentRule;

    @ApiModelProperty(value = "物流服务名称")
    @Excel(name = "物流服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private String state;

    @ApiModelProperty(value = "状态名称")
    @Excel(name = "状态名称")
    private String stateName;

    @ApiModelProperty(value = "发货天数")
    @Excel(name = "发货天数")
    private Integer shipmentDays;

    @ApiModelProperty(value = "轨迹停留天数")
    @Excel(name = "轨迹停留天数")
    private Integer trackStayDays;


}
