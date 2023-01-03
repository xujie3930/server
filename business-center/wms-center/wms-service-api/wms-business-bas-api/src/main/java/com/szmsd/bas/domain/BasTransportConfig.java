package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
@ApiModel(value = "", description = "BasTransportConfig运输方式对象")
public class BasTransportConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "运输方式code")
    private String transportCode;

    @ApiModelProperty(value = "运输方式中文名称")
    private String transportName;

    @ApiModelProperty(value = "运输方式英文名称")
    private String transportNameEn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "发货服务code")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;


    @ApiModelProperty(value = "修改人编号")
    private String updateBy;


    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    private String delFlag;


}