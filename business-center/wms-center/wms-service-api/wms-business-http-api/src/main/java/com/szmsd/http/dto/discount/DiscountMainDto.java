package com.szmsd.http.dto.discount;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.dto.grade.GradeDetailDto;
import com.szmsd.http.vo.DateOperation;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "DiscountMainDto", description = "折扣方案主信息")
public class DiscountMainDto {

    @ApiModelProperty("折扣方案Id")
    private String id;

    @ApiModelProperty("折扣方案名称")
    private String name;


    @ApiModelProperty("优先级，值越大，优先级别越高")
    private String order;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("有效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveBeginTime;

    @ApiModelProperty("有效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveEndTime;

    @ApiModelProperty("关联的产品集合")
    private List<DiscountDetailDto> pricingDiscountRules;

    @ApiModelProperty("关联的客户集合")
    private List<AssociatedCustomersDto> associatedCustomers;

    @ApiModelProperty("创建人信息")
    private DateOperation creation;

    @ApiModelProperty("修改人信息")
    private DateOperation lastModifyOperation;







}
