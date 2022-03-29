package com.szmsd.http.dto;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Year;
import java.util.Date;

/**
 * @author : wangshuai
 * @date : 2022-03-24 18:12
 * @description :提货时间信息
 **/
@Data
@Accessors(chain = true)
@ApiModel(value = "PickupDateInfo")
@NoArgsConstructor
@AllArgsConstructor
public class PickupDateInfo {
    /**
     * 开始时间 HHmm
     */
    @NotNull
    private String readyTime;

    /**
     * 结束时间  HHmm
     */
    @NotNull
    private String closeTime;

    /**
     * 提货时间（期望收货日期） yyyyMMdd
     */
    @NotNull
    private String pickupDate;

}
