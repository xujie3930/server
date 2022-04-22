package com.szmsd.returnex;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.ExcelUtils;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.dto.ReturnExpressAddDTO;
import com.szmsd.returnex.dto.ReturnExpressClientImportBO;
import com.szmsd.returnex.dto.ReturnExpressServiceAddDTO;
import com.szmsd.returnex.service.IReturnExpressService;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName: com.szmsd.returnex.TestC
 * @Description:
 * @Author: 11
 * @Date: 2021/4/1 18:50
 */
//@SpringBootTest
//@RunWith(SpringRunner.class)
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
        MultipartFile multipartFile = new MockMultipartFile("file", fileInputStream);
        List<String> strings = iReturnExpressService.importByTemplateClient(multipartFile);
        System.out.println(strings);


    }

    static final List<ReturnExpressClientImportBO> importBOList;

    static {
        int count = 41;
        importBOList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ReturnExpressClientImportBO returnExpressClientImportBO = new ReturnExpressClientImportBO();
            returnExpressClientImportBO.setExpectedNo(i + "");
            importBOList.add(returnExpressClientImportBO);
        }
    }

    @SneakyThrows
    @Test
    public void test_a() {
//        List<ReturnExpressClientImportBO> importBOList = new ArrayList<>();
        int count = 5;
        int size = importBOList.size();
        int segments = size / count;
        segments = size % count == 0 ? segments : segments + 1;
        List<CompletableFuture<List<String>>> errorMsgList = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            System.out.println(" ===========================分段=========================== " + i);
            List<ReturnExpressClientImportBO> importBOS;
            if (i == segments - 1) {
                importBOS = importBOList.subList(count * i, size);
            } else {
                importBOS = importBOList.subList(count * i, count * (i + 1));
            }
            errorMsgList.add(executeReal(importBOS));
        }

        CompletableFuture.allOf(errorMsgList.toArray(new CompletableFuture[0])).get();
        List<String> resultMsg = new ArrayList<>(importBOList.size());
        errorMsgList.forEach(x -> {
            try {
                resultMsg.addAll(x.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    private List<String> execute(List<ReturnExpressClientImportBO> importBOList) throws ExecutionException, InterruptedException {
        int count = 20;
        int size = importBOList.size();
        int segments = size / count;
        segments = size % count == 0 ? segments : segments + 1;
        List<CompletableFuture<List<String>>> errorMsgList = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            List<ReturnExpressClientImportBO> importBOS;
            if (i == segments - 1) {
                importBOS = importBOList.subList(count * i, size);
            } else {
                importBOS = importBOList.subList(count * i, count * (i + 1));
            }
            errorMsgList.add(executeReal(importBOS));
        }
        CompletableFuture.allOf(errorMsgList.toArray(new CompletableFuture[0])).get();
        List<String> resultMsg = new ArrayList<>(importBOList.size());
        errorMsgList.forEach(x -> {
            try {
                resultMsg.addAll(x.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return resultMsg;
    }

    private CompletableFuture<List<String>> executeReal(List<ReturnExpressClientImportBO> importBOList) {
        SecurityContext context = SecurityContextHolder.getContext();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            SecurityContextHolder.setContext(context);
            List<String> errorMsgList = new ArrayList<>();
            importBOList.forEach(x -> {
                System.out.println("exe: " + x);
            });
            return errorMsgList;
        });
    }

    @SneakyThrows
    @Test
    public void test_gen_returnEx_data() {
        HttpRequest post = HttpUtil.createPost("http://183.3.221.136:22221/api/wms-delivery/api/outbound/page")
                .header(Header.AUTHORIZATION, "Bearer 8cff2d86-e3a4-4ef3-b4a8-2384f5863e0e");
        HttpResponse execute = post.body("{\"state\":\"COMPLETED\",\"pageNum\":1,\"pageSize\":100}").execute();
        String body = execute.body();
        System.out.println(body);
        TableDataInfo tableDataInfo = JSONObject.parseObject(body, TableDataInfo.class);
        List rows = tableDataInfo.getRows();
        List<DelOutboundListVO> delOutboundListVOS = JSONObject.parseArray(JSONObject.parse(JSONObject.toJSONString(rows)).toString(), DelOutboundListVO.class);
        Date date = new Date();
        long time = System.currentTimeMillis();
        System.out.println("[size]:" + delOutboundListVOS.size());
        CountDownLatch countDownLatch = new CountDownLatch(delOutboundListVOS.size());
        delOutboundListVOS.forEach(x -> {
            new Thread(() -> {
                String orderNo = x.getOrderNo();
                HttpRequest post2 = HttpUtil.createPost("http://183.3.221.136:22221/api/wms-business-returnex/server/return/express/add")
                        .header(Header.AUTHORIZATION, "Bearer 8cff2d86-e3a4-4ef3-b4a8-2384f5863e0e");
                String param = "{\"returnSource\":\"068003\",\"returnSourceStr\":\"OMS退件预报\",\"returnNo\":\"14\",\"fromOrderNo\":\"CKCNI54921110100000001\",\"scanCode\":\"DM211101113024\",\"processRemark\":\"14\",\"remark\":\"\",\"arrivalTime\":\"2022-04-22 09:17:24\",\"expireTime\":\"2022-04-22 09:17:25\",\"sellerCode\":\"CNI549\",\"opType\":\"add\",\"status\":1}";
                ReturnExpressServiceAddDTO returnExpressAddDTO = JSONObject.parseObject(param, ReturnExpressServiceAddDTO.class);
                returnExpressAddDTO.setFromOrderNo(orderNo);
                returnExpressAddDTO.setArrivalTime(date);
                long l = System.currentTimeMillis();

                returnExpressAddDTO.setReturnNo(UUID.randomUUID().toString());
                returnExpressAddDTO.setScanCode(x.getTrackingNo());
                returnExpressAddDTO.setExpireTime(date);
                String s = JSONObject.toJSONString(returnExpressAddDTO);
                String replace = s.replaceAll(time + "", "\"2022-04-22 09:17:25\"");
                HttpRequest body1 = post2.body(replace);
                System.out.println("[orderNo]---请求 " + replace);
                HttpResponse add = post2.execute();
                System.out.println("[orderNo]---响应 " + add.body());
                System.out.println("===============================================================");
                countDownLatch.countDown();
            }).start();
        });
        countDownLatch.await();
        System.out.println("执行完成");

    }

}
