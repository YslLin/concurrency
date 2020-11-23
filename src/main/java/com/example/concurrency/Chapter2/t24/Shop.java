package com.example.concurrency.Chapter2.t24;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 异步编程与并行流.parallel()比较
 * @Author: YSL
 * @Date: 2020-11-23 13:48
 **/
public class Shop {

    Random random = new Random();

    private String name;

    public Shop(String name) {
        this.name = name;
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public double getPrice() {
        System.out.println(this.name);
        delay();
        return random.nextDouble() * 100;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        Executor executor = new ThreadPoolExecutor(5,13, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        List<Shop> shops = Arrays.asList(new Shop("shop1"),
                new Shop("shop2"),
                new Shop("shop3"),
                new Shop("shop4"),
                new Shop("shop5"),
                new Shop("shop6"),
                new Shop("shop7"),
                new Shop("shop8"),
                new Shop("shop9"),
                new Shop("shop10"),
                new Shop("shop11"),
                new Shop("shop12"),
                new Shop("shop13")
        );

        long start = System.currentTimeMillis();

//        // 方法一：加并行流.parallel()
//        List<String> list = shops.stream().parallel().map(shop -> String.format("%s price is %.2f ", shop.getName(), shop.getPrice()))
//                .collect(Collectors.toList());
//        System.out.println(list);

        // 方法二：CompletableFuture 异步编程
        List<CompletableFuture<String>> list = shops.stream().map(shop ->
                CompletableFuture.supplyAsync(() ->
                        String.format("%s price is %.2f ", shop.getName(), shop.getPrice())
                , executor)
        ).collect(Collectors.toList());
        System.out.println(list.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        System.out.println("耗时:" + (System.currentTimeMillis() - start));

        // 测试 thenCombine 与 thenCombineAsync 的区别，前者使用当前线程执行，后者使用公共线程池。
//        CompletableFuture<List<String>> f1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("111"+Thread.currentThread().getName());
//            return Stream.of("111").collect(Collectors.toList());
//        }, executor);
//        CompletableFuture<List<String>> f2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("222"+Thread.currentThread().getName());
//            return Stream.of("222").collect(Collectors.toList());
//        }, executor);
//        List<String> unionPoints = f1.thenCombine(f2, (v1, v2) -> {
//            System.out.println("333"+Thread.currentThread().getName());
//            v1.addAll(v2);
//            return v1;
//        }).exceptionally(e -> {
//            System.out.println("电子围栏合并异常"+Thread.currentThread().getName());
//            e.printStackTrace();
//            return new ArrayList<>();
//        }).join();
//        System.out.println("unionPoints");
//        System.out.println("耗时:" + (System.currentTimeMillis() - start));
    }

}
