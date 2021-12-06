package com.szmsd.chargerules.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "OperationDTO", description = "OperationDTO对象")
public class OperationDTO implements Serializable {
    @ExcelIgnore
    @ApiModelProperty(value = "ID")
    private Long id;
    @ApiModelProperty(value = "ID", hidden = true)
    @ExcelProperty(value = "业务费用顺序",index = 0)
    private Long rowId;

    @NotBlank(message = "仓库不能为空")
    @ExcelProperty(value = "仓库",index = 3)
    @ApiModelProperty(value = "仓库", required = true)
    private String warehouseCode;
    @NotBlank(message = "操作类型不能为空")
    @ApiModelProperty(value = "操作类型", required = true)
    private String operationType;
    @NotBlank(message = "操作类型不能为空")
    @ExcelProperty(value = "操作类型",index = 5)
    @ApiModelProperty(value = "操作类型名称", required = true)
    private String operationTypeName;

    @NotBlank(message = "订单类型不能为空")
    @ApiModelProperty(value = "订单类型", required = true)
    private String orderType;

    @ExcelProperty(value = "订单类型",index = 6)
    @ApiModelProperty(value = "订单类型", hidden = true)
    private String orderTypeName;

    @NotBlank(message = "币别不能为空")
    @ExcelProperty(value = "币别",index = 4)
    @ApiModelProperty(value = "币种编码", required = true)
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "客户类型", hidden = true)
    @ExcelProperty(value = "客户类型")
    private String cusTypeName;

    @ApiModelProperty(value = "客户类型编码", required = true)
    private String cusTypeCode;

    @ApiModelProperty(value = "客户名称 A,B")
    private String cusNameList;

    @ApiModelProperty(value = "客户编码 CNI1,CNI2")
    @ExcelProperty(value = "客户编号",index = 2)
    private String cusCodeList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生效时间")
    @ExcelProperty(value = "生效时间",index = 7)
    private LocalDateTime effectiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "失效时间")
    @ExcelProperty(value = "失效时间",index =8)
    private LocalDateTime expirationTime;
    @ExcelProperty(value = "备注",index = 9)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Valid
    @ApiModelProperty(value = "规则列表")
    private List<ChaOperationDetailsDTO> chaOperationDetailList;

    public boolean verifyData() {
        AssertUtil.isTrue(effectiveTime.compareTo(expirationTime) <= 0, "生效时间不能大于等于失效时间");
        if (CollectionUtils.isNotEmpty(chaOperationDetailList)) {
            // 转运/批量出库单-装箱费/批量出库单-贴标费 同一个仓库 只能存在一条配置
            if (DelOutboundOrderEnum.PACKAGE_TRANSFER.getCode().equals(operationType)
                    || DelOutboundOrderEnum.BATCH_PACKING.getCode().equals(operationType)
                    || DelOutboundOrderEnum.BATCH_LABEL.getCode().equals(operationType)) {
                AssertUtil.isTrue(chaOperationDetailList.size() == 0, operationTypeName + "只能配置一条规则数据");
            }
            AtomicInteger index = new AtomicInteger(1);
            AtomicInteger index2 = new AtomicInteger(1);
            //校验区间是否冲突
            boolean present = chaOperationDetailList.stream().peek(x -> {
                int indexThis = index2.getAndIncrement();
                BigDecimal minimumWeight = x.getMinimumWeight();
                BigDecimal maximumWeight = x.getMaximumWeight();
                AssertUtil.isTrue(minimumWeight.compareTo(maximumWeight) <= 0, String.format("第%s条规则中%s不能小于%s", indexThis, "最大重量", "最小重量"));
            }).sorted(Comparator.comparing(ChaOperationDetailsDTO::getFirstPrice)).reduce((x1, x2) -> {
                int indexThis = index.getAndIncrement();
                //判断是否相交
                //  max(A.left,B.left)<=min(A.right,B.right)
                AssertUtil.isTrue(x1.getMaximumWeight().min(x2.getMaximumWeight()).compareTo(x1.getMinimumWeight().max(x2.getMinimumWeight())) < 0, String.format("第%s条规则与第%s条规则冲突", indexThis, indexThis + 1));
                return x2;
            }).isPresent();

        }
        return true;
    }
}
