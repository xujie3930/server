package com.szmsd.chargerules;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.service.IChaOperationService;
import com.szmsd.chargerules.vo.ChaOperationVO;
import io.swagger.models.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
@RunWith(SpringRunner.class)
@SpringBootTest
public class BusinessChargeRulesApplicationTests {

    @Test
    public void a() {
        ChaOperationVO chaOperationVO = new ChaOperationVO();
        File file = new File("C:\\Users\\11\\Downloads\\" + LocalDate.now() + "\\" + System.currentTimeMillis() + "xlsx");
        EasyExcel.write(file).registerWriteHandler(new SheetWriteHandler() {
            @Override
            public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

            }

            @Override
            public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

            }
        });
    }
    @Resource
    private IChaOperationService iChaOperationService;
    @Test
    public void get(){
        final OperationQueryDTO operationQueryDTO = JSONObject.parseObject("{\"cusCodeList\":\"CNN272\",\"effectiveTime\":\"2021-12-03T17:09:00.867\",\"operationType\":\"Normal\",\"orderType\":\"Shipment\",\"pageNum\":1,\"pageSize\":10,\"warehouseCode\":\"UA\"}", OperationQueryDTO.class);
        ChaOperationVO chaOperationVO = iChaOperationService.queryOperationDetailByRule(operationQueryDTO);


    }
}
