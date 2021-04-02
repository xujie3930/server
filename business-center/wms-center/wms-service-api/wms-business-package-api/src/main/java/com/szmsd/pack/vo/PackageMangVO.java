package com.szmsd.pack.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * <p>
 * package - 交货管理 - 地址信息表
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "揽件列表查询条件", description = "揽件列表查询条件")
public class PackageMangVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除", hidden = true)
    private Integer delFlag = 0;

    @Excel(name = "单号")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Excel(name = "客户代码")
    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "创建时间-开始")
    private LocalDate submitTimeStart;

    @ApiModelProperty(value = "创建时间-结束")
    private LocalDate submitTimeEnd;

    @ApiModelProperty(value = "是否导出【 0：未导出，1：已导出】", example = "0")
    private Integer exportType;

    @Excel(name = "地址")
    @ApiModelProperty(value = "详细地址", required = true)
    private String deliveryAddress;

    @ApiModelProperty(value = "联系人姓名")
    @Excel(name = "联系人")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "期望收货日期")
    @Excel(name = "收货时间")
    private LocalDate expectedDeliveryTime;

    @ApiModelProperty(value = "揽件数量")
    @Excel(name = "收货数量")
    private Integer packageNum;

    @ApiModelProperty(value = "货物类型【 0：入库，1：转运】")
    @Excel(name = "货物类型")
    private Integer operationType;

    private String operationTypeStr;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;


}
