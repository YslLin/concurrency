package com.example.concurrency.extension.t05;

import java.util.concurrent.CountDownLatch;

/**
 * 测试账户转账单例锁
 * CountDownLatch 倒计时器
 */
public class Test1 {

    public static void main(String[] args) {
        Account src = new Account(10000);
        Account target = new Account(0);
        CountDownLatch countDownLatch = new CountDownLatch(9999);
        long timer = System.currentTimeMillis(); //  获取当前系统时间(毫秒)
        for (int i = 0; i < 9999; i++) {
            new Thread(() -> {
                try {
                    src.transactionToTarget(target, 1);
                } finally {
                    // 放在 finally 避免异常后导致 countDown 不执行
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("src = " + src.getBanalce());
        System.out.println("target = " + target.getBanalce());
        System.out.println("延迟时间：" + (System.currentTimeMillis() - timer) + "毫秒");
    }
}
