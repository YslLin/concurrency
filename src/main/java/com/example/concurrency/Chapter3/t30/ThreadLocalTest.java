package com.example.concurrency.Chapter3.t30;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程本地储存模式 - ThreadLocal
 * 本质上是一种避免共享的方案
 * 如果你需要在并发场景中使用一个线程不安全的工具类，最简单的方案就是避免共享。
 * 避免共享有两种方案：
 * 1.将工具类作为局部变量使用
 * 2.使用线程本地存储模式
 * 使用 ThreadLocal 需要注意，可能导致内存泄漏，使用结束后记得移除 threadLocal.remove();（也有人说不用显示调用 remove，如果遇到key == null的情况，会对value设置为null。）
 *
 * ThreadLocal 与 局部变量的区别是：ThreadLocal = 线程数，局部变量 = 调用量
 * 在高并发情况下，使用局部变量会频繁创建对象，使用 ThreadLocal 是针对每个线程创建一个对象
 *
 * 异步编程应该慎用 ThreadLocal。因为 ThreadLocal 是线程封闭的
 * ThreadLocal 会被 gc 回收，如果 gc 发生在 set ~ get 之间，get 取不到值咋整？
 */
public class ThreadLocalTest {

    public static void main(String[] args) {
        ThreadLocalTest tlt = new ThreadLocalTest();
        tlt.exeThreadId();
    }
    class ThreadId {
        final AtomicLong nextId = new AtomicLong(0);
        final ThreadLocal<Long> tl = ThreadLocal.withInitial(() -> nextId.getAndIncrement());
        final InheritableThreadLocal<String> itl = new InheritableThreadLocal<>();

        long get() {
            return tl.get();
        }

        void setITL(String v){
            itl.set(v);
        }

        String getITL() {
            return itl.get();
        }
    }

    void exeThreadId() {
        ThreadId ti = new ThreadId();
        new Thread(() -> {
            System.out.println(ti.get());
        }).start();
        Utils.sleep(1);
        new Thread(() -> {
            System.out.println(ti.get());
            System.out.println(ti.get());
        }).start();
        Utils.sleep(1);
        new Thread(() -> {
            System.out.println(ti.get());
            System.out.println(ti.get());
            System.out.println(ti.get());
        }).start();

        // 新对象会重置 ThreadLocal ，所以需要使用 static 声明方式？
        Utils.sleep(1);
        new Thread(() -> {
            ThreadId ti1 = new ThreadId();
            System.out.println("新实例-"+ti1.get());
        }).start();

        // 子线程获取的值与父线程不同
        Utils.sleep(1);
        new Thread(() -> {
            System.out.println("父线程-"+ti.get());
            new Thread(() -> {
                System.out.println("子线程-"+ti.get());
            }).start();
            Utils.sleep(1);
            new Thread(() -> {
                System.out.println("子线程-"+ti.get());
            }).start();
        }).start();

        // InheritableThreadLocal 子线程继承父线程变量
        // 不建议使用 InheritableThreadLocal 不只是可能导致内存泄漏，线程池中线程的创建是动态的，很容易导致继承关系错乱，最终业务逻辑计算错误。
        Utils.sleep(2);
        ti.setITL("主线程的变量");
        new Thread(() -> {
            System.out.println("父线程-"+ti.getITL());
            ti.setITL("父线程的变量");
            new Thread(() -> {
                System.out.println("子线程-"+ti.getITL());
            }).start();
        }).start();
    }
}
