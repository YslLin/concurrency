package com.example.concurrency.Chapter2.t24;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture 异步编程的优势
 * 1.无需手工维护线程，给任务分配线程工作也不需要我们关注；
 * 2.语义更清晰，由于支持链式编程方式，所以相对清晰，例如 f3 = f1.thenCombine(f2, ()->{})
 * 3.代码更简练可以专注于业务逻辑。
 */
public class Completable {

    public static void main(String[] args) {
        Completable completable = new Completable();
//        completable.makeTea();
//        completable.createCF();
//        completable.serialExec();
//        completable.ANDConvergence();
//        completable.ORConvergence();
        completable.handException();
    }

    /**
     * 创建 CompletableFuture 对象主要是 4 个静态方法
     * // 使用默认线程池
     * static CompletableFuture<Void> runAsync(Runnable runnable)
     * static <U> CompletableFuture <U> supplyAsync(Supplier <U> supplier)
     * // 可以指定线程池
     * static CompletableFuture<Void> runAsync (Runnable runnable, Executor executor)
     * static <U> CompletableFuture <U> supplyAsync(Supplier <U> supplier, Executor executor)
     * <p>
     * Runnable 接口的 run() 方法没有返回值, Supplier 接口的 get() 方法是有返回值的。
     * 后两个方法可以指定线程池参数
     * 默认情况下 CompletableFuture 会使用*公共*的 ForkJoinPool 线程池，这个线程池默认创建线程数是CPU的合数
     * 所有 CompletableFuture 共享一个线程池， 一旦有执行一些很慢的 I/O 操作，就会导致线程都阻塞在 I/O 操作上，从而导致线程饥饿
     * 所以 强烈建议你根据不同的业务类型创建不同的线程池，避免互相干扰。
     */
    void createCF() {
        CompletableFuture.runAsync(() -> System.out.println("1111"));
        CompletableFuture.supplyAsync(() -> "1");
        CompletableFuture.runAsync(() -> System.out.println("2222"), Executors.newFixedThreadPool(1));
        CompletableFuture.supplyAsync(() -> "2", Executors.newFixedThreadPool(1));
    }

