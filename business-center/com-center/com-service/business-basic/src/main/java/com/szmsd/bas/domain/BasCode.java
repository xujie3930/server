package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
* @author lufei
* @date 2020-06-29 09:38
* @desc
*/
@Data
@Accessors(chain = true)
@TableName(value = "bas_code_config")
public class BasCode {

    @ApiModelProperty(value = "流水号")
    private String id;

    @ApiModelProperty(value = "应用编号")
    private String appId;

    @ApiModelProperty(value = "流水号编码")
    private String code;

    @ApiModelProperty(value = "流水号名称")
    private String sequenceName;

    @ApiModelProperty(value = "多语言关联编号")
    private String i18nId;

    @ApiModelProperty(value = "前缀")
    private String prefix;

    @ApiModelProperty(value = "长度")
    private Integer length;

    @ApiModelProperty(value = "最小值")
    private Integer minValue;

    @ApiModelProperty(value = "最大值")
    private Long maxValue;

    @ApiModelProperty(value = "增量步长")
    private Integer incrementBy;

    @ApiModelProperty(value = "循环标志")
    private String cycleFlag;

    @ApiModelProperty(value = "排序标志")
    private String orderFlag;

    @ApiModelProperty(value = "最新流水号")
    private Long lastNumber;

    @ApiModelProperty(value = "状态：0停用，1使用")
    private Integer status;

    @ApiModelProperty(value = "单据类型")
    private String bizType;

    @ApiModelProperty(value = "后缀")
    private String suffix;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "时间戳")
    private Date timeStamp;

    @ApiModelProperty(value = "是否使用时间戳：0否 1是")
    private Integer isTimeStamp;

    //todo 待优化方案
    @ApiModelProperty(value = "时间格式(新增后尽量不修改，会造成数据混乱)")
    private String patter;

    @ApiModelProperty(value = "最后的时间")
    private String lastPatter;
}
