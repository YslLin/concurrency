package com.example.concurrency.extension.t05;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源管理类
 */
public class Allocator {
    private Allocator() {
    }

    private List<Account> locks = new ArrayList<>();

    // 一次性申请资源
    public synchronized void apply(Account src, Account tag) {
        // 资源被占用 则 等待
        while (locks.contains(src) || locks.contains(tag)) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 通过资源申请
        locks.add(src);
        locks.add(tag);
    }

    // 释放占用资源 并 通知全部堵塞线程
    public synchronized void release(Account src, Account tag) {
        locks.remove(src);
        locks.remove(tag);
        this.notifyAll();
    }

    /**
     * 单例锁
     */
    static class AllocatorSingle {
        public static Allocator install = new Allocator();
    }

    public static Allocator getInstance() {
        return AllocatorSingle.install;
    }
}
