package com.szmsd.bas.config;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HMacSHA256 {
    private static final Logger logger = LoggerFactory.getLogger(HMacSHA256.class);

    public static String encryptHex(String secret, String message) {
        try {
            byte[] bytes = encrypt(secret, message);
            if (null != bytes)
                return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            logger.error("加密失败，secret: {}, message: {}", secret, message);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String encryptBase64(String secret, String message) {
        try {
            byte[] bytes = encrypt(secret, message);
            if (null != bytes)
                return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            logger.error("加密失败，secret: {}, message: {}", secret, message);
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static byte[] encrypt(String secret, String message) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKey);
            return hmacSHA256.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("加密失败，secret: {}, message: {}", secret, message);
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
