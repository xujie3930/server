package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 18:50
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "AddressCommand")
@NoArgsConstructor
@AllArgsConstructor
public class AddressCommand {

    // 名字
    private String name;
    // 手机
    private String phone;
    // 邮件地址
    private String email;
    // 地址1
    private String address1;
    // 地址2
    private String address2;
    // 地址3
    private String address3;
    // 城市
    private String city;
    // 州
    private String province;
    // 邮编
    private String postCode;
    // 国家
    private String country;
}
