package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * <p>
 * 车次表
 * </p>
 *
 * @author ziling
 * @since 2020-11-18
 */
@TableName("bas_train_number")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasTrainNumber对象", description = "车次表")
public class BasTrainNumber {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "车次号")
    @Excel(name = "车次号")
    private String trainNumber;

    @ApiModelProperty(value = "车牌号")
    @Excel(name = "车牌号")
    private String licensePlate;

    @ApiModelProperty(value = "路由代码")
    @Excel(name = "路由代码")
    private String routeCode;

    @ApiModelProperty(value = "路由名称")
    @Excel(name = "路由名称")
    private String routeName;

    @ApiModelProperty(value = "发车时间")
    @Excel(name = "发车时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startCarTime;

    @ApiModelProperty(value = "发车标识")
    @Excel(name = "发车标识")
    private String startCarIden;

    @ApiModelProperty(value = "实际发车时间")
    @Excel(name = "实际发车时间")
    private Date actualStartCarTime;

    @ApiModelProperty(value = "到达标识")
    @Excel(name = "到达标识")
    private String arriveIden;

    @ApiModelProperty(value = "到达时间")
    @Excel(name = "到达时间")
    private Date arriveTime;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createBy;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateByName;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建网点编号")
    @Excel(name = "创建网点编号")
    private String createSiteCode;

    @ApiModelProperty(value = "创建网点")
    @Excel(name = "创建网点")
    private String createSiteName;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建开始时间")
    private String createStartTime;

    @TableField(exist = false)
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建结束时间")
    private String createEndTime;
}
