package com.szmsd.delivery.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundDetailDto", description = "DelOutboundDetailDto对象")
public class DelOutboundDetailDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank(message = "SKU不能为空", groups = {ValidationSaveGroup.class, ValidationUpdateGroup.class})
    @ApiModelProperty(value = "SKU")
    private String sku;

    @NotNull(message = "数量不能为空", groups = {ValidationSaveGroup.class, ValidationUpdateGroup.class})
    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;
    @ApiModelProperty(value = "是否需要贴新SKU标签 默认：false",allowableValues = "true,false")
    private Boolean needNewLabel = false;
    @ApiModelProperty(value = "行号")
    private Long lineNo;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "长 - 用于计算不保存")
    private Double length;

    @ApiModelProperty(value = "宽 - 用于计算不保存")
    private Double width;

    @ApiModelProperty(value = "高 - 用于计算不保存")
    private Double height;

    @ApiModelProperty(value = "重量 - 用于计算不保存")
    private Double weight;

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

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "箱长")
    private Double boxLength;

    @ApiModelProperty(value = "箱宽")
    private Double boxWidth;

    @ApiModelProperty(value = "箱高")
    private Double boxHeight;

    @ApiModelProperty(value = "箱重量 ")
    private Double boxWeight;


    @ApiModelProperty(value = "是否品牌")
    private String brandFlag;

    @ApiModelProperty(value = "品牌链接")
    private String brandUrl;

    @ApiModelProperty(value = "sku新标签文件")
    @TableField(exist = false)
    private List<AttachmentDataDTO> skuFile;

    @ApiModelProperty(value = "箱标文件")
    @TableField(exist = false)
    private List<AttachmentDataDTO> boxMarkFile;
}
