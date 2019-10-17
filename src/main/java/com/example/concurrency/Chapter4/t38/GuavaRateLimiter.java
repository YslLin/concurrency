package com.example.concurrency.Chapter4.t38;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Guava 是 Google 开源的 java 类库
 * 提供了一个限流器工具类 RateLimiter
 * RateLimiter 使用方法如下：
 * 2 个请求 / 秒等价于 1 个请求 /500 毫秒。
 */
public class GuavaRateLimiter {
    // 限流器流速：每秒 2 个请求
    RateLimiter limiter = RateLimiter.create(2);
    // 线程池
    ExecutorService es = Executors.newFixedThreadPool(1);
    // 执行时间：纳秒
    long prev = System.nanoTime();

    public static void main(String[] args) {
        GuavaRateLimiter g = new GuavaRateLimiter();
        g.exe();
    }

    void exe(){
        // 测试执行 20 次
        for (int i = 0; i < 20; i++) {
            // 限流器限流
            limiter.acquire();
            // 提交任务异步执行
            es.execute(()->{
                long cur = System.nanoTime();
                // 打印间隔时间：毫秒
                // System.nanoTime() ：ns 纳秒
                // 1纳秒 = 0.000001 毫秒
                // 1纳秒 = 0.00000 0001 秒
                // 1000_000 等效于 1000000，只是这样写可读性强
                System.out.println(String.format("%d ms.", (cur - prev)/ 1000_000));
                prev = cur;
            });
        }
        es.shutdown();
    }
}
