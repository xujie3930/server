package com.szmsd.returnex.vo;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: ReturnExpressVO
 * @Description: 退款单列表对象
 * @Author: 11
 * @Date: 2021/3/26 13:41
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ReturnExpressVO", description = "退货单列表详情VO")
public class ReturnExpressListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "退件原始单号 原出库单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "退件可扫描编码")
    private String scanCode;

    @ApiModelProperty(value = "预报单号")
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号")
    private String returnNo;


    @ApiModelProperty(value = "申请处理方式")
    private String processType;
    //@FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @ApiModelProperty(value = "申请处理方式", hidden = true)
    private String processTypeStr;

    public void setProcessType(String processType) {
        this.processType = processType;
        this.processTypeStr = processType;
    }

    public void setProcessTypeStr(String processTypeStr) {
        //empty
    }

    @ApiModelProperty(value = "退件单类型[ 自有库存退件 转运单退件 外部渠道退件]")
    private String returnType;
    //@FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    @ApiModelProperty(value = "退件单类型", hidden = true)
    private String returnTypeStr;

    public void setReturnType(String returnType) {
        this.returnType = returnType;
        this.returnTypeStr = returnType;
    }

    public void setReturnTypeStr(String returnTypeStr) {
        //empty
    }

    @ApiModelProperty(value = "实际处理方式编码", hidden = true)
    private String applyProcessMethod;
    @ApiModelProperty(value = "实际处理方式编码", hidden = true)
    private String applyProcessMethodStr;

    @ApiModelProperty(value = "到仓时间")
    private LocalDateTime arrivalTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "是否逾期")
    private String overdue;
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.YN)
    @ApiModelProperty(value = "是否逾期", hidden = true)
    private String overdueStr;

    public void setOverdue(String overdue) {
        this.overdue = overdue;
        this.overdueStr = overdue;
    }

    public void setOverdueStr(String overdueStr) {
        //empty
    }

    @ApiModelProperty(value = "处理备注")
    private String processRemark;
    @ApiModelProperty(value = "WMS备注")
    private String remark;
    @ApiModelProperty(value = "类型[默认：1：退件预报，2：VMS通知退件]")
    private String returnSource;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_SUB)
    //@FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @ApiModelProperty(value = "退件单来源[默认：1：退件预报2：VMS通知退件]", hidden = true)
    private String returnSourceStr;

    public void setReturnSource(String returnSource) {
        this.returnSource = returnSource;
        this.returnSourceStr = returnSource;
    }

    public void setReturnSourceStr(String returnSourceStr) {
        //empty
    }

    @ApiModelProperty(value = "处理状态编码")
    private String dealStatus;
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @ApiModelProperty(value = "处理状态编码", hidden = true)
    private String dealStatusStr;

    @ApiModelProperty(value = "目的仓库名称")
    private String warehouseCode;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "目的仓库名称", hidden = true)
    private String warehouseCodeStr;

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
        this.warehouseCodeStr = warehouseCode;
    }

    public void setWarehouseCodeStr(String warehouseCodeStr) {
        //empty
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
