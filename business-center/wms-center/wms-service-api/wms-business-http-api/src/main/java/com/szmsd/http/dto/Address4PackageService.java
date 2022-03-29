package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "Address4PackageService")
@NoArgsConstructor
@AllArgsConstructor
public class Address4PackageService {

    /**
     * 名字
     */
    private String name;

    /**
     * 公司名
     */
    private String companyName;


    /**
     * 手机
     */
    private String phone;


    /**
     * 邮件地址
     */
    private String email;


    /**
     * 地址1
     */
    private String address1;


    /**
     * 地址2
     */
    private String address2;


    /**
     * 地址3
     */
    private String address3;

    private String city;

    private String province;

    private String postCode;

    private String country;

}
