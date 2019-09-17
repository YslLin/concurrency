package com.example.concurrency.Chapter2.t25;

import com.example.concurrency.utils.Utils;

import java.util.*;
import java.util.concurrent.*;

/**
 * 当需要批量提交异步任务的时候建议你使用 CompletionService。
 * CompletionService 将线程池 Executor 和阻塞队列 BlockingQueue 的功能融合在了一起，能够让批量异步任务的管理更简单。
 * 除此之外，CompletionService 能够让异步任务的执行结果有序化，先执行完的先进入阻塞队列，利用这个特性，你可以轻松实现后续处理的有序性，避免无谓的等待，同时还可以快速实现诸如 Forking Cluster 这样的需求。
 * CompletionService 的实现类 ExecutorCompletionService，需要你自己创建线程池
 * 好处是你可以让多个 ExecutorCompletionService 的线程池隔离，这种隔离性能避免几个特别耗时的任务拖垮整个应用的风险。
 */
public class Completion {
    public static void main(String[] args) {
        Completion completion = new Completion();
//        completion.createCS();
//        completion.inquiryPrice();
//        completion.exeAPI();
        completion.forking();
    }

    /**
     * 创建 CompletionService 的两个构造方法
     * ExecutorCompletionService(Executor executor)
     * ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> completionQueue)
     * 两个方法都需要传入一个线程池。
     * 如果不指定队列，那么默认会使用无界的 LinkedBlockingQueue。任务执行结果 Future 对象会加入到该队列中。
     */
    void createCS() {
        // 创建线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 创建 CompletionService
        CompletionService cs1 = new ExecutorCompletionService(executor);
        // 创建队列
        ArrayBlockingQueue<Future<String>> arrayBlockingQueue = new ArrayBlockingQueue<>(3);
        // 创建指定队列的 CompletionService
        CompletionService<String> cs2 = new ExecutorCompletionService<>(executor, arrayBlockingQueue);
    }

    /**
     * CompletionService 接口的 5 个方法
     * submit 方法提交任务
     * submit(Runnable task, V result) 方法类似于 ThreadPoolExecutor 的 submit (Runnable task, T result)
     * take 方法从阻塞队列中获取并移除一个元素，阻塞方法
     * poll 方法与 take 的区别是 如果阻塞队列为空，poll 会直接返回 null 值
     * poll(long timeout, TimeUnit unit) 方法支持超时时间，如果等待了 timeout 时间，阻塞队列还是空的，返回 null 值
     * Future<V> submit(Callable<V> task);
     * Future<V> submit(Runnable task, V result);
     * Future<V> take() throws InterruptedException;
     * Future<V> poll();
     * Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
     */
    void exeAPI() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletionService cs = new ExecutorCompletionService(executor);
        // submit(Callable<V> task);
        cs.submit(() -> {
            Utils.sleep(1);
            return "submit(Callable<V> task);";
        });
        // submit(Runnable task, V result);
        Map<String, String> map = new HashMap<>();
        map.put("key", "Hello");
        Task task = new Task(map);
        cs.submit(task, map);
        // submit(Callable<V> task);
        cs.submit(() -> {
            Utils.sleep(3);
            return "poll timeout";
        });
        executor.shutdown();
        try {
            System.out.println(cs.take().get());
            Utils.sleep(3);
            System.out.println(cs.poll().get());
            System.out.println(cs.poll(1, TimeUnit.SECONDS));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现集群容错 Forking Cluster
     * 并行调用多个查询服务，只要一个成功即可返回结果。通常用于实时性要求较高的读操作，但需要浪费更多服务资源。
     * 例如：为了保证该服务的高可用和性能，你可以并行地调用 3 个地图服务商的 API，然后只要有 1 个正确返回了结果 r，那么地址转坐标这个服务就可以直接返回 r 了。
     * 利用 CompletionService 可以快速实现 Forking 这种集群模式
     */

    void forking() {
        // 创建 CUP 核数线程池
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CompletionService<String> cs = new ExecutorCompletionService<>(executor);
        // 用于保存 Future 对象
        List<Future<String>> futures = new ArrayList<>();
        // 提交异步任务，并保存 future 到 futures
        futures.add(cs.submit(this::getPriceByS1));
        futures.add(cs.submit(this::getPriceByS2));
        futures.add(cs.submit(this::getPriceByS3));
        // 获取最快返回的任务执行结果
        String r = null;
        try {
            // 只要有一个成功返回，则 break
            for (int i = 0; i < 3; i++) {
                r = cs.take().get();
                if (r != null) {
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            // 容错异常日志 此处会报 睡眠中断异常 sleep interrupted ，因为会被终止
        } finally {
            // 取消所有任务
            for (Future<String> f: futures) {
                f.cancel(true);
            }
        }
        // 返回结果
        System.out.println(r);
    }

    /**
     * 询价系统 优化
     * CompletionService 的内部维护了一个阻塞队列，当任务执行结束就把任务的执行结果 Future 对象加入到阻塞队列中
     * 通过阻塞队列保证S1、S2、S3查询价格任务，哪个先结束，先保存哪个，防止S2需要等待S1查询后才能保存的问题
     */
    void inquiryPrice() {
        Date start = new Date();
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建 CompletionService
        CompletionService cs = new ExecutorCompletionService(executor);
        cs.submit(this::getPriceByS1);
        cs.submit(this::getPriceByS2);
        cs.submit(this::getPriceByS3);
        for (int i = 0; i < 3; i++) {
            try {
                // CompletionService 的内部维护了一个阻塞队列
                // 当任务执行结束就把任务的执行结果 Future 对象加入到阻塞队列中
                Future<String> future = cs.take();
                String str = future.get();
                executor.execute(() -> save(str));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
        // 线程池所有任务执行结束
        while (true) {
            // isTerminated 若关闭后所有任务都已完成，则返回true。
            if (executor.isTerminated()) {
                System.out.println("询价结束：" + (new Date().getTime() - start.getTime()));
                break;
            }
        }
    }

    private String getPriceByS1() {
        Utils.sleep(1);
        return "PriceByS1";
    }

    private String getPriceByS2() {
        Utils.sleep(1);
        return "PriceByS2";
    }

    private String getPriceByS3() {
        Utils.sleep(1);
        return "PriceByS3";
    }

    private void save(String price) {
        try {
            if (price.contains("PriceByS1")) {
                System.out.println("Save PriceByS1");
            } else if (price.contains("PriceByS2")) {
                System.out.println("Save PriceByS2");
            } else if (price.contains("PriceByS3")) {
                System.out.println("Save PriceByS3");
            }
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Task implements Runnable {
        Map<String, String> map;

        Task(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public void run() {
            Utils.sleep(2);
            this.map.put("key", this.map.get("key") + " QQ");
        }
    }
}
