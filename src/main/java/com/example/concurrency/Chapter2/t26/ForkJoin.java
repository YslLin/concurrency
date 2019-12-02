package com.example.concurrency.Chapter2.t26;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join 分治任务模型
 * 斐波那契数列
 * 对于简单的并行任务，你可以通过“线程池 +Future”的方案来解决；
 * 如果任务之间有聚合关系，无论是 AND 聚合还是 OR 聚合，都可以通过 CompletableFuture 来解决；
 * 而批量的并行任务，则可以通过 CompletionService 来解决。
 * <p>
 * Fork/Join 并行计算框架主要解决的是分治任务。分治的核心思想是“分而治之”：
 * 将一个大的任务拆分成小的子任务去解决，然后再把子任务的结果聚合起来从而得到最终结果。
 */
public class ForkJoin {

    public static void main(String[] args) {
        ForkJoin forkJoin = new ForkJoin();
        forkJoin.exeFibonacci();
    }

    /**
     * 斐波那契计算
     * 1,1,2,3,5,8
     */
    void exeFibonacci() {
        // 创建分治任务线程池
        // 默认即是CPU核数
        ForkJoinPool fjp = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        // 创建分治任务
        Fibonacci fib = new Fibonacci(6);
        // 启动分治任务
        Integer result = fjp.invoke(fib);
        // 输出结果
        System.out.println(result);
    }

    // 递归任务
    class Fibonacci extends RecursiveTask<Integer> {
        final int n;

        Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) return n;
            Fibonacci f1 = new Fibonacci(n - 1);
            // 创建子任务
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            // 等待子线程任务结果，合并结果
            // f2.compute 会比 f1.fork 执行快 因为fork需要创建资源，仅仅说的是一层
            return f2.compute() + f1.join();
        }
    }
}
