package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "BasPartner", description = "BasPartner对象")
public class BasPartner extends BaseEntity {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "伙伴名称")
    private String partnerName;

    @ApiModelProperty(value = "订单删除标识")
    private Boolean deleteOrderFlag;

    @ApiModelProperty("当前页，从1开始")
    @TableField(exist = false)
    private int pageNum = 1;

    @ApiModelProperty("每页的数量")
    @TableField(exist = false)
    private int pageSize = 10;
}
