package com.szmsd.delivery.util;

import com.szmsd.common.core.utils.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;

public class TestDate {

    @Test
    public void testDate() {

        String format = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ssXXX", Locale.getDefault());

        System.out.println(format);
    }
}
