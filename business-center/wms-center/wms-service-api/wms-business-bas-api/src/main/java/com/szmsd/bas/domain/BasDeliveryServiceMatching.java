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


/**
* <p>
    * 发货服务匹配
    * </p>
*
* @author Administrator
* @since 2022-05-16
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="发货服务匹配", description="BasDeliveryServiceMatching对象")
public class BasDeliveryServiceMatching extends BaseEntity {

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

    @ApiModelProperty(value = "sku")
    @Excel(name = "sku")
    private String sku;

    @ApiModelProperty(value = "目的国家编码")
    @Excel(name = "目的国家编码")
    private String countryCode;

    @ApiModelProperty(value = "目的国家名称")
    @Excel(name = "目的国家名称")
    private String country;

    @ApiModelProperty(value = "买家物流选择")
    @Excel(name = "买家物流选择")
    private String buyerService;

    @ApiModelProperty(value = "起始重量 g")
    @Excel(name = "起始重量 g")
    private Double startWeight;

    @ApiModelProperty(value = "结束重量 g")
    @Excel(name = "结束重量 g")
    private Double endWeight;

    @ApiModelProperty(value = "尺寸(cm)")
    @Excel(name = "尺寸(cm)")
    private Double size;

    @ApiModelProperty(value = "体积(cm3)")
    @Excel(name = "体积(cm3)")
    private Double volume;

    @ApiModelProperty(value = "长+（宽+高）*2的值")
    @Excel(name = "长+（宽+高）*2的值")
    private Double perimeter;

    @ApiModelProperty(value = "长+宽+高 的值")
    @Excel(name = "长+宽+高 的值")
    private Double lwhSum;

    @ApiModelProperty(value = "仓库代码")
    @Excel(name = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    @Excel(name = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "发货服务名称")
    @Excel(name = "发货服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称")
    private String warehouseName;


}
