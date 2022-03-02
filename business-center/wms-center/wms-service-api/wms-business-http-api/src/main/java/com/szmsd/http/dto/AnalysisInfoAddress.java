package com.szmsd.http.dto;

import lombok.Data;

@Data
public class AnalysisInfoAddress {

    private String ToNode;
    private String CountryName;
    private String CountryCode;
    private String City;
    private String Province;
    private String Postcode;
    private String Street1;
    private String Street2;
    private String Street3;
    private String Recipient;
    private String Tel;
    private String Email;
}
