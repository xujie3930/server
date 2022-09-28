package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "账单明细统计生成记录表", description = "账单明细统计生成记录表")
@TableName("fss_account_bill_record")
public class AccountBillRecord extends FssBaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "创建时间")
    private Date billStartTime;

    @ApiModelProperty(value = "结束时间")
    private Date billEndTime;

    @ApiModelProperty(value = "状态 0 未处理 1 处理中 2 处理完成")
    private Integer buildStatus;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @ApiModelProperty(value = "删除状态 1 已删除 0 正常")
    private Integer deleted;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "业务ID")
    private String recordId;



}
