package com.szmsd.chargerules;

public class TestMath {

    private static boolean isInTheInterval(long current, long min, long max) {
        return Math.max(min, current) == Math.min(current, max);
    }

    public static void main(String[] args) {

        Long current = 12L;
        Long min = 13L;
        Long max = 24L;

        boolean theInterval = isInTheInterval(current,min,max);

        System.out.println(theInterval);

    }
}
