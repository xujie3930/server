package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 出库单明细
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单明细", description = "DelOutboundDetail对象")
public class DelOutboundDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;

    @ApiModelProperty(value = "行号")
    private Long lineNo;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "中文申报品名")
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值")
    private Double declaredValue;

    @ApiModelProperty(value = "产品属性编号")
    private String productAttribute;

    @ApiModelProperty(value = "带电信息编号")
    private String electrifiedMode;

    @ApiModelProperty(value = "电池包装编号")
    private String batteryPackaging;

    @ApiModelProperty(value = "海关编码")
    private String hsCode;

    @ApiModelProperty(value = "箱标")
    private String boxMark;



    @ApiModelProperty(value = "是否品牌")
    private String brandFlag;

    @ApiModelProperty(value = "品牌链接")
    private String brandUrl;


    @ApiModelProperty(value = "箱长")
    private Double boxLength;

    @ApiModelProperty(value = "箱宽")
    private Double boxWidth;

    @ApiModelProperty(value = "箱高")
    private Double boxHeight;

    @ApiModelProperty(value = "箱重量")
    private Double boxWeight;


    @ApiModelProperty(value = "操作类型")
    private String operationType;

}
