package com.szmsd.doc.api.warehouse.req;

import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel( description = "入库列表查询条件")
public class InboundReceiptQueryReq extends QueryDto {

    @ApiModelProperty(value = "入库单号",example = "1")
    private String warehouseNo;

    @ApiModelProperty(value = "入库单号")
    private List<String> warehouseNoList;

    @ApiModelProperty(value = "采购单")
    private String orderNo;

    @ApiModelProperty(value = "采购单")
    private List<String> orderNoList;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库方式")
    private String orderType;

    @ApiModelProperty(value = "客户编码",hidden = true)
    private String cusCode;

    @ApiModelProperty(value = "状态0已取消，1初始，2已提审，3审核通过，-3审核失败，4处理中，5已完成")
    private String status;

    @ApiModelProperty(value = "状态集合")
    private List<String> statusList;

    @ApiModelProperty(value = "送货方式编码")
    private String deliveryWayCode;

    @ApiModelProperty(value = "创建时间（CR）",hidden = true)
    private InboundReceiptQueryDTO.TimeType timeType= InboundReceiptQueryDTO.TimeType.CR;

    @ApiModelProperty(value = "开始时间yyyy-MM-dd")
    private String startTime;

    @ApiModelProperty(value = "结束时间yyyy-MM-dd")
    private String endTime;

    @ApiModelProperty(value = "入库方式编码")
    private String warehouseMethodCode;

    @Getter
    @AllArgsConstructor
    public enum TimeType {
        /** 入库单创建时间 **/
        CR("t.create_time"),
        ;
        private String field;
    }

}