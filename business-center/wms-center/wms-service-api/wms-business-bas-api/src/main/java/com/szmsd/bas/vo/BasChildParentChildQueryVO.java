package com.szmsd.bas.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 字母单的查询条件
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Data
public class BasChildParentChildQueryVO extends QueryDto {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "母单ID")
    private Integer parentId;

    @ApiModelProperty(value = "母单客户代码")
    private String parentSellerCode;

    @ApiModelProperty(value = "申请时间")
    private String applyTime;

    @ApiModelProperty(value = "处理时间")
    private String dealTime;

    @ApiModelProperty(value = "附件URL")
    private String attachment;

    @ApiModelProperty(value = "状态 1审核中 2驳回 3已绑定 4解绑")
    private String state;

    @ApiModelProperty(value = "子母状态 0未关联 1母 2子")
    private String childParentStatus;

    @ApiModelProperty(value = "时间类型 1-申请时间 2-处理时间")
    private String timeType;

    @ApiModelProperty(value = "时间范围")
    private List<Date> timeArrange;
}
