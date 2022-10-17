package com.szmsd.finance.util;

import com.szmsd.finance.vo.helibao.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

public class AES {

    private static final Logger logger = LoggerFactory.getLogger(AES.class);

    public static byte[] encrypt(byte[] data, byte[] key) {
        CheckUtils.notEmpty(data, "data");
        CheckUtils.notEmpty(key, "key");
        if (key.length != 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, seckey);// 初始化
            byte[] result = cipher.doFinal(data);
            return result; // 加密
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        CheckUtils.notEmpty(data, "data");
        CheckUtils.notEmpty(key, "key");
        if (key.length != 16) {
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.AES_ALGORITHM);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, seckey);// 初始化
            byte[] result = cipher.doFinal(data);
            return result; // 加密
        } catch (Exception e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    public static String encryptToBase64(String data, String key) {
        try {
            logger.info("AES加密原文：{}", data);
            byte[] valueByte = encrypt(data.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING), Base64.decode(key.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING)));
            return new String(Base64.encode(valueByte));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encrypt fail!", e);
        }

    }

    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] originalData = Base64.decode(data.getBytes());
            byte[] valueByte = decrypt(originalData, Base64.decode(key.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING)));
            String AES_decrypt = new String(valueByte, ConfigureEncryptAndDecrypt.CHAR_ENCODING);
            logger.info("AES解密原文：{}", AES_decrypt);
            return AES_decrypt;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }


}
