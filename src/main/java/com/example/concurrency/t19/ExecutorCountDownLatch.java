package com.example.concurrency.t19;

import com.example.concurrency.t19.tool.Reconciliation;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统性能优化
 * 查询订单、派送单，然后执行对账，最后将写入差异库。
 * 3.线程池版-并行订单查询与派送单查询
 * 版本 2 中 while 里面每次都会创建新线程，而创建线程是个耗时的操作。
 * 可以利用线程池 Executor 优化这个问题
 * 但是使用线程池带来的问题是，线程是重复利用的，根本不会退出，所以 join 方法已经失效了。
 * 可以使用计数器 CountDownLatch 解决同步问题
 */
public class ExecutorCountDownLatch {

    public static void main(String[] args) {
        Reconciliation r = new Reconciliation();
        // 创建 2 个线程的线程池
        Executor executor = Executors.newFixedThreadPool(2);
        // 存在未对账订单
        while (true) {
            // 开始时间
            Date s = new Date();
            // 计数器初始化为2
            CountDownLatch countDownLatch = new CountDownLatch(2);
            // 查询未对账订单
            AtomicReference<Date> pos = new AtomicReference<>();
            executor.execute(() -> {
                pos.set(r.getPOrders());
                // 计数器减一
                countDownLatch.countDown();
            });

            // 查询派送单
            AtomicReference<Date> dos = new AtomicReference<>();
            executor.execute(() -> {
                dos.set(r.getDOrders());
                // 计数器减一
                countDownLatch.countDown();
            });

            try {
                // 等待两个查询结束 计数器为0
                countDownLatch.await();
                // 执行对账操作
                long diff = r.check(pos.get(), dos.get());
                // 差异写入差异库
                r.save(diff, s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
