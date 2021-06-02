package com.cm.fm.mall.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 * 使用：ThreadUtil.getThreadPool.submit(new Runnable(){ //任务});
 */
public class ThreadUtil {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //线程池核心线程数
    private static int coreThreadSize = CPU_COUNT + 1;
    //最大线程数量，超过此值时，后续线程会被阻塞
    private static int maxThreadSize = CPU_COUNT * 2 + 1;
    //超过核心线程数的非核心线程最大存活时间，闲置时间超过此值，线程会被回收
    private static long keepAliveTime = 10L;
    //线程池等待队列长度
    private static int queueSize = 5;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            coreThreadSize,maxThreadSize,keepAliveTime,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(queueSize));

    /* 如果需要用到容量为1的线程池，应该使用SingleThreadPool */
    private static ExecutorService singlePool = Executors.newSingleThreadExecutor();

    public static ExecutorService getSinglePool(){
        return singlePool;
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public static void setThreadPool(ThreadPoolExecutor threadPool) {
        ThreadUtil.threadPool = threadPool;
    }
}
