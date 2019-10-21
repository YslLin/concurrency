package com.example.concurrency.Chapter5.t43;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 软件事务内存 STM (Software Transactional Memory)
 * 转账系统
 * 事务提交方式
 * 开发中需要事务可以使用 第三方类库 Multiverse
 * https://github.com/pveentjer/Multiverse
 */
public class Main {

    /**
     * 使用事务方式转账
     * 永远不会输出错误日志：
     * A: 100、B: 300、C: 300。
     * A: 100、B: 100、C: 300。
     */
    public static void main(String[] args) {
        Account a = new Account(200);
        Account b = new Account(200);
        Account c = new Account(200);
        AtomicLong star = new AtomicLong(System.currentTimeMillis());
        ExecutorService es = Executors.newFixedThreadPool(4);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
            // 预期 A: 100、B: 200、C: 300。
            String str = String.format("A: %d、B: %d、C: %d。", a.getBalance(), b.getBalance(), c.getBalance());
            if (!str.equals("A: 100、B: 200、C: 300。")) {
                System.out.println(str);
            }
            a.setBalance(200);
            b.setBalance(200);
            c.setBalance(200);
            long end = System.currentTimeMillis();
            if (end - star.get() > 3000_0) {
                System.out.println("当前版本号：" + STMTxn.getTxnId());
                star.set(end);
            }
        });
        es.execute(() -> {
            while (true) {
                try {
                    a.transfer(b, 100);
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        es.execute(() -> {
            while (true) {
                try {
                    b.transfer(c, 100);
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
