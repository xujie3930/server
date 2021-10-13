package com.szmsd.doc.api;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;

/**
 * @ClassName: RUtils
 * @Description:
 * @Author: 11
 * @Date: 2021-10-13 14:54
 */
public final class RUtils {

    public static <T> T getDataAndException(R<T> r) {
        if (null != r) {
            if (Constants.SUCCESS == r.getCode()) {
                return r.getData();
            } else {
                // 抛出接口返回的异常信息
                throw new CommonException("400", r.getMsg());
            }
        }
        throw new CommonException("400", "网络异常");
    }

}
