package com.example.concurrency.Chapter3.t34;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 避免线程死锁
 * 如果提交到相同线程池的任务不是互相独立的，而是有依赖关系的，那么就可能导致线程死锁。
 * 将一个大型计算任务分成两个阶段，第一个阶段的任务会等待第二阶段的子任务完成。而且两个阶段使用的还是同一个线程池。
 */
public class ThreadDeadlock {
    /**
     * 如果你执行下面的这段代码，会发现它永远执行不到最后一行。执行过程中没有任何异常，但是应用已经停止响应了。
     * 因为固定线程池数是 2 ，两个 L1 线程占用后 子线程 L2 没有可使用线程数，导致 L1 死等 L2 任务
     *
     * 解决该死锁问题
     * 方案一：将线程池数调大
     * 方案二：L1阶段任务和L2阶段任务使用各自的线程池
     * 强调：提交到相同线程池中的任务一定是互相独立的，否则就一定要慎重
     */
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(2);
        CountDownLatch l1 = new CountDownLatch(2);
        for (int i = 0; i < 2; i++) {
            System.out.println("L1");
            es.execute(() -> {
                CountDownLatch l2 = new CountDownLatch(2);
                for (int j = 0; j < 2; j++) {
                    es.execute(() -> {
                        System.out.println("L2");
                        l2.countDown();
                    });
                }
                // 等待 L2 阶段任务执行完
                try {
                    l2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                l1.countDown();
            });
        }
        // 等待 L1 阶段任务执行完
        try {
            l1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();
        System.out.println("END;");
    }
}
