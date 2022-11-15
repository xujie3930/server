package com.szmsd.http;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.ShipmentOrderSubmissionParam;
import com.szmsd.http.service.ICarrierService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessHttpApplication.class)
public class ICarrierServiceTest {


    @Autowired
    private ICarrierService iCarrierService;

    @Test
    public void htest(){

        ShipmentOrderSubmissionParam submission = new ShipmentOrderSubmissionParam();
        submission.setReferenceNumber("C23BF4CC453448E39C01D6A068F71D35");

        R r = iCarrierService.submission(submission);

        System.out.println(JSON.toJSONString(r));
    }
}
