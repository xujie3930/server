package com.szmsd.doc.api;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;

import java.util.function.Supplier;

/**
 * @ClassName: AssertUtil400
 * @Description:
 * @Author: 11
 * @Date: 2021-09-16 19:45
 */
public final class AssertUtil400 extends AssertUtil {
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new CommonException("400", message);
        }
    }
}
