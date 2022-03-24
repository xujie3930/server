package com.szmsd.pack.domain;

import java.math.BigDecimal;

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
 * 发货条件表
 * </p>
 *
 * @author admpon
 * @since 2022-03-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "发货条件表", description = "PackageDeliveryConditions对象")
public class PackageDeliveryConditions extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "产品代码")
    @Excel(name = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "仓库代码")
    @Excel(name = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库中文名(冗余字段)")
    @Excel(name = "仓库中文名(冗余字段)")
    private String warehouseNameCn;

    @ApiModelProperty(value = "仓库英文名(冗余字段)")
    @Excel(name = "仓库英文名(冗余字段)")
    private String warehouseNameEn;

    @ApiModelProperty(value = "指令下发节点代码")
    @Excel(name = "指令下发节点代码")
    private String commandNodeCode;

    @ApiModelProperty(value = "指令下发节点名称")
    @Excel(name = "指令下发节点名称")
    private String commandNodeName;

    @ApiModelProperty(value = "包材是否回传（0：否，1：是）")
    @Excel(name = "包材是否回传（0：否，1：是）")
    private String packageReturned;

    @ApiModelProperty(value = "重量是否回传（0：否，1：是）")
    @Excel(name = "重量是否回传（0：否，1：是）")
    private String weightReturned;

    @ApiModelProperty(value = "是否仓库贴标（0：否，1：是）")
    @Excel(name = "是否仓库贴标（0：否，1：是）")
    private String warehouseLabeling;

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private String status;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;


}
