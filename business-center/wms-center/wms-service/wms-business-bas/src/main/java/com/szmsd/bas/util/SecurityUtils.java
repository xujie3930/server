package com.szmsd.bas.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class SecurityUtils {

    /**
     * 前端密码解密
     *
     * @param password 前端原始密码
     * @return 用户输入密码
     */
    public static String decodePassword(String password) {
        return password;
    }

    /**
     * 密码加密
     *
     * @param password 用户输入密码
     * @return 存入数据库中的密码
     */
    public static String encryptPassword(String password, String passwordSalt) {
        return DigestUtils.md5Hex(password + passwordSalt);
    }

    /**
     * 获取盐
     *
     * @return 盐
     */
    public static String getSalt() {
        return getSalt(6);
    }

    /**
     * 获取盐
     *
     * @return 盐
     */
    public static String getSalt(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static void main(String[] args) {
        System.out.println(encryptPassword("123456", "kaEXaL"));
    }
}
