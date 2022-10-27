package com.szmsd.http.dto.custom;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "客户方案折扣和等级本地表", description = "BasCustomPricesgradeDto对象")
public class BasCustomPricesgradeDto extends QueryDto {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "折扣方案Id")
    private String templateId;

    @ApiModelProperty(value = "折扣方案名称")
    private String templateName;

    @ApiModelProperty(value = "是否自有方案模板")
    private String isSelfTemplate;

    @ApiModelProperty(value = "优先级（数越大优先级越高）")
    private Integer orderOn;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "有效起始时间")
    private Date beginTime;

    @ApiModelProperty(value = "有效结束时间")
    private Date endTime;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "0表示折扣方案，1表示等级方案")
    private String customprType;


    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "客户代码集合")
    private List<String> sellerCodeList;









}