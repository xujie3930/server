package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 出库单临时第三方任务表
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单临时第三方任务表", description = "DelOutboundThirdParty对象")
public class DelOutboundThirdParty extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态")
    private String state;

    @ApiModelProperty(value = "操作类型，开始处理：Processing，已发货：Shipped，已取消：Canceled")
    @Excel(name = "操作类型，开始处理：Processing，已发货：Shipped，已取消：Canceled")
    private String operationType;

    @ApiModelProperty(value = "处理次数")
    private Integer handleSize;

    @ApiModelProperty(value = "下一次处理时间")
    private Date nextHandleTime;

    @ApiModelProperty(value = "uuid")
    private String uuid;
}
