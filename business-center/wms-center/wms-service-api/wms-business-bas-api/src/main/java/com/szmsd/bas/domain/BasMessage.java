package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
* <p>
    * 
    * </p>
*
* @author l
* @since 2021-04-25
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasMessage对象")
public class BasMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
            @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    @Excel(name = "内容")
    private String content;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String type;

    @ApiModelProperty(value = "是否弹框")
    @Excel(name = "是否弹框")
    private Boolean bullet;


}
