package com.example.concurrency.Chapter3.t32;

import java.nio.channels.ServerSocketChannel;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Balking 模式 线程安全的单例模式
 * 对象初始化一次后就不再执行 new 了
 * 线程安全的单例模式本质上也是单次初始化
 */
public class Singleton {
    private Singleton() {
        System.out.println("实例对象");
    }

    /**
     * 方案一：使用 synchronized 互斥锁
     * 实现虽然功能上没有问题，但是性能很差，因为互斥锁 synchronized 将 getInstance 方法串行化了
     */
//    private static Singleton singleton;
//    public synchronized static Singleton getInstance() {
//        if (singleton == null) {
//            singleton = new Singleton();
//        }
//        return singleton;
//    }

    /**
     * 方案二：双重检查方案
     * 一旦对象被创建之后，就不会再执行 synchronized 相关代码了，也就是说此时 getInstance 方法是无锁的，从而解决了性能问题
     * 双重检查中的第一次检查，完全是出于对性能的考量：避免执行加锁操作。而加锁之后的二次检查，则是出于对安全性负责。
     * 用 volatile 禁止编译优化，防止编译优化后cpu缓存ok但内存还未ok时，线程切换了，导致其它线程 singleton != null，直接返回可能触发空指针异常
     */
    private static  Singleton singleton;
    public static Singleton getInstance(){
        if(singleton == null) {
            synchronized (Singleton.class){
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

    public static void main(String[] args) {
        Date start = new Date();
        ExecutorService exe = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch countDownLatch = new CountDownLatch(10000000);
        for (int i = 0; i < 10000000; i++) {
            exe.submit(() -> {
                try {
                    Singleton.getInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        exe.shutdown();
        try {
            countDownLatch.await();
            System.out.println("结束：" + (new Date().getTime() - start.getTime()) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
