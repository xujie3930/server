package com.szmsd.pack.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "揽件列表查询条件", description = "揽件列表查询条件")
public class PackageManagementConfigVo {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "联系人姓名")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "揽收地址")
    private String deliveryAddress;

    @ApiModelProperty(value = "揽收周期")
    private String weekName;

}
