package com.example.concurrency.Chapter3.t35;

import com.example.concurrency.utils.Utils;

/**
 * 两阶段终止模式：优雅地终止线程
 */
public class Proxy {
    boolean started = false;
    // 任务线程
    Thread rptThread;

    public static void main(String[] args) {
        Proxy p = new Proxy();
        p.exeThread();
    }

    void exeThread(){
        Proxy p = new Proxy();
        p.start();
        Utils.sleep(6);
        p.stop();
    }

    // 启动采集功能
    synchronized void start() {
        // 不允许同时启动多个采集线程
        if (started) {
            return;
        }

        started = true;
        rptThread = new Thread(() -> {
            // 线程中断标志位 判断是否需要终止任务
            while (!Thread.currentThread().isInterrupted()) {
                // 模拟执行任务
                System.out.println("执行定时任务");
                try {
                    // 定时执行该任务
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // 捕获到终止异常后需要重新设置终止状态
                    Thread.currentThread().interrupt();
                }
            }
            // 任务终止后恢复到可执行状态
            started = false;
        });
        rptThread.start();
    }

    // 终止任务方法
    synchronized void stop(){
        // 向任务线程发送终止指令
        rptThread.interrupt();
    }
}
