package com.example.concurrency.t21;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 原子化的对象引用类型
 * AtomicReference、AtomicStampedReference、AtomicMarkableReference
 */
public class AtomicQuote {

    public static void main(String[] args) {
        AtomicQuote quote = new AtomicQuote();
//        quote.exeAtomicReference();
//        quote.exeAtomicStampedReferenceAPI();
//        quote.exeStampedABA();
        quote.exeMarkableABA();
    }

    /**
     * 原子引用类 AtomicReference 并发测试
     */
    void exeAtomicReference() {
        AtomicReference<Integer> reference = new AtomicReference<>(0);

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                Integer old;
                do {
                    old = reference.get();
                } while (!reference.compareAndSet(old, old + 1));
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                Integer old;
                do {
                    old = reference.get();
                } while (!reference.compareAndSet(old, old + 1));
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(reference.get());
            }
        }).start();
    }

    /**
     * 带版本的原子引用类 AtomicStampedReference 并发测试
     */
    void exeAtomicStampedReference() {
        // 带版本号的 原子引用类 ，版本号防止出现 ABA 问题，要保证版本号的唯一性 (初始值, 初始版本号)
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(0, 0);

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                Integer old;
                int[] stamp = new int[0];
                do {
                    old = stampedReference.get(stamp);
                } while (!stampedReference.compareAndSet(old, old + 1, stamp[0], stamp[0] + 1));
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                Integer old;
                int[] stamp = new int[0];
                do {
                    old = stampedReference.get(stamp);
                } while (!stampedReference.compareAndSet(old, old + 1, stamp[0], stamp[0] + 1));
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                System.out.println(stampedReference.get(new int[0]));
            }
        }).start();
    }

    /**
     * 带版本的原子引用类 AtomicStampedReference 并发测试
     */
    void exeAtomicStampedReferenceAPI() {
        // 带版本号的 原子引用类 ，版本号防止出现 ABA 问题，要保证版本号的唯一性 (初始值, 初始版本号)
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(1, 1);

        Integer old;
        int[] stamp = new int[]{0};
        do {
            old = stampedReference.get(stamp);
        } while (!stampedReference.compareAndSet(old, old + 1, stamp[0], stamp[0] + 1));

        System.out.println(old);
        System.out.println(stamp[0]);
        System.out.println(stampedReference.get(stamp));
        System.out.println(stampedReference.getStamp());
        System.out.println(stampedReference.getReference());
    }

    /**
     * 测试 ABA 问题
     * 如果不是使用 版本号的方式，线程1与线程2都对成功 true
     * 使用 stamp 版本号的方式，线程1执行成功，线程2因为线程1执行后更新了版本号，所以线程2执行失败 false
     */
    void exeStampedABA() {
        // (初始引用，初始版本)
        AtomicStampedReference<Integer> stampedReference = new AtomicStampedReference<>(100, 1);
        Thread t1 = new Thread(() -> {
            int stamp = stampedReference.getStamp();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean r1 = stampedReference.compareAndSet(100, 101, stamp, stamp + 1);
            stamp = stampedReference.getStamp();
            boolean r2 = stampedReference.compareAndSet(101, 100, stamp, stamp + 1);
            System.out.println(r1);
            System.out.println(r2);
        });

        Thread t2 = new Thread(() -> {
            int stamp = stampedReference.getStamp();
            System.out.println(stampedReference.getStamp());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(stampedReference.getStamp());
            boolean r = stampedReference.compareAndSet(100, 101, stamp, stamp + 1);
            System.out.println(":" + r);
        });

        t1.start();
        t2.start();
    }

    /**
     * 测试 ABA 问题
     * AtomicMarkableReference 与 AtomicStampedReference 的区别
     * AtomicMarkableReference可以理解为上面AtomicStampedReference的简化版
     * 就是不关心修改过几次，仅仅关心是否修改过。
     * 因此变量mark是boolean类型，仅记录值是否有过修改。
     * TODO 具体使用场景未确定 没用搞懂它的意义
     */
    void exeMarkableABA() {
        // (初始引用，初始标记)
        AtomicMarkableReference<Integer> stampedReference = new AtomicMarkableReference<>(100, false);
        Thread t1 = new Thread(() -> {
            boolean mark = stampedReference.isMarked();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean r1 = stampedReference.compareAndSet(100, 101, mark, !mark);
            mark = stampedReference.isMarked();
            boolean r2 = stampedReference.compareAndSet(101, 100, mark, !mark);
            System.out.println(r1);
            System.out.println(r2);
        });

        Thread t2 = new Thread(() -> {
            boolean mark = stampedReference.isMarked();
            System.out.println(stampedReference.isMarked()+".");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(stampedReference.isMarked()+".");
            boolean r = stampedReference.compareAndSet(100, 101, mark, !mark);
            System.out.println(":" + r);
        });

        t1.start();
        t2.start();
    }
}
