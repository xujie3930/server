package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.inventory.enums.PurchaseEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


/**
 * <p>
 * 采购单日志
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "PurchaseLog对象")
public class PurchaseLogAddDTO {

    @NotNull
    @ApiModelProperty(value = "关联的id集合")
    @Excel(name = "关联的id集合")
    private Integer associationId;

    @ApiModelProperty(value = "类型（0:创建采购单，1：创建入库单）")
    @Excel(name = "类型（0:创建采购单，1：创建入库单）")
    private PurchaseEnum type;

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehouseNo;
    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号")
    private List<String> orderNoList;

    public void setOrderNoList(List<String> orderNoList) {
        this.orderNoList = orderNoList;
        Optional.ofNullable(orderNoList).filter(CollectionUtils::isNotEmpty)
                .ifPresent(x -> orderNo = String.join(",", x));
    }

    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号")
    private String orderNo;
    @ApiModelProperty(value = "日志内容")
    @Excel(name = "日志内容")
    private String logDetails;
    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;
    @ApiModelProperty(value = "创建者")
    private String createByName;

    /**
     * 设置日志
     *
     * @param
     */
    public void formatLogDetails() {
        String formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
        switch (type) {
            case PURCHASE_ORDER:
                logDetails = type.generateLog(createByName, formatTime, orderNo, purchaseNo);
                break;
            case WAREHOUSING_LIST:
                logDetails = type.generateLog(createByName, formatTime, purchaseNo, warehouseNo);
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
