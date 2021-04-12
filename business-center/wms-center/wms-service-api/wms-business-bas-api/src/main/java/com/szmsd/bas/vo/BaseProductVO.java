package com.szmsd.bas.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BaseProductVO {
    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "中文申报品名")
    private String productNameChinese;
    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    @TableField("`code`")
    private String code;

    @ApiModelProperty(value = "仓库测量重量g")
    private Double weight;

    @ApiModelProperty(value = "仓库测量长 cm")
    private Double length;

    @ApiModelProperty(value = "仓库测量宽 cm")
    private Double width;

    @ApiModelProperty(value = "仓库测量高 cm")
    private Double height;

    @ApiModelProperty(value = "仓库测量体积 cm3")
    private BigDecimal volume;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "绑定专属包材产品名")
    @Excel(name = "绑定专属包材产品名")
    private String bindCodeName;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentFileDTO> documentsFiles;
}
