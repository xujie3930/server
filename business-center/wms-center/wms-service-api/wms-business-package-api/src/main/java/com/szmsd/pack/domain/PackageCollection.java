package com.szmsd.pack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


/**
 * <p>
 * package - 交货管理 - 揽收
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "package - 交货管理 - 揽收", description = "PackageCollection对象")
public class PackageCollection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private Integer delFlag;

    @ApiModelProperty(value = "揽收人")
    @Excel(name = "揽收人")
    private String collectionName;

    @ApiModelProperty(value = "联系方式")
    @Excel(name = "联系方式")
    private String collectionPhone;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String collectionCountry;

    @ApiModelProperty(value = "州省")
    @Excel(name = "州省")
    private String collectionProvince;

    @ApiModelProperty(value = "城市")
    @Excel(name = "城市")
    private String collectionCity;

    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String collectionPostCode;

    @ApiModelProperty(value = "揽收地址")
    @Excel(name = "揽收地址")
    private String collectionAddress;

    @ApiModelProperty(value = "揽收至仓库")
    @Excel(name = "揽收至仓库")
    private String collectionToWarehouse;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "收货人")
    @Excel(name = "收货人")
    private String receiverName;

    @ApiModelProperty(value = "联系方式")
    @Excel(name = "联系方式")
    private String receiverPhone;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String receiverCountry;

    @ApiModelProperty(value = "州省")
    @Excel(name = "州省")
    private String receiverProvince;

    @ApiModelProperty(value = "城市")
    @Excel(name = "城市")
    private String receiverCity;

    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String receiverPostCode;

    @ApiModelProperty(value = "收货地址")
    @Excel(name = "收货地址")
    private String receiverAddress;

    @ApiModelProperty(value = "揽收服务编码")
    @Excel(name = "揽收服务编码")
    private String collectionServiceCode;

    @ApiModelProperty(value = "揽收服务名称")
    @Excel(name = "揽收服务名称")
    private String collectionServiceName;

    @ApiModelProperty(value = "揽收计划")
    @Excel(name = "揽收计划")
    private String collectionPlan;

    @ApiModelProperty(value = "处理方式")
    @Excel(name = "处理方式")
    private String handleMode;

    @ApiModelProperty(value = "揽收日期")
    private Date collectionDate;

    @ApiModelProperty(value = "箱数")
    private Integer boxNumber;

    @ApiModelProperty(value = "揽收单号")
    private String collectionNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "合计数量")
    private Integer totalQty;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "货物名称")
    @TableField(exist = false)
    private String skuNames;

    @ApiModelProperty(value = "揽收货物")
    @TableField(exist = false)
    private List<PackageCollectionDetail> detailList;
}
