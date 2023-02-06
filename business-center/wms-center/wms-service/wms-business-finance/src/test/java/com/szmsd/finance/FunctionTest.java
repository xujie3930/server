package com.szmsd.finance;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionTest {

    public int compute(int num, Function<Integer,Integer> function){

        return function.apply(num);
    }

    public int compute(int num,Function<Integer,Integer> f1,Function<Integer,Integer> f2){

        return f1.compose(f2).apply(num);
    }

    public int andThen(int num,Function<Integer,Integer> f1,Function<Integer,Integer> f2){

        return f1.andThen(f2).apply(num);
    }

    public int compute(int num, BiFunction<Integer,Integer,Integer> function){

        return 0;
        //return function.apply();
    }


    public static void main(String[] args) {

        FunctionTest functionTest = new FunctionTest();
        int num = functionTest.compute(5,item -> item * 4);
        System.out.println(num);

        int numcom = functionTest.compute(2,item -> item * 2,item -> item  + 5);
        System.out.println(numcom);

        int andThen = functionTest.andThen(2,item -> item * 2,item -> item  + 5);
        System.out.println(andThen);

    }
}
