package com.szmsd.http.controller;


import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("httpPricedProductLogController")
public class HttpPricedProductLogController {
    @Autowired
    private RemoteInterfaceService remoteInterfaceService;

    @GetMapping("/pricedProducts/{productCode}")
    public R pricedProductsProductCode(@PathVariable String productCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/pricedProducts/"+productCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }

    @GetMapping("/pricedSheets/{sheetCode}")
    public R pricedSheetsSheetCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/pricedSheets/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }

    @GetMapping("/pricedGrades/{sheetCode}")
    public R pricedGradesSheetCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/pricedGrades/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }


    @GetMapping("/customTemplates/{sheetCode}")
    public R customTemplatesCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/customTemplates/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }


    @GetMapping("/handleFee/{sheetCode}")
    public R handleFeesCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/handleFee/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }

    @GetMapping("/pricedDiscount/{sheetCode}")
    public R pricedDiscountsCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/pricedDiscount/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }

    @GetMapping("/warehouseRent/{sheetCode}")
    public R warehouseRentsCode(@PathVariable String sheetCode) {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.OperationRecord.wrapper("/api/records/warehouseRent/"+sheetCode+"/operationRecords");
        httpRequestDto.setUri(url);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        return R.ok(map4);
    }



}
