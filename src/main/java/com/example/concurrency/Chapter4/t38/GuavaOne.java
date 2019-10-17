package com.example.concurrency.Chapter4.t38;

import java.util.concurrent.TimeUnit;

/**
 * Guava 令牌桶算法实现
 * 单个容量令牌算法
 */
public class GuavaOne {
    // 下一个令牌产生时间
    long next = System.nanoTime();
    // 发放令牌间隔：纳秒
    long interval = 1000_000_000;

    // 预占令牌，返回能够获取令牌的时间
    synchronized long reserve(long now) {
        // 如果 请求时间在下一个令牌产生时间之后
        // 则 重新计算下一个令牌产生时间
        // 即 获取令牌时间 5 秒，下个令牌时间 3 秒，那么 将下个令牌时间改为 5 秒， 4 秒产生的令牌没有线程使用便跳过了
        if (now > next) {
            // 将下一个令牌产生时间重置为当前时间
            next = now;
        }
        // 能够获取令牌的时间
        long at = next;
        // 设置下个令牌产生时间
        next += interval;
        // 返回线程需要等待的时间
        return Math.max(at, 0);
    }

    // 申请令牌
    void acquire() {
        // 申请令牌的时间
        long now = System.nanoTime();
        // 预占令牌时间
        long at = reserve(now);
        // 需要等待的时间
        long waitTime = Math.max(at - now, 0);
        // 是否需要等待
        if (waitTime > 0) {
            try {
                // sleep 睡眠 不会释放锁
                TimeUnit.NANOSECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试 每秒容量为一个的限流器简单实现
     */
    public static void main(String[] args) {
        // 执行时间：纳秒
        long prev = System.nanoTime();
        GuavaOne g = new GuavaOne();
        for (int i = 0; i < 10; i++) {
            g.acquire();
            long cur = System.nanoTime();
            System.out.println(String.format("%d ms.", (cur - prev)/ 1000_000));
            prev = cur;
        }
    }
}
