package com.szmsd.http.service;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.BusinessHttpApplication;
import com.szmsd.http.dto.discount.DiscountDetailDto;
import com.szmsd.http.dto.discount.DiscountPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessHttpApplication.class)
public class IHtpDiscountServiceTest {


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
}
