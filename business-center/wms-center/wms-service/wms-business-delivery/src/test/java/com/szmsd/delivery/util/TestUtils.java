package com.szmsd.delivery.util;

import com.alibaba.fastjson.JSON;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-29 10:34
 */
public class TestUtils {

    @Test
    public void test_subtractDate() throws ParseException {
        String format = "yyyy-MM-dd";
        Date beginDate = DateUtils.parseDate("2021-04-19", format);
        Date endDate = DateUtils.parseDate("2021-04-25", format);

        long diff = Utils.subtractDate(endDate, beginDate);

        System.out.println(diff);

        for (int i = 0; i <= diff; i++) {
            System.out.println(i);
        }
    }

    @Test
    public void test_fullDiff() throws ParseException {
        String format = "yyyy-MM-dd";
        Date beginDate = DateUtils.parseDate("2021-04-19", format);
        Date endDate = DateUtils.parseDate("2021-04-25", format);

        List<DelOutboundReportListVO> list = new ArrayList<>();
        list.add(new DelOutboundReportListVO("2021-04-22", 4));
        list.add(new DelOutboundReportListVO("2021-04-24", 4));

        long diff = Utils.subtractDate(endDate, beginDate) + 1;

        String currentDate;
        for (int i = 0; i < list.size(); i++) {
            currentDate = DateFormatUtils.format(beginDate, format);
            if (!currentDate.equals(list.get(i).getDate())) {
                list.add(i, new DelOutboundReportListVO(currentDate, 0));
            }
            beginDate = Utils.addDays(beginDate, 1);
        }

        int d2 = (int) (diff - list.size());
        if (d2 > 0) {

            for (int i = 0; i < d2; i++) {
                currentDate = DateFormatUtils.format(beginDate, format);
                list.add(new DelOutboundReportListVO(currentDate, 0));
                beginDate = Utils.addDays(beginDate, 1);
            }
        }

        System.out.println(JSON.toJSONString(list));
    }

}
