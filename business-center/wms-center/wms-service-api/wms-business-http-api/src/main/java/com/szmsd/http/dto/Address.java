package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "Address")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @ApiModelProperty
    private String street1;

    @ApiModelProperty
    private String street2;

    @ApiModelProperty
    private String street3;

    @ApiModelProperty
    private String postCode;

    @ApiModelProperty
    private String city;

    @ApiModelProperty
    private String province;

    @ApiModelProperty
    private String country;

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

    public Address(String receiverAddress, String s, String s1, String receiverPostCode, String receiverCity, String receiverProvince, CountryInfo countryInfo) {
        this.street1= receiverAddress;
        this.street2= s;
        this.street3= s1;
        this.postCode= receiverPostCode;
        this.city= receiverCity;
        this.province= receiverProvince;
        this.country = countryInfo;
    }

    public Address(String receiverAddress, String s, String s1, String receiverPostCode, String receiverCity, String receiverProvince) {
        this.street1= receiverAddress;
        this.street1= s;
        this.street1= s1;
        this.street1= receiverPostCode;
        this.street1= receiverCity;
        this.street1= receiverProvince;
    }
}
