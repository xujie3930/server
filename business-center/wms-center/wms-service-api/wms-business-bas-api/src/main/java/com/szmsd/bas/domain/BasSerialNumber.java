package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 流水号信息
 * </p>
 *
 * @author gen
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "流水号信息", description = "BaseSerialNumber对象")
@TableName("bas_serial_number")
public class BasSerialNumber extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "业务编码")
    private String code;

    @ApiModelProperty(value = "当前序列")
    private Long currentSequence;

    @ApiModelProperty(value = "步长")
    private Integer step;

    @ApiModelProperty(value = "流水号长度")
    private Integer length;

    @ApiModelProperty(value = "前缀")
    private String prefix;

    @ApiModelProperty(value = "流水号类型")
    private Integer type;

    @ApiModelProperty(value = "模板")
    private String template;

    @ApiModelProperty(value = "周期格式")
    private String cycleFormat;

    @ApiModelProperty(value = "当前周期")
    private String currentCycle;

    @ApiModelProperty(value = "缓存数量")
    private Integer cacheSize;

    @ApiModelProperty(value = "转换器")
    private String converts;
}
