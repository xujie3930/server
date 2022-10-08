package com.szmsd.system.config;

import org.apache.commons.codec.binary.Base64;

/**
 * @author jiangjun
 * @version 1.0
 * @description
 * @date 2022/10/08 16:28
 */
public class Base64Utilrs {
    // 加密
    public  String getBase64(String str) {
        String encodeBase64String = Base64.encodeBase64String(str.getBytes());
        return encodeBase64String;
    }

    // 解密
    public  String getFromBase64(String s) {
        byte[] decodeBase64 = Base64.decodeBase64(s);
        s=new String(decodeBase64);
        return s;
    }

    /**
     * 处理身份证号的敏感信息
     * @param codeId
     * @return
     */
    public  String codePwd(String codeId){
        codeId = codeId.substring(0,7) + "********" +codeId.substring(15,codeId.length());
        return codeId;
    }

    public  String PhonoNumber(String phonoNumber){
        phonoNumber = phonoNumber.substring(0,3) + "****" +phonoNumber.substring(7,phonoNumber.length());
        return phonoNumber;
    }

//    public static void main(String[] args) {
//        String a ="110101199003076819";
//        String base64 = getBase64(a);
//        System.out.println(base64);
//        String fromBase64 = getFromBase64(base64);
//        System.out.println(fromBase64);
//    }

}
