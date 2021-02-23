package com.szmsd.common.core.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池 - 运单处理
 */
//@Component
public class ThreadPool2 {

    private static int wokerNum ;
    private static int maxNum ;
    private static int maxQuqueSize;
    private volatile static ThreadPoolExecutor executor;

    public static void setWokerNum(int wokerNum) {
        ThreadPool2.wokerNum = wokerNum;
    }
    public static void setMaxNum(int maxNum) {
        ThreadPool2.maxNum = maxNum;
    }
    public static void setMaxQuqueSize(int maxQuqueSize) {
        ThreadPool2.maxQuqueSize = maxQuqueSize;
    }

    // 获取单例的线程池对象   PriorityBlockingQueue   如需要优先级，请先 FutureTask ，然后实现Comparable接口
    public static ThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (ThreadPool2.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(wokerNum,// 核心线程数
                            maxNum, // 最大线程数
                            60L, // 闲置线程存活时间
                            TimeUnit.MILLISECONDS,// 时间单位
                            new ArrayBlockingQueue<>(maxQuqueSize),// 线程队列
                            Executors.defaultThreadFactory()// 线程工厂
                    );
                }
            }
        }
        return executor;
    }

    public static String getPoolMessage(){
        String result="";
        if(executor!=null){
            int queueSize = executor.getQueue().size();
//            System.err.println("当前排队线程数：" + queueSize);
            int activeCount = executor.getActiveCount();
//            System.err.println("当前活动线程数：" + activeCount);
            long completedTaskCount = executor.getCompletedTaskCount();
//            System.err.println("执行完成线程数：" + completedTaskCount);
            long taskCount = executor.getTaskCount();
//            System.err.println("总线程数：" + taskCount);
            int largestPoolSize = executor.getLargestPoolSize();
//            System.err.println("曾经出现过最大的线程数：" + largestPoolSize);
            int priority = Thread.currentThread().getPriority();
            result+="ThreadPool2\n "+ Thread.currentThread().getName()+"\n 当前排队线程数："+queueSize+"\n 当前活动线程数："+activeCount+"\n 执行完成线程数："+completedTaskCount+"\n 总线程数："+taskCount
                    +"\n 曾经出现过最大的线程数："+largestPoolSize + "\n 当前线程优先级："+priority;
        }
        return result;
    }
}
