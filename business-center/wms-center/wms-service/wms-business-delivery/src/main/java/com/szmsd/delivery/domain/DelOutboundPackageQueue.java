package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 出库单核重记录
    * </p>
*
* @author asd
* @since 2021-04-02
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="出库单核重记录", description="DelOutboundPackageQueue对象")
public class DelOutboundPackageQueue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
            @TableId(value = "id", type = IdType.UUID)
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

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String orderNo;

    @ApiModelProperty(value = "处理状态")
    @Excel(name = "处理状态")
    private String state;


}
