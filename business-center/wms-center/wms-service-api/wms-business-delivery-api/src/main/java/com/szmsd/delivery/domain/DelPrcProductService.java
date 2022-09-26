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
    * PRC-产品服务
    * </p>
*
* @author admin
* @since 2022-09-26
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="PRC-产品服务", description="DelPrcProductService对象")
public class DelPrcProductService extends BaseEntity {

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
    private Integer version;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    private String productCode;

    @ApiModelProperty(value = "服务渠道名")
    @Excel(name = "服务渠道名")
    private String logisticsRouteId;

    @ApiModelProperty(value = "是否可查询")
    @Excel(name = "是否可查询")
    private Boolean showFlag;

    @ApiModelProperty(value = "是否可下单")
    @Excel(name = "是否可下单")
    private Boolean inServiceFlag;

    @ApiModelProperty(value = "仓库核重之后重新获取挂号")
    @Excel(name = "仓库核重之后重新获取挂号")
    private Boolean againTrackingNoFlag;


}
