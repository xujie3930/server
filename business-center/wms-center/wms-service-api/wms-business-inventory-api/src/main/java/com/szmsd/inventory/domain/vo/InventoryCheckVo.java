package com.szmsd.inventory.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.inventory.domain.InventoryCheckDetails;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "InventoryCheckVo", description = "Inventory盘点表")
public class InventoryCheckVo {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "申请单号")
    private String orderNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "仓库代码")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseCode;

    @ApiModelProperty(value = "库存盘点审批结果")
    private Integer status;

    @ApiModelProperty(value = "审批不通过原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "盘点时间")
    private String checkTime;

    @ApiModelProperty(value = "详情")
    private List<InventoryCheckDetails> list;



}
