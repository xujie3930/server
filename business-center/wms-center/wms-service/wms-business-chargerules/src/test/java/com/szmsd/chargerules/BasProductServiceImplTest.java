package com.szmsd.chargerules;

import com.alibaba.fastjson.JSON;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.chargerules.service.IBasProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessChargeRulesApplication.class)
public class BasProductServiceImplTest {

    @Autowired
    private IBasProductService iBasProductService;

    @Test
    public void query(){

        List<String> stringList = new ArrayList<>();
        stringList.add("KSS");
        List<BasProductService> basProductServices = iBasProductService.selectBasProductByCode(stringList);

        System.out.println(JSON.toJSONString(basProductServices));
    }
}
