package com.szmsd.finance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Disguiser {
    private static final Logger logger = LoggerFactory.getLogger(Disguiser.class);

    public static final String ENCODE = "UTF-8";

    public static String apiDisguise(String message) {
        return apiDisguise(message, ENCODE, ConfigureEncryptAndDecrypt.SHA_256_ALGORITHM);
    }

    public static String apiDisguise(String message, String encoding, String algName) {
        message = message.trim();
        byte value[];
        try {
            value = message.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            value = message.getBytes();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(algName);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return ConvertUtils.toHex(md.digest(value));
    }


}
