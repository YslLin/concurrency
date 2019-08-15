package com.example.concurrency.t22;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 自定义 简化线程池
 */
public class MyThreadPool {
    BlockingQueue<Runnable> workQueue;
    List<WorkerThread> threads = new ArrayList<>();

    MyThreadPool(int poolSize, BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        for (int i = 0; i < poolSize; i++) {
            WorkerThread workerThread = new WorkerThread();
            workerThread.start();
            threads.add(workerThread);
        }
    }

    class WorkerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = workQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void execute(Runnable command){
        try {
            workQueue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建线程池 2个线程的有界阻塞队列
        MyThreadPool pool = new MyThreadPool(2,new LinkedBlockingQueue<>(2));

        for (int i = 0; i < 20; i++) {
            pool.execute(pool.getTasks());
        }
    }

    /**
     * 临时任务
     */
    class Tasks implements Runnable {
        @Override
        public void run() {
            try {
                int r = (int) (Math.random() * 10);
                TimeUnit.SECONDS.sleep(r);
                System.out.println(Thread.currentThread().getName() + " is over");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Tasks getTasks(){
        return new Tasks();
    }
}