    /**
     * 串行关系
     * 指的是依赖的任务只要有一个完成就可以执行当前任务
     * CompletionStage 接口里面描述串行关系，主要是 thenApply、thenAccept、thenRun 和 thenCompose 这四个系列的接口。
     * thenApply 方法既能接收参数也支持返回值，参数 fn 接口类型 Function<T,R>
     * thenAccept 方法支持参数，但不支持返回值，参数 consumer 接口类型 Consumer<T>
     * thenRun 方法既不能接收参数也不支持返回值，参数 action 接口类型 Runnable
     * thenCompose 方法接收两个参数返回
     * Async 代表的是异步执行fn、consumer、action
     * <p>
     * CompletionStage<R> thenApply(fn);
     * CompletionStage<R> thenApplyAsync(fn);
     * CompletionStage<Void> thenAccept(consumer);
     * CompletionStage<Void> thenAcceptAsync(consumer);
     * CompletionStage<Void> thenRun(action);
     * CompletionStage<Void> thenRunAsync(action);
     * CompletionStage<R> thenCompose(fn);
     * CompletionStage<R> thenComposeAsync(fn);
     */
    void serialExec() {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> "Hello World");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> " QQ");
        // 当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化。
        f.thenApply(s -> s + " WX")
                // thenCompose 方法允许你对两个 CompletionStage 进行流水线操作，第一个操作完成时，将其结果作为参数传递给第二个操作。
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s))
                // 接收任务的处理结果，并消费处理，无返回结果。
                .thenAccept(System.out::println)
                // 跟 thenAccept 方法不一样的是，不关心任务的处理结果。只要上面的任务执行完成，就开始执行 thenAccept 。
                .thenRun(() -> System.out.println("thenRun 不支持参数与返回值"));

        // 非异步处理与异步处理 会在输出 3 之后再输出第二次的 Hello World
        System.out.println(1);
        f.thenAccept(System.out::println);
        System.out.println(2);
        f.thenAcceptAsync(System.out::println);
        System.out.println(3);
        this.sleep(3);
    }



    /**
     * AND 汇聚关系
     * 指的是依赖的任务全部完成后才开始执行当前任务
     * CompletionStage 接口里面描述 AND 汇聚关系，主要是 thenCombine、thenAcceptBoth 和 runAfterBoth 系列的接口。
     * 这些接口的区别也是 fn、consumer、action 这三个核心参数不同
     * CompletionStage<R> thenCombine(other, fn);
     * CompletionStage<R> thenCombineAsync(other, fn);
     * CompletionStage<Void> thenAcceptBoth(other, consumer);
     * CompletionStage<Void> thenAcceptBothAsync(other, consumer);
     * CompletionStage<Void> runAfterBoth(other, action);
     * CompletionStage<Void> runAfterBothAsync(other, action);
     */
    void ANDConvergence() {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> "Hello World");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> " QQ");
        // thenCombine 支持参数与返回值
        f.thenCombine(f1, (s, s2) -> s + s2).thenAccept(System.out::println);
        // thenAcceptBoth 支持参数不支持返回值
        f.thenAcceptBoth(f1, (s, s2) -> System.out.println(s + s2));
        // runAfterBoth 不支持参数与返回值
        f.runAfterBoth(f1, () -> System.out.println("runAfterBoth 不支持参数与返回值"));
    }

    /**
     * OR 汇聚关系
     * 指的是依赖的任务只要有一个完成就可以执行当前任务
     * CompletionStage 接口里面描述 OR 汇聚关系，主要是 applyToEither、acceptEither 和 runAfterEither 系列的接口。
     * 这些接口的区别也是 fn、consumer、action 这三个核心参数不同
     * CompletionStage applyToEither(other, fn);
     * CompletionStage applyToEitherAsync(other, fn);
     * CompletionStage acceptEither(other, consumer);
     * CompletionStage acceptEitherAsync(other, consumer);
     * CompletionStage runAfterEither(other, action);
     * CompletionStage runAfterEitherAsync(other, action);
     */
    void ORConvergence() {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> "Hello World");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> " QQ");
        // applyToEither 支持参数与返回值
        f.applyToEither(f1, (s) -> s + " One").thenAccept(System.out::println);
        // acceptEither 支持参数不支持返回值
        f.acceptEither(f1, (s) -> System.out.println(s + " Two"));
        // runAfterEither 不支持参数与返回值
        f.runAfterEither(f1, () -> System.out.println("runAfterEither 不支持参数与返回值"));

        // 其它 OR 汇聚关系示例
        CompletableFuture<Integer> t1 = CompletableFuture.supplyAsync(() -> {
            int r = (int) (Math.random() * 10);
            this.sleep(r);
            return r;
        });
        CompletableFuture<Integer> t2 = CompletableFuture.supplyAsync(() -> {
            int r = (int) (Math.random() * 10);
            this.sleep(r);
            return r;
        });
        CompletableFuture<Void> t3 = t1.acceptEither(t2, System.out::println);
        t3.join();
    }

    /**
     * 异常处理
     * 异常处理都支持链式编程方式
     * exceptionally 方法类似于 catch{} 异常处理
     * whenComplete 方法类似于 finally{} 无论是否异常都会执行回调函数
     * handle 方法类似于 finally{} 与 whenComplete 的区别在于，handle 支持返回结果
     * CompletionStage exceptionally(fn);
     * CompletionStage<R> whenComplete(consumer);
     * CompletionStage<R> whenCompleteAsync(consumer);
     * CompletionStage<R> handle(fn);
     * CompletionStage<R> handleAsync(fn);
     */
    void handException(){
        CompletableFuture<Integer> f0 = CompletableFuture.supplyAsync(()->7/0).thenApply(r->r*10);
        CompletableFuture<Integer> f1 = f0.exceptionally(e-> -1);
        CompletableFuture<Integer> f2 = f0.handle((r, e)-> -2);
        CompletableFuture<Integer> f3 = f0.whenComplete((r, e)-> System.out.println(e.toString()));
        try {
            System.out.println(f1.get());
            System.out.println(f2.get());
            System.out.println(f3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    void makeTea() {
        // 任务 1：洗水壶 -> 烧开水
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1:洗水壶...");
            sleep(1);

            System.out.println("T1:烧开水...");
            sleep(15);
        });

        // 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2:洗茶壶...");
            sleep(1);

            System.out.println("T2:洗茶杯...");
            sleep(2);

            System.out.println("T2:拿茶叶...");
            sleep(1);
            return " 龙井 ";
        });

        // 任务 3：等待任务 1 和任务 2 完成后执行 -> 泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf) -> {
            System.out.println("T1:拿到茶叶:" + tf);

            System.out.println("T1:泡茶...");
            return "上茶:" + tf;
        });

        // 等待任务 3 执行结果
        System.out.println(f3.join());
    }

    void sleep(long time) {
        sleep(time, TimeUnit.SECONDS);
    }

    void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
