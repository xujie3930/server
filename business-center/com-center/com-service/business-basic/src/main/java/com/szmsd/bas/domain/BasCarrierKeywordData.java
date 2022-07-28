package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "", description = "BasCarrierKeywordData")
public class BasCarrierKeywordData {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;


    @ApiModelProperty(value = "关键词主表id")
    private Integer carrierKeywordId;

    @NotBlank(message = "关键词类型不能为空")
    @ApiModelProperty(value = "关键词（多个使用英文逗号拼接）")
    private String carrierKeywordType;

    @NotBlank(message = "原始关键词不能为空")
    @ApiModelProperty(value = "原始关键词（多个使用英文逗号拼接）")
    @Excel(name = "原始关键词（多个使用英文逗号拼接）")
    private String originaKeywords;

    @NotBlank(message = "现有关键词不能为空")
    @ApiModelProperty(value = "现有关键词（多个使用英文逗号拼接）")
    @Excel(name = "现有关键词（多个使用英文逗号拼接）")
    private String nowKeywords;


}