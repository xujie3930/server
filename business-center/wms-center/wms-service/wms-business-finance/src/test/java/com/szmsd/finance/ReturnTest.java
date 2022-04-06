package com.szmsd.finance;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.compont.IRemoteApi;
import com.szmsd.finance.dto.RefundReviewDTO;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.service.IDeductionRecordService;
import com.szmsd.finance.service.impl.RefundRequestServiceImpl;
import com.szmsd.finance.vo.CreditUseInfo;
import com.szmsd.finance.vo.RefundRequestListVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: ReturnTest
 * @Description:
 * @Author: 11
 * @Date: 2022-01-25 11:50
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessFinanceApplication.class)
public class ReturnTest {

    @Resource
    private RefundRequestServiceImpl refundRequestService;
    @Resource
    private IRemoteApi remoteApi;
    @Resource
    private IAccountBalanceService iAccountBalanceService;
    @Resource
    private IDeductionRecordService iDeductionRecordService;

    @Test
    public void auditTest() {
        AtomicInteger index = new AtomicInteger(1);
        List<RefundRequestListVO> rows;
        while (CollectionUtils.isNotEmpty(rows = getRow())) {
            long start = System.currentTimeMillis();
            List<String> collect = rows.stream().map(RefundRequestListVO::getId).map(Object::toString).collect(Collectors.toList());
            RefundReviewDTO refundReviewDTO = new RefundReviewDTO();
            refundReviewDTO.setIdList(collect);
            refundReviewDTO.setReviewRemark("通过");
            refundReviewDTO.setStatus(RefundStatusEnum.COMPLETE);
            HttpResponse authorization = HttpRequest.put("https://web-client.dmfulfillment.cn/api/wms-finance/refundRequest/approve").header("Authorization", "Bearer 9980ad62-9bcf-4b68-97c8-749488415889")
                    .body(JSONObject.toJSONString(refundReviewDTO)).executeAsync();
            log.info("次数：{} | 耗时：{} s,请求id:{},响应：{}", index, System.currentTimeMillis() - start, collect, authorization.body());
        }

    }

    public List<RefundRequestListVO> getRow() {
        List<RefundRequestListVO> rows = new ArrayList<>();
        HttpResponse httpResponse = HttpRequest.get("https://web-client.dmfulfillment.cn/api/wms-finance/refundRequest/page?auditStatus=1&treatmentPropertiesCode=025001&pageNum=1&pageSize=1")
                .header("Authorization", "Bearer 9980ad62-9bcf-4b68-97c8-749488415889").executeAsync();
        if (httpResponse.isOk()) {
            String body = httpResponse.body();
            TableDataInfo tableDataInfo = JSONObject.parseObject(body, TableDataInfo.class);
            String s = tableDataInfo.getRows().toString();
            rows = JSONObject.parseArray(s, RefundRequestListVO.class);
        }
        return rows;
    }

    @Test
    public void auditTest1() throws Exception {
        int countThread = 10;
        List<String> idList = new ArrayList<>();
        long id = 63022L;
        for (int i = 10; i > 0; i--) {
            idList.add(id-- + "");
        }

        CyclicBarrier cyclicBarrier = new CyclicBarrier(countThread);
        CountDownLatch countDownLatch = new CountDownLatch(countThread);
        for (int i = 0; i < countThread; i++) {
            new Thread(() -> {
                try {
                    log.info("执行：---------");
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


                refundRequestService.afterApprove(RefundStatusEnum.COMPLETE, idList);
                countDownLatch.countDown();
                log.info("执1111行：---------" + countDownLatch.getCount());
            }).start();

        }
        while (countDownLatch.getCount() != 0) {

        }
        int parties = cyclicBarrier.getParties();
        System.out.println(parties);


    }

    @Test
    public void query_user_amount() {
        Map<String, CreditUseInfo> cny = iDeductionRecordService.queryTimeCreditUse("", Arrays.asList("CNY"), Arrays.asList(CreditConstant.CreditBillStatusEnum.CHECKED));
        System.out.println(cny);
    }

    @Test
    public void query_user_amount_bill() {
        Long cny = 0L;
        while ((cny = iDeductionRecordService.moveInvalidCreditBill()) > 0) {
            log.info("sync info {}", cny);
        }
    }
}
