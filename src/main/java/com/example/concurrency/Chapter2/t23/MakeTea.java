package com.example.concurrency.Chapter2.t23;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 泡茶
 * 华罗庚 烧水泡茶最优工序
 * 线程 T1 负责洗水壶、烧开水、泡茶
 * 线程 T2 负责洗茶壶、洗茶叶、拿茶叶
 * T1 执行泡茶前需要等待 T2 完成拿茶叶的任务
 */
public class MakeTea {

    public static void main(String[] args) {
        MakeTea tea = new MakeTea();
        tea.exeDrinkTea();
    }

    /**
     * 喝茶
     */
    void exeDrinkTea() {
        try {
            // 创建任务
            FutureTask<String> ft2 = new FutureTask<>(new T2Task());
            FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));
            // 创建线程
            new Thread(ft1).start();
            new Thread(ft2).start();
            // 等待任务结束
            System.out.println("喝茶-" + ft1.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // T1 任务：洗水壶、烧开水、泡茶
    class T1Task implements Callable<String> {
        FutureTask<String> ft2;

        T1Task(FutureTask<String> ft2) {
            this.ft2 = ft2;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1:洗水壶...");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T1:烧开水...");
            TimeUnit.SECONDS.sleep(15);
            System.out.println("T1:烧完开水...");

            String ftr = ft2.get();
            System.out.println("T1:拿到茶叶:" + ftr);

            System.out.println("T1:泡茶...");
            return "上茶:" + ftr;
        }
    }

    // T2 任务：洗茶壶、洗茶叶、拿茶叶
    class T2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2:洗茶壶...");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T2:洗茶杯...");
            TimeUnit.SECONDS.sleep(2);

            System.out.println("T2:拿茶叶...");
            TimeUnit.SECONDS.sleep(1);

            return " 龙井 ";
        }
    }
}
