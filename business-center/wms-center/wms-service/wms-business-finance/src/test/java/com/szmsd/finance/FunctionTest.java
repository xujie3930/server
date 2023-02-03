package com.szmsd.finance;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionTest {

    public int compute(int num, Function<Integer,Integer> function){

        return function.apply(num);
    }

    public int compute(int num, BiFunction<Integer,Integer,Integer> function){

        return 0;
        //return function.apply();
    }

    public static void main(String[] args) {

        FunctionTest functionTest = new FunctionTest();
        int num = functionTest.compute(5,item -> item * 4);

        System.out.println(num);

    }
}
