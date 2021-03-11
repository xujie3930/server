package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.api.controller.DefaultTargetType;
import com.szmsd.http.domain.HtpTransaction;
import com.szmsd.http.service.IHtpTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


/**
 * <p>
 * http事务处理表 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
@Api(tags = {"事务处理"})
@ApiSort(800000)
@RestController
@RequestMapping("/htp-transaction")
public class HtpTransactionController extends BaseController {

    @Resource
    private IHtpTransactionService htpTransactionService;
    @Autowired
    @Qualifier("HttpRestTemplate")
    private RestTemplate restTemplate;

    @PreAuthorize("@ss.hasPermi('HtpTransaction:HtpTransaction:add')")
    @Log(title = "http事务处理表模块", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @ApiOperation(value = "事务处理 - 创建事务", position = 100)
    @ApiImplicitParam(name = "dto", value = "HtpTransaction", dataType = "HtpTransaction")
    public R<Integer> create(@RequestBody HtpTransaction htpTransaction) {
        return toOk(htpTransactionService.insertHtpTransaction(htpTransaction));
    }

    @PostMapping("/worker")
    @ApiOperation(value = "事务处理 - 创建事务", position = 200)
    @ApiImplicitParam(name = "dto", value = "HtpTransaction", dataType = "HtpTransaction")
    public R<Integer> worker(@RequestBody HtpTransaction htpTransaction) {

        HttpMethod hm = this.getHttpMethod("POST");
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Content-Type", "application/json;charset=utf-8");
        HttpEntity<?> httpEntity = new HttpEntity<>("{\"invoiceNo\": \"CK0001\",\"invoiceType\": \"outboundTransactionHandler\"}", headers);
        String requestClass = "com.szmsd.http.dto.CreateShipmentRequestDto";
        try {
            Class<?> forName = Class.forName(requestClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ResponseEntity<R<?>> responseEntity = this.restTemplate.exchange("http://wms-business-delivery/htp-transaction-handler/get", hm, httpEntity, new DefaultTargetType<R<?>>() {
        }.getClassType());
        R<?> resultJson = responseEntity.getBody();
        System.out.println(resultJson);
        if (null != resultJson) {
            // return JSON.toJSONString(resultJson);
        }
        //


        HttpEntity<?> httpEntity2 = new HttpEntity<>("{\"invoiceNo\": \"CK0001\",\"invoiceType\": \"outboundTransactionHandler\"}", headers);
        ResponseEntity<R<?>> exchange = this.restTemplate.exchange("http://wms-business-delivery/htp-transaction-handler/callback", hm, httpEntity2, new DefaultTargetType<R<?>>() {
        }.getClassType());
        R<?> body = exchange.getBody();
        System.out.println(body);

        // return toOk(htpTransactionService.insertHtpTransaction(htpTransaction));
        return R.ok();
    }

    private HttpMethod getHttpMethod(String httpMethod) {
        HttpMethod hm = HttpMethod.GET;
        if (HttpMethod.POST.name().equals(httpMethod)) {
            hm = HttpMethod.POST;
        }
        return hm;
    }
}
