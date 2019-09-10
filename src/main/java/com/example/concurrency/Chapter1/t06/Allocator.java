package com.example.concurrency.Chapter1.t06;

import java.util.List;

/**
 * ‘等待通知’机制优化‘循环等待’
 */
public class Allocator {
    private List<Object> als;

    // 一次性申请所有资源
    synchronized void apply(Object from, Object to) {
        // 如果目标资源已被占用
        // 阻塞线程 并 等待 notify() 通知
        // 通知后重新校验 目标资源是否已被占用
        while (als.contains(from) || als.contains(to)) {
            try {
                // 阻塞线程
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        als.add(from);
        als.add(to);
    }

    // 归还资源
    synchronized void free(Object from, Object to){
        als.remove(from);
        als.remove(to);
        // 通知阻塞线程
        notifyAll();
    }
}
