package com.szmsd.finance;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.RefundReviewDTO;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.vo.RefundRequestListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @ClassName: ReturnTest
 * @Description:
 * @Author: 11
 * @Date: 2022-01-25 11:50
 */
@Slf4j
public class ReturnTest {

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
}
