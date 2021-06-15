package com.szmsd.putinstorage.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.putinstorage.annotation.InboundReceiptLog;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.enums.InboundReceiptRecordEnum;
import com.szmsd.putinstorage.service.InboundReceiptRecordAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class InboundReceiptRecordAspect {

    @Resource
    private InboundReceiptRecordAsyncService inboundReceiptRecordAsyncService;

    @Pointcut("@annotation(com.szmsd.putinstorage.annotation.InboundReceiptLog)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    private void before(JoinPoint point) {
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    private void after(JoinPoint point, Object result) {
        if (result == null) {
            return;
        }
        R r = (R) result;
        if (r.getCode() != 200) {
            return;
        }

        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        InboundReceiptLog recordAnnotation = targetMethod.getDeclaredAnnotation(InboundReceiptLog.class);
        InboundReceiptRecordEnum recordEnum = recordAnnotation.record();
        Object arg = point.getArgs()[0];

        InboundReceiptRecord inboundReceiptRecord = new InboundReceiptRecord();
        inboundReceiptRecord.setType(recordEnum.getType());
        inboundReceiptRecord.setRemark(getStr(recordEnum.get(null)));

        if (arg instanceof String) {
            inboundReceiptRecord.setWarehouseNo(arg.toString());
            inboundReceiptRecordAsyncService.saveRecord(inboundReceiptRecord);
            return;
        }

        if (arg instanceof List) {
            inboundReceiptRecord.setWarehouseNo(String.join(",", getStr(arg)));
            inboundReceiptRecordAsyncService.saveRecord(inboundReceiptRecord);
            return;
        }

        List<String> reflexFills = recordEnum.getContentFill();
        Map<String, Object> sourceMap = JSONObject.parseObject(JSON.toJSONString(arg));

        List<String> params = null;
        if (CollectionUtils.isNotEmpty(reflexFills)) {
            params = new ArrayList<>();
            for (String reflexFill : reflexFills) {
                params.add(sourceMap.get(reflexFill) + "");
            }
        }
        if (StringUtils.isNotEmpty(recordEnum.getCreateBy())) {
            inboundReceiptRecord.setCreateBy(getStr(sourceMap.get(recordEnum.getCreateBy())));
        }
        if (StringUtils.isNotEmpty(recordEnum.getCreateByName())) {
            inboundReceiptRecord.setCreateByName(getStr(sourceMap.get(recordEnum.getCreateByName())));
        }
        if (StringUtils.isNotEmpty(recordEnum.getWarehouseNo())) {
            inboundReceiptRecord.setWarehouseNo(getStr(sourceMap.get(recordEnum.getWarehouseNo())));
        }
        if (StringUtils.isNotEmpty(recordEnum.getSku())) {
            inboundReceiptRecord.setSku(getStr(sourceMap.get(recordEnum.getSku())));
        }
        String record = recordEnum.get(params);
        inboundReceiptRecord.setRemark(record);
        inboundReceiptRecordAsyncService.saveRecord(inboundReceiptRecord);
    }

    @SuppressWarnings("unchecked")
    public String getStr(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof List) {
            try {
                return String.join(",", (List<String>) val);
            } catch (ClassCastException ignored) {
            }
        }
        return val.toString();
    }

}
