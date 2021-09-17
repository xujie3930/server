package com.szmsd.doc.utils;

import com.szmsd.doc.api.AssertUtil400;
import org.springframework.util.Base64Utils;

/**
 * @ClassName: Base64CheckUtils
 * @Description: base64
 * @Author: 11
 * @Date: 2021-09-17 14:33
 */
public final class Base64CheckUtils extends Base64Utils {

    public static byte[] checkAndConvert(String productImageBase64) {
        byte[] bytes = Base64CheckUtils.decodeFromString(productImageBase64);
        AssertUtil400.isTrue(bytes.length >= 10 * 1024, "文件处理");
        return bytes;
    }

}
