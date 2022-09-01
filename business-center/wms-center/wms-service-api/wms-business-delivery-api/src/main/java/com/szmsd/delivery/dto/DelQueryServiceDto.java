package com.szmsd.delivery.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
* <p>
    * 查件服务
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="查件服务", description="DelQueryService对象")
public class DelQueryServiceDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "物流服务编号")
    private String shipmentRule;

    @ApiModelProperty(value = "物流服务名称")
    @Excel(name = "物流服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;


    @ApiModelProperty(value = "业务经理-id")
    @Excel(name = "业务经理")
    private String serviceManager;

    @ApiModelProperty(value = "业务经理-编码")
    @Excel(name = "业务经理")
    private String serviceManagerName;

    @ApiModelProperty(value = "业务经理-名称")
    @Excel(name = "业务经理")
    private String serviceManagerNickName;


    @ApiModelProperty(value = "客服-id")
    private String serviceStaff;

    @ApiModelProperty(value = "客服姓名-编码")
    private String serviceStaffName;

    @ApiModelProperty(value = "客服姓名")
    @Excel(name = "客服姓名")
    private String serviceStaffNickName;

    @ApiModelProperty(value = "查件原因")
    @Excel(name = "查件原因")
    private String reason;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String country;

    @ApiModelProperty(value = "refno")
    @Excel(name = "refno")
    private String refNo;


    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "状态名称")
    @Excel(name = "状态名称")
    private String stateName;



    @ApiModelProperty(value = "创建日期查询条件")
    private String[] createTimes;

    @ApiModelProperty(value = "反馈时间查询条件")
    private String[] feedbacks;

    @ApiModelProperty(value = "IDS")
    private Long[] ids;

    @ApiModelProperty(value = "查件反馈明细集合")
    private List<DelQueryServiceFeedback> delQueryServiceFeedbackList;

    private String queryNoOne;

    @ApiModelProperty(value = "发货天数")
    private Long delDays;

    @ApiModelProperty(value = "轨迹停留天数")
    private Long trackingDays;

    @ApiModelProperty(value = "查件标识(0是红色,1是绿色)")
    private Long checkFlag;

    @ApiModelProperty(value = "允查发货天数")
    private Long queryseShipmentDays;

    @ApiModelProperty(value = "允查轨迹停留天数")
    private Long querysetrackStayDays;



}
