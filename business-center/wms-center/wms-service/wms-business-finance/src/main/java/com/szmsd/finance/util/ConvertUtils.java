package com.szmsd.finance.util;


import java.text.DecimalFormat;

public abstract class ConvertUtils {

    private static final DecimalFormat simpleFormat = new DecimalFormat("####");
    public static String toHex(byte input[]){
        if(input == null) {
            return null;
        }
        StringBuffer output = new StringBuffer(input.length * 2);
        for(int i = 0; i < input.length; i++){
            int current = input[i] & 0xff;
            if(current < 16) {
                output.append("0");
            }
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }

    private ConvertUtils(){}
}
