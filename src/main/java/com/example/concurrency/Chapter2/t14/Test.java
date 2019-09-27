package com.example.concurrency.Chapter2.t14;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock 锁
 * 解决互斥同步问题
 */
public class Test {
    private final Lock rtl = new ReentrantLock();
    int value = 0;
    public void addOne(){
        rtl.lock();
        try {
            value += 1;
        } finally {
            rtl.unlock();
        }
    }

    public static void main(String[] args) {
        long timer = System.currentTimeMillis(); //  获取当前系统时间(毫秒)
        CountDownLatch countDownLatch = new CountDownLatch(99999);
        Test t = new Test();
        for (int i = 0; i < 99999; i++) {
            new Thread(()->{
                try {
                    t.addOne();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
            System.out.println("结果："+t.value);
            System.out.println("延迟时间：" + (System.currentTimeMillis() - timer) + "毫秒");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
