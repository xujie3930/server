package com.szmsd.http.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.BusinessHttpApplication;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.discount.DiscountDetailDto;
import com.szmsd.http.dto.discount.DiscountPage;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.vo.HttpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessHttpApplication.class)
public class IHtpDiscountServiceTest {
    @Autowired
    private RemoteInterfaceService remoteInterfaceService;

    @Autowired
    IHttpDiscountService iHttpDiscountService;

    @Test
    public void autoInsert() {

        DiscountPage discountPage = new DiscountPage();
        discountPage.setId("634e4ca2c2ef85000849903e");
        discountPage.setPageNumber(1);
        discountPage.setPageSize(20);

        R<PageVO<DiscountDetailDto>> pageVOR = iHttpDiscountService.detailResultPage(discountPage);

        PageVO<DiscountDetailDto> pageVO = pageVOR.getData();

        System.out.println(JSON.toJSONString(pageVO));
    }



    @Test
    public void autoInserts() {

        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        int i=1;

        String url = DomainEnum.TJAPIDomain.wrapper("/v1/Bills/ExchangeRate?PageSize=200&PageIndex="+i+"");
        i++;
        httpRequestDto.setUri(url);
        //httpRequestDto.setBody(tpieceDto);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);

    }
}
