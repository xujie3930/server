package com.szmsd.returnex;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;

/**
 * @ClassName: com.szmsd.returnex.TestC
 * @Description:
 * @Author: 11
 * @Date: 2021/4/1 18:50
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestC {

    @Resource
    private IReturnExpressService returnExpressService;

    @Test
    public void te() {
        returnExpressService.expiredUnprocessedForecastOrder();
    }

    @Resource
    private ConfigStatus configStatus;
    @Test
    public void te2() {
        System.out.println(JSONObject.toJSONString(configStatus));
    }

    @Test
    public void testExport(){
        File file = new File("D:\\workspace\\java\\ck1\\serve\\business-center\\wms-center\\wms-service\\wms-business-returnex\\src\\main\\"+System.currentTimeMillis()+".xlsx");
        EasyExcel.write(file, ReturnExpressServiceAddDTO.class).sheet().doWrite(new ArrayList());

    }
}
