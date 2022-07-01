package com.szmsd.http.dto.grade;

import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "GradeMainDto", description = "客户方案-修改折扣方案规则")
public class GradeMainDto {


    @ApiModelProperty("折扣方案Id")
    private String id;

    @ApiModelProperty("折扣方案名称")
    private String name;


    @ApiModelProperty("优先级，值越大，优先级别越高")
    private String order;

    @ApiModelProperty("备注")
    private String remark;


    @ApiModelProperty("有效开始时间")
    private String effectiveBeginTime;

    @ApiModelProperty("有效结束时间")
    private String effectiveEndTime;

    @ApiModelProperty("关联的产品集合")
    private List<GradeDto> pricingGradeTemplateRules;

    @ApiModelProperty("关联的客户集合")
    private List<AssociatedCustomersDto> associatedCustomers;

    @ApiModelProperty("创建人信息")
    private Operation creation;

    @ApiModelProperty("修改人信息")
    private Operation lastModifyOperation;





}
