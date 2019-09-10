package com.example.concurrency.Chapter2.t22;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池命名工厂
 */
public class NameThreadFactory implements ThreadFactory {

    /**
     * 线程池编号(static修饰)(容器里面所有线程池的数量)
     */
    private static AtomicInteger poolNumber = new AtomicInteger(1);
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     * 线程编号(当前线程池线程的数量)
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 业务名称前缀
     */
    private final String namePrefix;

    /**
     * 重写线程名称 (获取线程池编号、线程编号、线程组)
     */
    public NameThreadFactory(@NotNull String prefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        // 拼接线程前缀
        this.namePrefix = prefix + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
    }

    /**
     * 抽象类 线程工厂 的 抽象方法
     */
    @Override
    public Thread newThread(Runnable r) {
        // 方便dump的时候排查（重写线程名称）
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        // 返回True该线程就是守护线程
        if (t.isDaemon())
            t.setDaemon(false);
        // 线程优先级 CPU 使用权
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
