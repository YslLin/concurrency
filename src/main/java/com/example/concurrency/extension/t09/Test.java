package com.example.concurrency.extension.t09;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试 Tread.stop() 是否会释放 隐式锁与显示锁
 */
public class Test {
    private int i = 1;

    private synchronized int get() {
        return i;
    }

    private synchronized void add() {
        System.out.println("add s");
        Utils.sleep(3);
        i++;
        System.out.println("add e");
    }

    public static void main(String[] args) {
        Test t = new Test();
//        t.exeSync();
//        t.exeLock();
        t.exeSL();
    }

    /**
     * 测试 synchronized 是否释放锁。
     * 结论 t1.stop(); 会释放 隐式锁
     */
    void exeSync() {
        Test test = new Test();
        Thread t1 = new Thread(() -> {
            test.add();
        });
        Thread t2 = new Thread(() -> {
            System.out.println(test.get());
        });
        t1.start();
        t2.start();
        Utils.sleep(1);
        t1.stop();
    }

    private final Lock rtl = new ReentrantLock();

    private int getLock() {
        rtl.lock();
        try {
            return i;
        } finally {
            rtl.unlock();
        }
    }

    private void addLock() {
        rtl.lock();
        try {
            System.out.println("add s");
            Utils.sleep(3);
            i++;
            System.out.println("add e");
        } finally {
            rtl.unlock();
        }
    }

    /**
     * 测试 Lock 是否释放锁。
     * 结论 t1.stop(); 会释放 显式锁
     */
    void exeLock() {
        Test test = new Test();
        Thread t1 = new Thread(() -> {
            test.addLock();
        });
        Thread t2 = new Thread(() -> {
            System.out.println(test.getLock());
        });
        t1.start();
        t2.start();
        Utils.sleep(1);
        t1.stop();
    }

    /**
     * 测试 synchronized 与 Lock 锁是否会互斥。
     * 结论：两种锁的方式不会互斥
     */
    void exeSL() {
        Test test = new Test();
        Thread t1 = new Thread(() -> {
            test.addLock();
        });
        Thread t2 = new Thread(() -> {
            System.out.println(test.get());
        });
        t1.start();
        t2.start();
    }
}
