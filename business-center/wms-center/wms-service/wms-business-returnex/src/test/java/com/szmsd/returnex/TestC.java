package com.szmsd.returnex;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

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
    public void testExport() {
        File file = new File("D:\\workspace\\java\\ck1\\serve\\business-center\\wms-center\\wms-service\\wms-business-returnex\\src\\main\\" + System.currentTimeMillis() + ".xlsx");
        EasyExcel.write(file, ReturnExpressServiceAddDTO.class).sheet().doWrite(new ArrayList());

    }

    @Resource
    private IReturnExpressService iReturnExpressService;

    @SneakyThrows
    @Test
    public void testImport() {
        File file = new File("D:\\workspace\\java\\ck1\\serve\\business-center\\wms-center\\wms-service\\wms-business-returnex\\src\\main\\resources\\template\\退货处理模板.xlsx");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",fileInputStream);
        List<String> strings = iReturnExpressService.importByTemplateClient(multipartFile);
        System.out.println(strings);


    }
}
