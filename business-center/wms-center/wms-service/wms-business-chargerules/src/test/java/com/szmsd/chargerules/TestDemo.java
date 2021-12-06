package com.szmsd.chargerules;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.cache.Ehcache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.chargerules.config.AnalysisListenerAbstract;
import com.szmsd.chargerules.config.LocalDateTimeConvert;
import com.szmsd.chargerules.dto.ChaOperationDetailsDTO;
import com.szmsd.chargerules.dto.OperationDTO;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TestDemo
 * @Description:
 * @Author: 11
 * @Date: 2021-12-05 13:35
 */
public class TestDemo {
    @Test
    public void get(){
        File file = new File("C:\\Users\\11\\Downloads\\ChargeOperation.xlsx");
        ExcelReader excelReader = EasyExcel.read(file).readCache(new Ehcache(5)).build();
        AnalysisListenerAbstract<OperationDTO> listener0 = new AnalysisListenerAbstract<>();
        ReadSheet readSheet = EasyExcel.readSheet(0).head(OperationDTO.class)
                .registerConverter(new LocalDateTimeConvert()).registerReadListener(listener0).build();
        AnalysisListenerAbstract<ChaOperationDetailsDTO> listener1 = new AnalysisListenerAbstract<>();

        ReadSheet readSheet1 = EasyExcel.readSheet(1).head(ChaOperationDetailsDTO.class).registerReadListener(listener1).build();

        excelReader.read(readSheet, readSheet1);
        excelReader.finish();
        List<OperationDTO> operationDTOList = listener0.getResultList();
        List<ChaOperationDetailsDTO> chaOperationDetailsDTOList = listener1.getResultList();

        System.out.println(JSONObject.toJSONString(operationDTOList));
        System.out.println(JSONObject.toJSONString(chaOperationDetailsDTOList));
    }
}
