package com.szmsd.http.dto;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wangshuai
 * @date : 2022-03-24 18:03
 * @description : 请求提货服务数据
 **/
@Data
@Accessors(chain = true)
@ApiModel(value = "CreatePickupPackageCommand")
@NoArgsConstructor
@AllArgsConstructor
public class CreatePickupPackageCommand {
    /**
     * 流水号(订单号)
     */
    private String referenceNumber;

    private Address pickupAddress;

    private PickupPieces pickupPieces;

    private PickupDateInfo pickupDateInfo;

    /**
     * 系统提货服务
     */
    private String pickupPackageServiceName;

}
