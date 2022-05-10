package com.szmsd.returnex.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ReturnExpressVO
 * @Description: 退款单列表对象
 * @Author: 11
 * @Date: 2021/3/26 13:41
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ReturnExpressVO", description = "退货单详情VO")
public class ReturnExpressVO {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;
    @ApiModelProperty(value = "sku")
    private String sku;
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "退件原始单号 原出库单号")
    @Excel(name = "退件原始单号 原出库单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "退件可扫描编码")
    @Excel(name = "退件可扫描编码")
    private String scanCode;

    @ApiModelProperty(value = "预报单号")
    @Excel(name = "预报单号")
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号")
    @Excel(name = "VMS处理单号")
    private String returnNo;

    @ApiModelProperty(value = "申请处理方式")
    @Excel(name = "申请处理方式")
    private String processType;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @ApiModelProperty(value = "申请处理方式")
    @Excel(name = "申请处理方式")
    private String processTypeStr;

    public void setProcessType(String processType) {
        this.processType = processType;
        this.processTypeStr = processType;
    }

    public void setProcessTypeStr(String processTypeStr) {
        //empty
    }

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String returnType;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String returnTypeStr;

    public void setReturnType(String returnType) {
        this.returnType = returnType;
        this.returnTypeStr = returnType;
    }

    public void setReturnTypeStr(String returnTypeStr) {
        //empty
    }

    @ApiModelProperty(value = "退件目标仓库编码")
    @Excel(name = "退件目标仓库编码")
    private String warehouseCode;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "退件目标仓库编码")
    @Excel(name = "退件目标仓库编码")
    private String warehouseCodeStr;

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
        this.warehouseCodeStr = warehouseCode;
    }

    public void setWarehouseCodeStr(String warehouseCodeStr) {
        //empty
    }

    @ApiModelProperty(value = "退货渠道", example = "客户自选")
    @Excel(name = "退货渠道")
    private String returnChannel;

    @ApiModelProperty(value = "申请处理方式编码")
    @Excel(name = "申请处理方式编码")
    private String applyProcessMethod;
    @ApiModelProperty(value = "申请处理方式编码")
    @Excel(name = "申请处理方式编码")
    private String applyProcessMethodStr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "到仓时间")
    @Excel(name = "到仓时间")
    private LocalDateTime arrivalTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    @Excel(name = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "是否逾期")
    @Excel(name = "是否逾期")
    private String overdue;

    @ApiModelProperty(value = "处理备注")
    @Excel(name = "处理备注")
    private String processRemark;
    @ApiModelProperty(value = "仓库处理备注")
    @Excel(name = "仓库处理备注")
    private String remark;
    @ApiModelProperty(value = "退件单来源[默认：1：申请退件]")
    @Excel(name = "退件单来源[默认：1：申请退件]")
    private String returnSource;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    //@FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @ApiModelProperty(value = "退件单来源[默认：1：申请退件]")
    @Excel(name = "退件单来源[默认：1：申请退件]")
    private String returnSourceStr;

    public void setReturnSource(String returnSource) {
        this.returnSource = returnSource;
        this.returnSourceStr = returnSource;
    }

    public void setReturnSourceStr(String returnSourceStr) {
        //empty
    }


    @ApiModelProperty(value = "处理状态编码")
    @Excel(name = "处理状态编码")
    private String dealStatus;
    @ApiModelProperty(value = "处理状态编码")
    @Excel(name = "处理状态编码")
    private String dealStatusStr;

    @ApiModelProperty(value = "商品列表")
    List<ReturnExpressGoodVO> goodList;

    /**---ADD----*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "过期时间")
    @Excel(name = "过期时间")
    private Date expireTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    private Date processTime;

    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String customerRemark;

    @ApiModelProperty(value = "新出库单号")
    @Excel(name = "新出库单号")
    private String fromOrderNoNew;

    @ApiModelProperty(value = "新物流跟踪号")
    @Excel(name = "新物流跟踪号")
    private String scanCodeNew;
    @ApiModelProperty(value = "refNo")
    private String refNo;
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
