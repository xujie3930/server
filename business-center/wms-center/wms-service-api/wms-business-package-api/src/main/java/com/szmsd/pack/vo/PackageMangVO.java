package com.szmsd.pack.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value = "揽件列表查询条件", description = "揽件列表查询条件")
public class PackageMangVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除", hidden = true)
    private Integer delFlag = 0;

    @Excel(name = "单号", width = 20)
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    //@Excel(name = "创建时间")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @Excel(name = "客户代码")
    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "是否导出【 0：未导出，1：已导出】", example = "0")
    private Integer exportType;

    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.ADDRESS_MANAGEMENT)
    @ApiModelProperty(value = "是否导出【 0：未导出，1：已导出】", example = "0")
    private String exportTypeStr;

    public void setExportType(Integer exportType) {
        this.exportType = exportType;
        if (null != exportType) {
            if (exportType == 0) {
                exportTypeStr = "未导出";
            } else {
                exportTypeStr = "已导出";
            }
        }
    }

    @Excel(name = "地址", width = 32)
    @ApiModelProperty(value = "详细地址", required = true)
    private String deliveryAddress;

    @ApiModelProperty(value = "联系人姓名")
    @Excel(name = "联系人")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String linkPhone;
    @Excel(name = "期望收货日期")
    @ApiModelProperty(value = "期望收货日期")
    private LocalDate expectedDeliveryTime;
    //@Excel(name = "收货时间")
    private LocalDate deliveryTime;

    @ApiModelProperty(value = "揽件数量")
    @Excel(name = "收货数量")
    private Integer packageNum;

    @ApiModelProperty(value = "实际揽收数量")
    //@Excel(name = "实际揽收数量")
    private Integer receivePackageNum;

    @ApiModelProperty(value = "司机姓名")
    @Excel(name = "司机姓名")
    private String driverName;

    @ApiModelProperty(value = "司机号码")
    @Excel(name = "司机号码")
    private String driverPhone;

    @ApiModelProperty(value = "货物类型【 0：入库，1：转运】")
    private Integer operationType;

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
        if (null != operationType) {
            if (operationType == 0) {
                operationTypeStr = "入库";
            } else {
                operationTypeStr = "转运";
            }
        }
    }

    @ApiModelProperty(value = "货物类型【 0：入库，1：转运】")
    private String operationTypeStr;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "省 - 名称")
    private String provinceNameZh;

    @ApiModelProperty(value = "省 - 简码", hidden = true)
    private String provinceCode;

    @ApiModelProperty(value = "省 - 英文名", hidden = true)
    private String provinceNameEn;

    @ApiModelProperty(value = "市 - 名称")
    private String cityNameZh;

    @ApiModelProperty(value = "市 - 简码", hidden = true)
    private String cityCode;

    @ApiModelProperty(value = "市 - 英文名", hidden = true)
    private String cityNameEn;

    @ApiModelProperty(value = "区 - 名称")
    private String districtNameZh;

    @ApiModelProperty(value = "区 - 简码", hidden = true)
    private String districtCode;

    @ApiModelProperty(value = "区 - 英文名", hidden = true)
    private String districtNameEn;

    @ApiModelProperty(value = "具体地址 - 中文名")
    private String addressZh;

    @ApiModelProperty(value = "选中的地址")
    private String showChoose;

    public void setShowChoose() {
        this.showChoose = String.join(" ", provinceNameZh, cityNameZh, districtNameZh);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
