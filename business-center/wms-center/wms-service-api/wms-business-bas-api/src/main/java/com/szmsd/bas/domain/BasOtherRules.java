package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
* <p>
    * 其他规则匹配
    * </p>
*
* @author Administrator
* @since 2022-05-16
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="其他规则匹配", description="BasOtherRules对象")
public class BasOtherRules extends BaseEntity {

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

    @ApiModelProperty(value = "发货方式")
    @Excel(name = "发货方式")
    private String deliveryMethod;

    @ApiModelProperty(value = "自定义标识")
    @Excel(name = "自定义标识")
    private String customIdentification;

    @ApiModelProperty(value = "回传规则")
    @Excel(name = "回传规则")
    private String returnRule;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    @NotEmpty(message = "sellerCode不能为空")
    private String sellerCode;


}
