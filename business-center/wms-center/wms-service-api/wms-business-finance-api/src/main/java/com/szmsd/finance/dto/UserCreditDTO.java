package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import javax.validation.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Data
@ApiModel(description = "用户授信额度信息")
public class UserCreditDTO {
    @Valid
    @ApiModelProperty(value = "用户授信额度详情信息(类型只能二选一)")
    private List<UserCreditDetailDTO> userCreditDetailList = new ArrayList<>();

    public void setUserCreditDetailList(List<UserCreditDetailDTO> userCreditDetailList) {
        this.userCreditDetailList = userCreditDetailList;
        this.checkCreditList();
    }

    @NotBlank(message = "客户编码不能为空")
    @ApiModelProperty(value = "客户编码", required = true)
    private String cusCode;

    private void checkCreditList() {
        if (CollectionUtils.isNotEmpty(userCreditDetailList)) {
            long creditTypeCount = userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditType).distinct().count();
            AssertUtil.isTrue(creditTypeCount <= 1, "同一用户暂时只支持同一授信类型");
            userCreditDetailList.stream().map(UserCreditDetailDTO::getCreditType).filter(x -> x == CreditConstant.CreditTypeEnum.TIME_LIMIT).findAny().ifPresent(x -> {
                AssertUtil.isTrue(userCreditDetailList.size() <= 1, "选择账期只需要设置账期周期,且只需要一条记录");
            });
            long count = userCreditDetailList.stream().map(UserCreditDetailDTO::getCurrencyCode).distinct().count();
            AssertUtil.isTrue(count == userCreditDetailList.size(), "请检查是否存在相同币别的授信类型");
            AtomicInteger index = new AtomicInteger(1);
            StringBuilder errorMsg = new StringBuilder();
            userCreditDetailList.forEach(x -> {
                CreditConstant.CreditTypeEnum creditType = x.getCreditType();
                switch (creditType) {
                    case QUOTA:
                        this.userCreditDetailList.forEach(userCreditDetailDTO -> {
                            Set<ConstraintViolation<UserCreditDetailDTO>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(userCreditDetailDTO, Quota.class);
                            String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
                            if (StringUtils.isNotBlank(error)) {
                                errorMsg.append(String.format("请检查第%s条数据:%s\r", index, error));
                            }
                        });
                        return;
                    case TIME_LIMIT:
                        this.userCreditDetailList.forEach(userCreditDetailDTO -> {
                            Set<ConstraintViolation<UserCreditDetailDTO>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(userCreditDetailDTO, Interval.class);
                            String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
                            if (StringUtils.isNotBlank(error)) {
                                errorMsg.append(String.format("请检查第%s条数据:%s\r", index, error));
                            }
                        });
                        return;
                    case DEFAULT:
                        AssertUtil.isTrue(false, "类型异常");
                    default:
                }
            });
            if (StringUtils.isNotBlank(errorMsg.toString())) {
                throw new RuntimeException(errorMsg.toString());
            }
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
