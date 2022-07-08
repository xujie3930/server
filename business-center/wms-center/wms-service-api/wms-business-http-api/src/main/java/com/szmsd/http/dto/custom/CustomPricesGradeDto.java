package com.szmsd.http.dto.custom;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.http.vo.DateOperation;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomPricesMainDto", description = "客户方案-等级")
public class CustomPricesGradeDto {

    @ApiModelProperty("唯一标识")
    private String id;


    @ApiModelProperty("折扣方案Id")
    private String templateId;


    @ApiModelProperty("折扣方案名称")
    private String templateName;


    @ApiModelProperty("是否自有方案模板")
    private String isSelfTemplate;


    @ApiModelProperty("关联产品代码(报价等级和报价折扣关联产品代码，处理费与仓租关联仓库代码)")
    private List<String> associatedCodes;

    @ApiModelProperty("优先级（数越大优先级越高）")
    private Integer order;

    @ApiModelProperty("备注")
    private String remark;


    @ApiModelProperty("有效起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginTime;


    @ApiModelProperty("有效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("创建人")
    private DateOperation creation;

    @ApiModelProperty("操作人")
    private String author;


    @ApiModelProperty("创建时间")
    private String date;
}
