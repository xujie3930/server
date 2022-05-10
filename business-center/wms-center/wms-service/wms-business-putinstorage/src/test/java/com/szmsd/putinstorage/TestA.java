package com.szmsd.putinstorage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.domain.InboundReceiptDetail;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import com.szmsd.putinstorage.service.IInboundReceiptDetailService;
import com.szmsd.putinstorage.service.IInboundReceiptService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: TestA
 * @Description:
 * @Author: 11
 * @Date: 2021-10-13 16:14
 */
@SpringBootTest(classes = BusinessPutinstorageApplication.class)
@RunWith(SpringRunner.class)
public class TestA {
    @Resource
    private InboundReceiptMapper inboundReceiptMapper;
    @Resource
    private IInboundReceiptService iInboundReceiptService;
    @Resource
    private IInboundReceiptDetailService  inboundReceiptDetailService;
    @Test
    public void a() {
        String warehouseNo = "1";
        ArrayList<String> deliveryNoList = new ArrayList<>();
        deliveryNoList.add("xxx");
        deliveryNoList.add("xxx");
        LambdaQueryWrapper<InboundReceipt> in = Wrappers.<InboundReceipt>lambdaQuery().and(x -> x.ne(InboundReceipt::getWarehouseNo, warehouseNo));
        deliveryNoList.forEach(deliveryNo -> in.or(x -> x.like(InboundReceipt::getTrackingNumber, deliveryNoList)));
//        System.out.println(in.getSqlSelect());
        List<InboundReceipt> inboundReceipts = inboundReceiptMapper.selectList(in);

    }

    @SneakyThrows
    @Test
    public void a1() {
        String s = "TEST";
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int j = 0; j < 100; j++) {
            int finalJ = j;
            new Thread(() -> {
                ArrayList<InboundReceipt> inboundReceipts = new ArrayList<>();
                ArrayList<InboundReceiptDetail> inboundReceiptsE = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    String s1 = s + "-" + finalJ + "-" + i;
                    InboundReceipt inboundReceipt = new InboundReceipt();
                    inboundReceipt.setCusCode("CNID73").setWarehouseNo(s1).setOrderNo(s1).setWarehouseCode("TEST");
                    inboundReceipts.add(inboundReceipt);
                    InboundReceiptDetail inboundReceiptDetail = new InboundReceiptDetail();
                    inboundReceiptDetail.setWarehouseNo(s1);
                    inboundReceiptsE.add(inboundReceiptDetail);
                }
                iInboundReceiptService.saveBatch(inboundReceipts);
                inboundReceiptDetailService.saveBatch(inboundReceiptsE);
                System.out.println("0000000000000000000="+countDownLatch.getCount());
                countDownLatch.countDown();
            }).start();

        }
        countDownLatch.await();

    }
}
