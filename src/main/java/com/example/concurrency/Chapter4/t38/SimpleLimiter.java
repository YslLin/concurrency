package com.example.concurrency.Chapter4.t38;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * Guava 令牌桶算法实现
 * 多个容量令牌算法
 */
public class SimpleLimiter {
    // 当前令牌桶中的令牌数量
    long storedPermits = 0;
    // 令牌桶的容量
    long maxPermits = 3;
    // 下一个令牌产生时间
    long next = System.nanoTime();
    // 发放令牌间隔：纳秒
    long interval = 1000_000_000;

    // 请求时间在下一个令牌产生时间之后
    // 1. 重新计算令牌桶中的令牌数
    // 2. 将下一个令牌发放的时间重置为当前时间
    void resync(long now) {
        if (now > next) {
            // 新生产的令牌数
            long newPermits = (now - next) / interval;
            // 新令牌增加到令牌桶
            storedPermits = Math.min(maxPermits, storedPermits + newPermits);
            // 将下一个令牌产生时间重置为当前时间
            next = now;
        }
    }

    // 预占令牌，返回能够获取令牌的时间
    synchronized long reserve(long now) {
        resync(now);
        // 能够获取令牌的时间
        long at = next;
        // 令牌桶中能提供的令牌数
        long fb = Math.min(1, storedPermits);
        // 令牌净需求：首先减掉令牌桶中的令牌
        long nr = 1 - fb;
        // 重新计算下一个令牌产生时间
        next = next + nr * interval;
        // 重新计算令牌桶中的令牌
        storedPermits -= fb;
        return at;
    }

    // 申请令牌1
    void acquire() {
        // 申请令牌的时间
        long now = System.nanoTime();
        // 预占令牌
        long at = reserve(now);
        // 需等待时间
        long waitTime = Math.max(at - now, 0);
        // 按照条件等待
        if (waitTime > 0) {
            try {
                TimeUnit.NANOSECONDS.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // 执行时间：纳秒
        long prev = System.nanoTime();
        SimpleLimiter sl = new SimpleLimiter();
        for (int i = 0; i < 30; i++) {
            sl.acquire();
            long cur = System.nanoTime();
            System.out.println(String.format("%d ms.", (cur - prev) / 1000_000));
            prev = cur;
            // 每三次 睡眠三秒 等待令牌发放
            if (i % 3 == 0) {
                Utils.sleep(3100, TimeUnit.MILLISECONDS);
            }
        }
    }
}
