package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * <p>
 *
 * </p>
 *
 * @author YM
 * @since 2022-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "", description = "BasCarrierKeyword对象")
public class BasCarrierKeyword extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @NotBlank(message = "承运商不能为空")
    @ApiModelProperty(value = "承运商code")
    @Excel(name = "承运商code")
    private String carrierCode;

    @ApiModelProperty(value = "承运商名称")
    @Excel(name = "承运商名称")
    private String carrierName;

    @NotBlank(message = "关键词不能为空")
    @ApiModelProperty(value = "关键词（多个使用英文逗号拼接）")
    @Excel(name = "关键词（多个使用英文逗号拼接）")
    private String keywords;

    @ApiModelProperty(value = "状态（0停用 1正常）")
    @Excel(name = "状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改者名称")
    @Excel(name = "修改者名称")
    private String updateBy;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除")
    @Excel(name = "删除标志（0代表存在 2代表删除")
    private String delFlag;

    @ApiModelProperty(value = "关键词字表")
    @TableField(exist = false)
    private List<BasCarrierKeywordData> basCarrierKeywordDataList;



}
