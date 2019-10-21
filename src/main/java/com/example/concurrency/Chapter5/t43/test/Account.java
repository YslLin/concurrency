package com.example.concurrency.Chapter5.t43.test;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.*;

/**
 * 转账并发测试
 * 没有锁的情况下转账结果测试
 */
public class Account {
    private int balance;

    public Account(int balance) {
        this.balance = balance;
    }

    // 转账
    void transfer(Account target, int amt) {
        if (this.balance > amt) {
            this.balance -= amt;
            target.balance += amt;
        }
    }

    /**
     * 不使用事务会导致转账出现并发问题
     * B 账号的金额会出现被线程计算覆盖问题：
     * A: 100、B: 300、C: 300。
     * A: 100、B: 100、C: 300。
     * @param args
     */
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);
        Account a = new Account(200);
        Account b = new Account(200);
        Account c = new Account(200);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
            // 预期 A: 100、B: 200、C: 300。
            String str = String.format("A: %d、B: %d、C: %d。", a.balance, b.balance, c.balance);
            if (!str.equals("A: 100、B: 200、C: 300。")) {
                System.out.println(str);
            }
            a.balance = 200;
            b.balance = 200;
            c.balance = 200;
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
        es.shutdown();
    }
}
