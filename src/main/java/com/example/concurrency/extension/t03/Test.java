package com.example.concurrency.extension.t03;

import com.example.concurrency.utils.Utils;

/**
 * 这个受保护的资源就是静态变量 value，两个锁分别是 this 和 SafeCalc.class。
 * 因此这两个临界区没有互斥关系，临界区 addOne() 对 value 的修改对临界区 get() 也没有保证可见性
 * 线程 t1 在执行 addOne() 方法期间，线程 t2 执行 get() 方法是没有互斥性的
 */
public class Test {

    static long value = 0L;

    synchronized long get() {
        return value;
    }

    synchronized static void addOne() {
        for (int i = 0; i < 10000; i++) {
            value += 1;
            Utils.sleep(1);
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        Thread t1 = new Thread(() -> {
            Test.addOne();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(test.get());
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            System.out.println(test.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
