package com.szmsd.pack.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.pack.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * <p>
 * package - 交货管理 - 地址信息表
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "package - 交货管理 - 地址信息表", description = "PackageManagement对象")
public class PackageMangAddDTO implements BOConvert {

    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "数据异常")
    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    //@NotBlank(message = "请先登录用户")
    @ApiModelProperty(value = "用户code", required = true)
    @Excel(name = "用户code")
    private String sellerCode;

    @NotBlank(message = "联系人姓名不能为空")
    @ApiModelProperty(value = "联系人姓名", required = true)
    @Excel(name = "联系人姓名")
    private String linkUserName;

    @NotBlank(message = "联系电话不能为空")
    @ApiModelProperty(value = "联系电话", required = true)
    @Excel(name = "联系电话")
    private String linkPhone;

    @NotBlank(message = "省名称不能为空")
    @ApiModelProperty(value = "省 - 名称", required = true)
    @Excel(name = "省 - 名称")
    private String provinceNameZh;

    @ApiModelProperty(value = "省 - 简码", hidden = true)
    @Excel(name = "省 - 简码")
    private String provinceCode;

    @ApiModelProperty(value = "省 - 英文名", hidden = true)
    @Excel(name = "省 - 英文名")
    private String provinceNameEn;

    @NotBlank(message = "市名称不能为空")
    @ApiModelProperty(value = "市 - 名称", required = true)
    @Excel(name = "市 - 名称")
    private String cityNameZh;

    @ApiModelProperty(value = "市 - 简码", hidden = true)
    @Excel(name = "市 - 简码")
    private String cityCode;

    @ApiModelProperty(value = "市 - 英文名", hidden = true)
    @Excel(name = "市 - 英文名")
    private String cityNameEn;

    @NotBlank(message = "区名称不能为空")
    @ApiModelProperty(value = "区 - 名称", required = true)
    @Excel(name = "区 - 名称")
    private String districtNameZh;

    @ApiModelProperty(value = "区 - 简码", hidden = true)
    @Excel(name = "区 - 简码")
    private String districtCode;

    @ApiModelProperty(value = "区 - 英文名", hidden = true)
    @Excel(name = "区 - 英文名")
    private String districtNameEn;

    @ApiModelProperty(value = "具体地址 - 中文名", hidden = true)
    @Excel(name = "具体地址 - 中文名")
    private String addressZh;

    @ApiModelProperty(value = "具体地址 - 英文名", hidden = true)
    @Excel(name = "具体地址 - 英文名")
    private String addressEn;

    @ApiModelProperty(value = "详细地址", required = true)
    private String deliveryAddress;

    @ApiModelProperty(value = "提交时间", hidden = true)
    @Excel(name = "提交时间")
    private LocalDateTime submitTime = LocalDateTime.now();

    @FutureOrPresent(message = "期望收货日期不能为过去时间")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "期望收货日期不能为空")
    @ApiModelProperty(value = "期望收货日期", required = true)
    @Excel(name = "期望收货日期")
    private LocalDate expectedDeliveryTime;

    @ApiModelProperty(value = "揽件数量")
    @Excel(name = "揽件数量")
    private Integer packageNum;

    @ApiModelProperty(value = "货物类型【 0：入库，1：转运】")
    @Excel(name = "货物类型【 0：入库，1：转运】")
    private Integer operationType;

    @ApiModelProperty(value = "备注")
    private String remark;


}
