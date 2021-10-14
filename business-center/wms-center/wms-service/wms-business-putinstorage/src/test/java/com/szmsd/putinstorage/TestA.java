package com.szmsd.putinstorage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.putinstorage.domain.InboundReceipt;
import com.szmsd.putinstorage.mapper.InboundReceiptMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
}
