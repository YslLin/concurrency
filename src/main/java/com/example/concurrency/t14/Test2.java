package com.example.concurrency.t14;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 可重入锁
 */
public class Test2 {
    private final Lock rtl = new ReentrantLock();
    int value = 0;

    public int getValue() {
        rtl.lock();
        try {
            return value;
        } finally {
            rtl.unlock();
        }
    }

    public void setValue(int value) {
        rtl.lock();
        try {
            this.value = value;
        } finally {
            rtl.unlock();
        }
    }

    public void addOne() {
        // 获取锁
        rtl.tryLock();
        try {
            // set 与 get 方法内都会 重新获取锁 rtl.lock()
            // 此时如果是 可重入锁 则再次加锁成功
            // 如果不是 可重入锁 则新城阻塞
            setValue(getValue() + 1);
        } finally {
            rtl.unlock();
        }
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(99999);
        Test2 t2 = new Test2();
        for (int i = 0; i < 99999; i++) {
            new Thread(() -> {
                try {
                    t2.addOne();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
            System.out.println("结果：" + t2.getValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 测试得出 synchronized 也是可重入锁
        // 不可重入锁是指 同一个线程无法重复获取自身所持有的锁。也叫 自旋锁
        // 详细介绍：https://www.cnblogs.com/dj3839/p/6580765.html
//        Test2t t2t = new Test2t();
//        t2t.addOne();
//        System.out.println("结果：" + t2t.getValue());
    }

    static class Test2t {
        int value = 0;

        public synchronized int getValue() {
            synchronized (this) {
                return value;
            }
        }

        public synchronized void setValue(int value) {
            synchronized (this) {
                this.value = value;
            }
        }

        public synchronized void addOne() {
            synchronized (this){
                setValue(getValue() + 1);
            }
        }
    }
}
