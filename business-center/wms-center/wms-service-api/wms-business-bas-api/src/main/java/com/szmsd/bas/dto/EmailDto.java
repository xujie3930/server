package com.szmsd.bas.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author jiangjun
 * @since 2022-10-12
 */
@Data
@ApiModel(value = "", description = "EmailDto对象")
public class EmailDto {

    @ApiModelProperty(value = "收件人")
    private String to;

    @ApiModelProperty(value = "主题")
    private String subject;

    @ApiModelProperty(value = "文本类容")
    private String text;

    @ApiModelProperty(value = "文件资源")
    private String filePath;

    @ApiModelProperty(value = "0表示包裹查询导入更新挂号，1（其他功能）")
    private Integer modularType;


    @ApiModelProperty(value = "员工编号")
    private String empCode;

    @ApiModelProperty(value = "数据集合")
    List<EmailObjectDto> list ;
}
