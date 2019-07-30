package com.example.concurrency.t19;

import com.example.concurrency.t19.tool.Reconciliation;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统性能优化
 * 查询订单、派送单，然后执行对账，最后将写入差异库。
 * 2.多线程版-并行订单查询与派送单查询
 */
public class ManyAccount {

    public static void main(String[] args) {
        Reconciliation r = new Reconciliation();
        // 存在未对账订单
        while (true) {
            // 开始时间
            Date s = new Date();
            // 查询未对账订单
            AtomicReference<Date> pos = new AtomicReference<>();
            Thread T1 = new Thread(() -> {
                pos.set(r.getPOrders());
            });

            // 查询派送单
            AtomicReference<Date> dos = new AtomicReference<>();
            Thread T2 = new Thread(() -> {
                dos.set(r.getDOrders());
            });
            T1.start();
            T2.start();

            try {
                // 等待两个查询结束
                T1.join();
                T2.join();

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
