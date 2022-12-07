package com.szmsd.delivery;

public class SplitTest {

    public static void main(String[] args) {

        String spec = "abc*abc*a";

        String s[] = spec.split("\\*");

        System.out.println(s[0]);
    }
}
