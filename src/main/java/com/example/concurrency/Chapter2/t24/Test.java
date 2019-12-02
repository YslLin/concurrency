package com.example.concurrency.Chapter2.t24;

import com.example.concurrency.utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CompletableFuture 测试异步逻辑
 */
public class Test {
    ExecutorService es = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        Test t = new Test();
        t.exeTest();
    }

    void exeTest(){

        // 创建异步执行方法
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(()->{
            Utils.sleep(1);
           return " cf1 " ;
        }, es);
        cf1.thenAcceptAsync(System.out::println);

        // cf1 与 cf2 是并行关系
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(()->{
            Utils.sleep(1);
            return " cf2 " ;
        }, es);
        cf2.thenAcceptAsync(System.out::println);

        // AND 聚合关系
        CompletableFuture<String> cf3 = cf1.thenCombineAsync(cf2, (v1, v2)->{
            Utils.sleep(2);
            return v1 + v2 + " cf3 ";
        }, es);
        cf3.thenAcceptAsync(System.out::println);

        // cf3 与 cf4 是并行关系
        CompletableFuture<String> cf4 = cf2.thenCombineAsync(cf1, (v1, v2)->{
            Utils.sleep(4);
            return v1 + v2 + " cf4 ";
        }, es);
        cf4.thenAcceptAsync(System.out::println);

        // OR 聚合关系
        CompletableFuture<String> cf5 = cf3.applyToEitherAsync(cf4, (v1)->{
            Utils.sleep(1);
            return v1 + " cf5 ";
        }, es);
        cf5.thenAcceptAsync(System.out::println);

        // 串行关系
        CompletableFuture<String> cf6 = cf5.thenApplyAsync((v1)->{
            Utils.sleep(1);
            return  v1 + " cf6 ";
        }, es);

        try {
            // 主线程阻塞获取结果
            System.out.println(cf6.get());
            es.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
