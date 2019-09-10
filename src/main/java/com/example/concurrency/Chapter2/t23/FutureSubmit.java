package com.example.concurrency.Chapter2.t23;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 获取线程任务执行结果 Future
 */
public class FutureSubmit {

    public static void main(String[] args) {
        FutureSubmit fs = new FutureSubmit();
//        fs.exeSubmit();
//        fs.exeFuture();
        fs.exeFutureTask();
    }

    /**
     * 1.Runnable 没有返回值；
     * 2.Runnable 的 run() 方法异常只能在内部消化，不能往上继续抛
     * Runnable 与 Callable 都可以编写多线程程序;都采用 Thread.start() 启动线程
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                int r = (int) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(r);
                System.out.println("Runnable: " + Thread.currentThread().getName() + " is over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 1.Callable 可以返回执行结果;配合 Future、FutureTask 可以用来获取异步执行的结果
     * 2.Callable 接口的 call() 方法允许抛出异常；
     * Runnable 与 Callable 都可以编写多线程程序;都采用 Thread.start() 启动线程
     */
    Callable<String> callable = new Callable<String>() {
        @Override
        public String call() throws Exception {
            int r = (int) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(r);
            System.out.println("Callable: " + Thread.currentThread().getName() + " is over");
            return "callable: " + r;
        }
    };

    //
    Object x = new Object();

    class Task implements Runnable {
        List<Object> result;

        Task(List<Object> result) {
            this.result = result;
        }

        @Override
        public void run() {
            System.out.println(result.get(0));
            // 可以对 result 进行操作
            result.add(x);
        }
    }

    /**
     * 三种 submit 提交方式
     * // 提交 Runnable 任务
     * Future<?> submit(Runnable task);
     * // 提交 Callable 任务
     * <T> Future<T> submit(Callable<T> task);
     * // 提交 Runnable 任务及结果引用
     * <T> Future<T> submit(Runnable task, T result);
     */
    void exeSubmit() {
        ExecutorService exe = Executors.newFixedThreadPool(3);

        // 提交 Runnable 任务
        // run() 方法没有返回值，所以 submit() 方法返回的 Future 只能用来平判断任务已经结束了
        // 与 Thread.join() 方法相同。
        Future fr = exe.submit(runnable);

        // 提交 Callable 任务
        // call() 方法有返回值，所以 submit() 方法返回的 Future 对象可以通过调用 get() 方法来获取执行结果。
        Future fc = exe.submit(callable);

        /* Runable 返回值 */
        List<Object> result = new ArrayList<>();
        Object a = new Object();
        result.add(a);
        // 提交 Runnable 任务及结果引用 T
        // fro.get() 返回值就是传给 submit() 方法的参数 result
        // 参数 Task 传入了 result 对象 这样Task 的 run() 方法中对 result 进行操作了
        // result 相当于主线程与子线程之间的桥梁 通过它可以共享数据
        Future<List<Object>> fro = exe.submit(new Task(result), result);
        exe.shutdown();
        try {
            System.out.println(fr.get());
            System.out.println(fc.get());
            // 因 get() 方法返回值就是 result
            // 所以下面的等式成立
            List<Object> r = fro.get();
            System.out.println(result == r);
            System.out.println(a == r.get(0));
            System.out.println(x == r.get(1));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Future 接口的5个方法
     */
    void exeFuture() {
        ExecutorService exe = Executors.newFixedThreadPool(1);
        try {
            Future<String> future = exe.submit(callable);
            // 取消执行任务的方法。
//            System.out.println("cancel: " + future.cancel(true));
            // 如果此任务正常完成之前被取消，则返回 true
            System.out.println("isCancelled: " + future.isCancelled());
            // 判断任务是否已结束
            System.out.println("isDone: " + future.isDone());
            // 获取任务执行结果，支持超时机制
            System.out.println("get timeout: " + future.get(7000, TimeUnit.MILLISECONDS));
            // 获取任务执行结果,未完成时则阻塞调用线程
            System.out.println("get: " + future.get());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            exe.shutdown();
        }
    }

    /**
     * FutureTask 工具类
     * 有两个构造函数, 参数与 submit() 方法类似
     * FutureTask(Callable<V> callable);
     * FutureTask(Runnable runnable, V result);
     * FutureTask 实现了 Runnable 和 Future 接口
     * 所以 FutureTask 可以直接提交给 ThreadPoolExecutor 执行，也可以直接被 Thread 执行
     * 同时 也能用来获取任务的执行结果。
     */
    void exeFutureTask(){
        // 创建 FutureTask 工具类
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1+2);
        // 创建线程池并提交任务
        ExecutorService exe = Executors.newFixedThreadPool(1);
        try {
            exe.submit(futureTask);
            System.out.println("ThreadPoolExecutor: " + futureTask.get());
            // 创建线程并启动线程
            new Thread(futureTask).start();
            System.out.println("Thread: " + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            exe.shutdown();
        }
    }

    /**
     * execute 与 submit 方法
     * execute(Runnable command)
     * --在将来的某个时间执行给定的命令。
     * submit(Callable<T> task)
     * --提交值返回任务以执行，并返回代表任务待处理结果的 Future。 未来的get方法将在成功完成后返回任务的结果。
     * submit(Runnable task);
     * --提交一个可运行的任务执行，并返回一个表示该任务的未来。 未来的get方法将返回null 成功完成时。
     * submit(Runnable task, T result)
     * --提交一个可运行的任务执行，并返回一个表示该任务的未来。 未来的get方法将在成功完成后返回给定的结果。
     */
    void exeSubmit1() {
        ExecutorService exec = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 20; i++) {
            Future f = exec.submit(() -> {
                Date s = new Date();
                try {
                    int r = (int) (Math.random() * 10);
                    TimeUnit.SECONDS.sleep(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return Thread.currentThread().getName() + " is over " + (new Date().getTime() - s.getTime());
            });
            try {
                System.out.println("我不会被阻塞");
                System.out.println("get() 会被阻塞"+f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        exec.shutdown();
    }
}
