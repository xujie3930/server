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


/**
 * 子母单-子表信息操作日志
 *
 * @author: taoJie
 * @since: 2022-07-13
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "子母单-子表信息操作日志", description = "BasChildParentLog对象")
public class BasChildParentLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "母单客户代码")
    private String parentSellerCode;

    @ApiModelProperty(value = "状态 1审核中 2驳回 3已绑定 4解绑")
    private String state;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;


    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "删除标识：0未删除 2已删除")
    private String delFlag;


}